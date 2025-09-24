# 3.1 Concurrent Add/Add

### Problem Definition

This problem occurs in CRDT-based list or map structures when two replicas independently and concurrently insert **values** at the **same logical key or index**.

#### Scenario

Given: Two replicas of a list or map.
At time T₀: The state is identical.
```
[A, B, C]
```
At time T₁:
At the same logical time:
- **Node A** inserts "X" at index `2` (between `B` and `C`)
- **Node B** also inserts "X" at index `2`
Both inserts happen concurrently and independently and with different operation identifiers or metadata
  (e.g., timestamps, Lamport clocks, node IDs).

### Why This is a Problem

Although the inserted value is identical,
CRDTs distinguish operations based on their **identity**, not just the payload.
This introduces the possibility of both inserts being preserved, despite their redundancy at the value level.

A CRDT system must answer:

- Should both "X" values be retained?
- Should one be dropped based on metadata (e.g., causal ordering, timestamps)?
- Is value-level deduplication even desirable at the CRDT layer?

### Possible Outcomes

#### 1. Treat as Distinct Operations

CRDT merges both inserts, resulting in:

```
[A, B, X, X, C]
```

This approach is common in CRDTs that favor preserving intent and maintaining causality,
even at the cost of visible duplicates.

#### 2. Deduplicate by Value (Application Logic)

The system or application layer chooses to ignore duplicate values:

```
[A, B, X, C]
```

This requires logic outside the core CRDT merge function,
as CRDTs typically avoid discarding operations without explicit rules.

### Notes

- In CRDTs like RGA or Logoot, insert positions are calculated using unique identifiers, making every insertion positionally unique even if values are identical.
- The choice of whether to deduplicate depends on whether the system considers operation **intent** or **outcome** more important.

### Questions for Design

- Is deduplication a user-facing requirement or an internal optimization?
- Does the application layer need to track origin metadata for inserted values?
- Should the merge logic attempt to collapse semantically equivalent operations?

## Solutions:
### 1. Use Unique Identifiers for Inserts
Assign a unique identifier to each node (e.g., a replica ID or UUID, or Replica ID + Counter (Lamport or Vector Clock)).
When merging operations, always apply inserts based on a total order over these node IDs.
For example, elements inserted by a node with a lower ID are ordered first.
This tiebreaking strategy ensures consistent ordering across all replicas without requiring coordination.
 
### 2. Last-Writer-Wins (LWW) Strategy
Another solution would be Last-Writer-Wins (LWW) 
where the most recent operation (based on timestamp or vector clock) is kept, but this can lead to loss of intent.

#### 3. Operational Transformation (OT)-like Resolution
Reorder one of the inserts (e.g., insert one after the other) based on conflict metadata.
This is an extension of replica ID-based ordering but allow preserving user intent and more complex conflict resolution.
Example:
Two Nodes (replicas) make concurrent edits:
- Node A: inserts "X" at index 2 → [A, B, X, C]
- Node B: inserts "Y" at index 2 → [A, B, Y, C]
  **Node A** inserts "X" at index 2 → `[A, B, X, C]`
- **Node B** inserts "Y" at index 2 → `[A, B, Y, C]`

These operations are concurrent — no node saw the other’s operation yet.

##### Merge Happens at Node C

Node C receives both operations:

1. First receives opA: `insert("X", index=2)` → `[A, B, X, C]`
2. Then receives opB: `insert("Y", index=2)`

Before applying opB, Node C transforms it because index 2 is already occupied by "X". It shifts opB to index 3:

```
opB' = insert("Y", index=3)
```

Final result at **Node C**:

```
[A, B, X, Y, C]
```

##### What Happens at Node A?

- Node A already has `[A, B, X, C]`
- It receives opB: `insert("Y", index=2)`
- But opA (its own insert) already changed the list — "X" is at index 2
- So Node A transforms opB the same way:

```
transform(opB, opA) = insert("Y", index=3)
```

- Applies it:

```
[A, B, X, Y, C]
```

##### What Happens at Node B?

- Node B already has `[A, B, Y, C]`
- It receives opA: `insert("X", index=2)`
- Both inserts targeted the same index, but since Node A's ID is lower, opA is ordered before opB
- Node B transforms its local state to insert "X" at index 2
- It shifts its own previously inserted "Y" to index 3

Final result at **Node B**:
```
[A, B, X, Y, C]
```



> Search:
> - "CRDT concurrent insertion"
> - "commutativity in concurrent writes"

- Simulate: two nodes adding `x` at the same key/index
- Model: do they converge to a stable state?
- CRDT Handling:
    - CvRDT: merge via set union
    - CmRDT: relies on commutative op replay
    - Sequence CRDT: needs tie-breaker logic (e.g., nodeId, timestamp)