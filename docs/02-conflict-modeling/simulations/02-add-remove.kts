/**
 * CRDT Simulation: Concurrent Add/Remove at the same position
 *
 * Scenario:
 * - Start state: [A, B, C]
 * - Node A inserts "X" at index 2 (between B and C)
 * - Node B removes the element at index 2 (B)
 *
 * We demonstrate two policies across three modeling styles:
 * 1) Insert-wins at position  -> keep X even if B is removed
 * 2) Remove-wins at position  -> drop X if it anchored to the removed B
 */

println("=== Concurrent Add/Remove Simulation ===")

// Utility

data class ElementId(val id: String) { override fun toString() = id }

// Initial logical elements with stable IDs
val A = ElementId("A")
val B = ElementId("B")
val C = ElementId("C")

// A helper to pretty print visible sequence
fun visible(list: List<SeqNode>) = list.filter { !it.tombstone }.map { it.value }

// ========================
// Model 1: CvRDT (state-based) with tombstones
// ========================
println("=== CvRDT (state-based) ===")

data class CvItem(
    val id: ElementId,
    val value: String,
    val anchor: ElementId? = null, // where it wants to appear (after anchor)
    val createdBy: String,
    val ts: Long,
)

data class CvRemove(val id: ElementId, val createdBy: String, val ts: Long)

class CvState(private val nodeId: String) {
    // Set-based state: we store items and removes; merge is set-union
    val items = mutableSetOf<CvItem>()
    val removes = mutableSetOf<CvRemove>()

    fun seed(initial: List<Pair<ElementId, String>>) {
        initial.forEachIndexed { idx, (id, v) ->
            items.add(CvItem(id, v, anchor = if (idx == 0) null else initial[idx - 1].first, createdBy = nodeId, ts = idx.toLong()))
        }
    }

    fun insertAfter(anchor: ElementId?, id: ElementId, value: String, ts: Long): CvItem {
        val it = CvItem(id, value, anchor, nodeId, ts)
        items.add(it)
        return it
    }

    fun remove(id: ElementId, ts: Long): CvRemove {
        val r = CvRemove(id, nodeId, ts)
        removes.add(r)
        return r
    }

    fun merge(other: CvState) {
        items.addAll(other.items)
        removes.addAll(other.removes)
    }

    fun materialize(policyInsertWins: Boolean): List<String> {
        // Build linked order using anchors; skip items whose anchor is unknown except head
        val byId = items.associateBy { it.id }
        val tombstoned = removes.map { it.id }.toSet()

        // Collect nodes as a simple chain start->... preserving initial seed order by ts, then others by ts
        val seed = items.filter { it.anchor == null }.sortedBy { it.ts }.toMutableList()
        val rest = items.filter { it.anchor != null }.sortedBy { it.ts }

        val ordered = mutableListOf<CvItem>()
        ordered.addAll(seed)
        // naive placement: for each item, place after the first occurrence of its anchor
        rest.forEach { item ->
            val idx = ordered.indexOfFirst { it.id == item.anchor }
            if (idx >= 0) ordered.add(idx + 1, item) else ordered.add(item) // append if anchor unknown
        }

        // Apply visibility per policy
        val visible = ordered.filter { item ->
            val isRemoved = item.id in tombstoned
            val anchorRemoved = item.anchor != null && item.anchor in tombstoned
            when {
                isRemoved -> false // never show removed item
                anchorRemoved && !policyInsertWins -> false // drop insert if its anchor removed
                else -> true
            }
        }
        return visible.map { it.value }
    }
}

run {
    println("-- Policy: Insert-wins")
    val a = CvState("A").apply { seed(listOf(A to "A", B to "B", C to "C")) }
    val b = CvState("B").apply { /* empty seed, will merge */ }

    // Concurrent ops with same logical timestamp 100
    // Node A inserts X after B (anchor=B)
    a.insertAfter(anchor = B, id = ElementId("X"), value = "X", ts = 100)
    // Node B removes B
    b.remove(id = B, ts = 100)

    // Merge both ways (state-based union)
    a.merge(b); b.merge(a)

    println("A: " + a.materialize(policyInsertWins = true))
    println("B: " + b.materialize(policyInsertWins = true))

    println("-- Policy: Remove-wins")
    println("A: " + a.materialize(policyInsertWins = false))
    println("B: " + b.materialize(policyInsertWins = false))
}

println()

// ========================
// Model 2: CmRDT (operation-based)
// ========================
println("=== CmRDT (operation-based) ===")

data class OpInsert(val valueId: ElementId, val value: String, val index: Int, val nodeId: String, val ts: Long)

data class OpRemove(val index: Int, val nodeId: String, val ts: Long)

class CmList(private val nodeId: String, initial: List<String>) {
    private val list = initial.toMutableList()
    private val history = mutableListOf<Any>() // OpInsert | OpRemove

    fun insert(valueId: ElementId, value: String, index: Int, ts: Long): OpInsert {
        val op = OpInsert(valueId, value, index, nodeId, ts)
        apply(op)
        history.add(op)
        return op
    }

