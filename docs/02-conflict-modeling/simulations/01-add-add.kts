/**
 * CRDT Simulation: Concurrent Add/Add Conflict Resolution
 *
 * This Kotlin script demonstrates how different CRDT models handle concurrent
 * insertions by different nodes at the same logical position.
 *
 * Scenario:
 * - Two replicas (Node A and Node B) start with identical state: [A, B, C]
 * - Both concurrently insert different values ("X" and "Y") at index 2
 *
 * Three CRDT Models are demonstrated:
 * 1. CvRDT (Commutative Replicated Data Type) - Set-based approach
 * 2. CmRDT (Commutative Monotonic Replicated Data Type) - Operation-based approach
 * 3. Sequence CRDT - Position-based approach with Operational Transformation
 */

// ========================
// CvRDT Simulation (Set-based)
// ========================
println("=== CvRDT Simulation (Set-based) ===")

data class CvRDTItem(val value: String, val nodeId: String, val timestamp: Long)

class CvRDTList(private val nodeId: String, initial: List<String> = emptyList()) {
    private val items = mutableSetOf<CvRDTItem>()

    init {
        initial.forEachIndexed { index, value ->
            items.add(CvRDTItem(value, nodeId, index.toLong()))
        }
    }

    fun insert(value: String, timestamp: Long): CvRDTItem {
        val item = CvRDTItem(value, nodeId, timestamp)
        items.add(item)
        return item
    }

    fun merge(other: CvRDTList) {
        // Merge while deduplicating items based on value and timestamp only
        val otherItems = other.items.toMutableSet()
        otherItems.removeAll { item ->
            // Check if we already have an item with the same value and timestamp
            items.any { existing ->
                existing.value == item.value && existing.timestamp == item.timestamp
            }
        }
        items.addAll(otherItems)
    }

    fun printState() {
        // Sort by timestamp to show order
        val sortedItems = items.sortedBy { it.timestamp }
        val values = sortedItems.map { it.value }
        println("[$nodeId] Set-based List: $values")
    }
}

val cvrdtA = CvRDTList("A", listOf("A", "B", "C"))
val cvrdtB = CvRDTList("B")

// Both nodes insert at same logical time (timestamp)
val itemA = cvrdtA.insert("X", 100)
val itemB = cvrdtB.insert("Y", 100)

// Merge operations
cvrdtA.merge(cvrdtB)
cvrdtB.merge(cvrdtA)

cvrdtA.printState()
cvrdtB.printState()

println()

// ========================
// CmRDT Simulation (Operation-based)
// ========================
println("=== CmRDT Simulation (Operation-based) ===")

data class Operation(val value: String, val originalIndex: Int, val nodeId: String, val timestamp: Long)

class CmRDTList(private val nodeId: String, initial: List<String> = emptyList()) {
    private val list = initial.toMutableList()
    private val history = mutableListOf<Operation>()

    fun insert(value: String, index: Int, timestamp: Long): Operation {
        return Operation(value, index, nodeId, timestamp).also { op ->
            list.add(index, op.value)
            history.add(op)
        }
    }

    // Simple merge - just append operations (in practice would use proper commutativity rules)
    fun merge(other: CmRDTList) {
        other.history.forEach { op ->
            if (!history.contains(op)) {
                // Apply operation with same index
                list.add(op.originalIndex, op.value)
                history.add(op)
            }
        }
    }

    fun printState() = println("[$nodeId] List: $list")
}

val cmrdtA = CmRDTList("A", listOf("A", "B", "C"))
val cmrdtB = CmRDTList("B", listOf("A", "B", "C"))

// Both nodes insert at same logical time
val opA = cmrdtA.insert("X", 2, 100)
val opB = cmrdtB.insert("Y", 2, 100)

// Merge operations
cmrdtA.merge(cmrdtB)
cmrdtB.merge(cmrdtA)

cmrdtA.printState()
cmrdtB.printState()

println()

// ========================
// Sequence CRDT Simulation (Position-based with OT)
// ========================
println("=== Sequence CRDT Simulation (Position-based with OT) ===")

data class SequenceOperation(
    val value: String,
    val originalIndex: Int,
    val nodeId: String,
    val transformedIndex: Int = originalIndex
)

class SequenceCRDTList(private val nodeId: String, initial: List<String> = emptyList()) {
    private val list = initial.toMutableList()
    private val history = mutableListOf<SequenceOperation>()

    fun insert(value: String, index: Int): SequenceOperation {
        return SequenceOperation(value, index, nodeId)
            .also { op -> applyTransformed(op) }
    }

    fun receive(remote: SequenceOperation) {
        remote.transformAgainst(history).also { transformedOp ->
            applyTransformed(transformedOp)
        }
    }

    private fun applyTransformed(op: SequenceOperation) {
        val boundedIndex = op.transformedIndex.coerceIn(0, list.size)
        val finalOp = op.copy(transformedIndex = boundedIndex)
        list.add(boundedIndex, finalOp.value)
        history.add(finalOp)
    }

    private fun SequenceOperation.transformAgainst(history: List<SequenceOperation>): SequenceOperation {
        val newIndex = history.fold(originalIndex) { acc, local ->
            when {
                local.transformedIndex < acc -> acc + 1
                local.transformedIndex == acc && local.nodeId < this.nodeId -> acc + 1
                else -> acc
            }
        }
        return copy(transformedIndex = newIndex)
    }

    fun printState() = println("[$nodeId] List: $list")
}

val seqA = SequenceCRDTList("A", listOf("A", "B", "C"))
val seqB = SequenceCRDTList("B", listOf("A", "B", "C"))

// Both nodes insert different values at the same logical position (index 2)
val opFromA = seqA.insert("X", 2)
val opFromB = seqB.insert("Y", 2)

// Simulate exchange of operations between replicas
seqA.receive(opFromB)
seqB.receive(opFromA)

seqA.printState()
seqB.printState()

println()

// ========================
// Summary
// ========================
println("=== Summary ===")
println("CvRDT: Uses set-based approach - merge via union, handles duplicates naturally")
println("CmRDT: Uses operation-based approach - relies on commutative operations")
println("Sequence CRDT: Uses position-based approach with OT for conflict resolution")