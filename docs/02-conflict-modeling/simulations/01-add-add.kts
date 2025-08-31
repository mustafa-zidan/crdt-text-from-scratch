/**
 * CRDT Simulation: Concurrent Add/Add Conflict Resolution
 * 
 * This Kotlin script demonstrates how concurrent insertions by different nodes
 * at the same logical position are resolved in a CRDT system.
 * 
 * In this scenario:
 * - Two replicas (Node A and Node B) start with identical state: [A, B, C]
 * - Both concurrently insert different values ("X" and "Y") at index 2
 * - The simulation shows how Operational Transformation resolves these conflicts
 * 
 * Key Concepts Demonstrated:
 * 1. Operation Transformation to maintain consistency
 * 2. Node ID-based tie-breaking for concurrent operations
 * 3. Index shifting when operations conflict
 * 4. Convergent behavior across all replicas
 */

data class Operation(
    val value: String,
    val originalIndex: Int,
    val nodeId: String,
    val transformedIndex: Int = originalIndex
)

class CRDTList(private val nodeId: String, initial: List<String> = emptyList()) {
    private val list = initial.toMutableList()
    private val history = mutableListOf<Operation>()

    /**
     * Inserts a local operation and returns it
     * This represents an operation performed by this node locally
     */
    fun insert(value: String, index: Int): Operation {
        return Operation(value, index, nodeId)
            .also(::applyTransformed)
    }

    /**
     * Receives and applies a remote operation with proper OT-style transformation
     * This simulates receiving an operation from another replica and transforming it
     * to account for local operations that have already been applied
     */
    fun receive(remote: Operation) {
        remote
            .transformAgainst(history)
            .also(::applyTransformed)
    }

    /**
     * Applies a transformed operation and updates list/history
     * This is the core logic that actually applies the operation to the data structure
     */
    private fun applyTransformed(op: Operation) {
        val boundedIndex = op.transformedIndex.coerceIn(0, list.size)
        val finalOp = op.copy(transformedIndex = boundedIndex)
        list.add(boundedIndex, finalOp.value)
        history.add(finalOp)
    }

    /**
     * OT-style transformation: resolves index against prior ops
     * This is the key mechanism that ensures operations from different nodes
     * can be applied consistently even when they conflict on position
     */
    private fun Operation.transformAgainst(history: List<Operation>): Operation {
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

// Initialize two replicas with identical state
val nodeA = CRDTList("A", listOf("A", "B", "C"))
val nodeB = CRDTList("B", listOf("A", "B", "C"))

// Both nodes insert different values at the same logical position (index 2)
val opFromA = nodeA.insert("X", 2)
val opFromB = nodeB.insert("Y", 2)

// Simulate exchange of operations between replicas
// Each replica receives the other's operation and transforms it appropriately
nodeA.receive(opFromB)
nodeB.receive(opFromA)

// Print final states to show convergence
nodeA.printState()
nodeB.printState()