    fun remove(index: Int, ts: Long): OpRemove {
        val op = OpRemove(index, nodeId, ts)
        apply(op)
        history.add(op)
        return op
    }

    fun receive(op: Any, policyInsertWins: Boolean) {
        val transformed = when (op) {
            is OpInsert -> transformInsert(op, policyInsertWins)
            is OpRemove -> transformRemove(op)
            else -> op
        }
        apply(transformed)
        history.add(op) // store original for future transforms
    }

    private fun transformInsert(op: OpInsert, policyInsertWins: Boolean): OpInsert {
        var idx = op.index
        history.forEach { h ->
            when (h) {
                is OpInsert -> if (h.index <= idx) idx += 1
                is OpRemove -> if (h.index < idx) idx -= 1 else if (h.index == idx) {
                    // concurrent remove at the same index
                    if (!policyInsertWins) return op.copy(index = -1) // mark as dropped
                }
            }
        }
        return op.copy(index = idx)
    }

    private fun transformRemove(op: OpRemove): OpRemove {
        var idx = op.index
        history.forEach { h ->
            when (h) {
                is OpInsert -> if (h.index <= idx) idx += 1
                is OpRemove -> if (h.index < idx) idx -= 1
            }
        }
        return op.copy(index = idx)
    }

    private fun apply(op: Any) {
        when (op) {
            is OpInsert -> if (op.index >= 0) list.add(op.index.coerceIn(0, list.size), op.value)
            is OpRemove -> if (op.index in list.indices) list.removeAt(op.index)
        }
    }

    fun state() = list.toList()
}

run {
    val a = CmList("A", listOf("A", "B", "C"))
    val b = CmList("B", listOf("A", "B", "C"))

    // Local ops
    val ins = a.insert(ElementId("X"), "X", index = 2, ts = 100)
    val rem = b.remove(index = 2, ts = 100)

    println("-- Policy: Insert-wins")
    a.receive(rem, policyInsertWins = true)
    b.receive(ins, policyInsertWins = true)
    println("A: ${a.state()}")
    println("B: ${b.state()}")

    // Reset replicas for remove-wins
    val a2 = CmList("A", listOf("A", "B", "C"))
    val b2 = CmList("B", listOf("A", "B", "C"))
    val ins2 = a2.insert(ElementId("X"), "X", index = 2, ts = 100)
    val rem2 = b2.remove(index = 2, ts = 100)

    println("-- Policy: Remove-wins")
    a2.receive(rem2, policyInsertWins = false)
    b2.receive(ins2, policyInsertWins = false)
    println("A: ${a2.state()}")
    println("B: ${b2.state()}")
}

println()

// ========================
// Model 3: Sequence CRDT (position-based with tombstones)
// ========================
println("=== Sequence CRDT (position-based) ===")

data class SeqNode(val id: ElementId, val value: String, var tombstone: Boolean = false)

class SequenceCRDT(initial: List<Pair<ElementId, String>>) {
    private val nodes = mutableListOf<SeqNode>()
    init { initial.forEach { (id, v) -> nodes.add(SeqNode(id, v, tombstone = false)) } }

    fun insertAfter(anchor: ElementId?, id: ElementId, value: String, policyInsertWins: Boolean) {
        // Find index of anchor (skipping tombstone if insert-wins is allowed)
        val anchorIdx = when (anchor) {
            null -> -1
            else -> nodes.indexOfFirst { it.id == anchor }
        }
        val anchorNode = if (anchorIdx >= 0) nodes[anchorIdx] else null
        if (anchorNode?.tombstone == true && !policyInsertWins) {
            // drop insert
            return
        }
        val insertAt = (anchorIdx + 1).coerceAtLeast(0).coerceAtMost(nodes.size)
        nodes.add(insertAt, SeqNode(id, value, tombstone = false))
    }

    fun remove(id: ElementId) {
        nodes.find { it.id == id }?.tombstone = true
    }

    fun values(): List<String> = visible(nodes)
}

run {
    val base = listOf(A to "A", B to "B", C to "C")

    // Insert-wins
    val seqIW = SequenceCRDT(base)
    seqIW.insertAfter(anchor = B, id = ElementId("X"), value = "X", policyInsertWins = true)
    seqIW.remove(B)
    println("-- Policy: Insert-wins")
    println("State: ${seqIW.values()}")

    // Remove-wins
    val seqRW = SequenceCRDT(base)
    seqRW.insertAfter(anchor = B, id = ElementId("X"), value = "X", policyInsertWins = false)
    seqRW.remove(B)
    println("-- Policy: Remove-wins")
    println("State: ${seqRW.values()}")
}

println()

println("=== Summary ===")
println("CvRDT: state union + tombstones; policy controls visibility of inserts anchored on removed elements")
println("CmRDT: op replay with simple transforms; policy determines whether insert at removed index is dropped")
println("Sequence CRDT: tombstones retain position; policy toggles whether inserts survive when anchor is deleted")
