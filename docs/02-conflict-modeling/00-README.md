# Conflict Modeling in CRDTs

This document outlines key conflict scenarios in distributed systems and how various CRDT types approach them. It serves
as a simulation journal and pattern catalog.

---

## Table of Contents

1. [Why Conflict Modeling Matters](#why-conflict-modeling-matters)
2. [Modeling Framework](#modeling-framework)
3. [Conflict Patterns](#conflict-patterns)
    - 3.1 Concurrent Add/Add
    - 3.2 Concurrent Add/Remove
    - 3.3 Out-of-Order Delivery
    - 3.4 Network Partition
    - 3.5 Ghost Deletes / Tombstone Drift
    - 3.6 Duplicate Delivery
    - 3.7 Causal Violations
    - 3.8 Structural Conflicts (for sequences)
4. [Pattern Summary Matrix](#pattern-summary-matrix)
5. [Lessons Learned](#lessons-learned)

---

## Why Conflict Modeling Matters

- CRDTs promise convergence without coordination, but this only holds under specific failure assumptions. 
- Modeling conflict is essential to understand and verify how CRDTs behave under delay, concurrency, and faults. 
- Real-world systems face out-of-order delivery, partitions, and crashes—scenarios where correctness must still hold. 
- Robustness isn’t an afterthought; it’s a result of designing for the failure path from the start. 
- You gain confidence in a CRDT by stress-testing it against conflict scenarios, not by assuming the happy path. 
- Key idea: Convergence is only meaningful when conflict actually happens.

### Mental hook:

> Convergence is only meaningful when conflict is present. The absence of coordination *is* the system’s challenge.

---

## Modeling Framework

### Node Model

* The Node is represented with an object that contain a list of CRDT objects along with receive operations and conflict resolution mechanisms
* Each Node contain a history (op log) of all the events received or the transformations done to objects (can be considered a transformation event) by the node.

### Delivery Mode

* Data transported asynchronously and Out-of-order between replicas with a tiebreaker to avoid a total order tracking
* Duplicates are *implicitly* handled only if transformation is commutative across deliveries; however, without dedup, **duplicate delivery ⇒ duplicate inserts**.

### Time & Causality

- **Clocking:** None explicitly (no Lamport or vector clocks).
- **Causality capture:** Indirect via `transformAgainst(history)` which replays prior applied ops.
- **Concurrency detection:** Two ops are “concurrent” if they target the same `originalIndex` without a causal relation.
- **Tie-break rule:** If `local.transformedIndex == acc && local.nodeId < this.nodeId` then shift right (+1).  
  This yields a deterministic total order among concurrent inserts at the same position.

### Convergence & Correctness Claims

- **Convergence (as coded):** For concurrent inserts at the same position, replicas converge by consistent tie-break (`nodeId`).
- **Preconditions needed for broader correctness:**
    - **Dedup:** introduce unique op IDs and a `seen` set; otherwise duplicates diverge.
    - **Causal readiness/buffering:** if later you add deletes/moves, you’ll need dependencies or causal checks.
    - **History availability:** `transformAgainst(history)` assumes prior ops are present and in the same effective order.

### Invariants / Safety Properties

- **I1 (Deterministic ordering of concurrent equal-index inserts):** With a stable, total order on `nodeId`, all replicas compute the same insertion indices after transforming against the same prefix.
- **I2 (Index bounding):** `coerceIn(0, list.size)` prevents OOB, but can mask logic errors by silently retargeting.

### Mental hook:

> You’re not building a system—you’re building a lab to **stress-test** causal logic.

---

## Conflict Patterns

### 3.1 Concurrent Add/Add

> Search:
> - "CRDT concurrent insertion"
> - "commutativity in concurrent writes"

- Simulate: two nodes adding `x` at the same key/index
- Model: do they converge to a stable state?
- CRDT Handling:
    - CvRDT: merge via set union
    - CmRDT: relies on commutative op replay
    - Sequence CRDT: needs tie-breaker logic (e.g., nodeId, timestamp)

---

### 3.2 Concurrent Add/Remove

> Search:
> - "add-remove conflict crdt"
> - "OR-Set semantics"

- Simulate: node A adds, node B removes (same element/key)
- Who wins? Why?
- CRDT Examples:
    - G-Set: can't remove
    - 2P-Set: remove wins permanently
    - OR-Set: uses op IDs to resolve (observed-remove semantics)

---

### 3.3 Out-of-Order Delivery

> Search:
> - "crdt eventual delivery"
> - "idempotent merges"

- Simulate: node receives op 2 before op 1
- Question: how is correctness preserved?
- What needs to be **buffered**, **deferred**, or **applied immediately**?

---

### 3.4 Network Partition

> Search:
> - "network partition crdt"
> - "CAP theorem eventual consistency"

- Simulate: nodes diverge for `n` operations, then reconnect
- Compare pre- and post-merge state
- Challenge: garbage collection, conflict merging

---

### 3.5 Ghost Deletes / Tombstone Drift

> Search:
> - "crdt tombstone management"
> - "garbage collection crdt"

- Problem: deletions require bookkeeping
- What happens if tombstones are forgotten too early?

---

### 3.6 Duplicate Delivery

> Search:
> - "idempotent crdt operations"
> - "duplicate ops"

- Simulate: node receives the same op twice
- Should result in **no change** on second apply
- What makes an op idempotent? How do you check it?

---

### 3.7 Causal Violations

> Search:
> - "crdt vector clock enforcement"
> - "concurrent vs causal ops"

- Simulate: operation `B` depends on `A`, but `A` is delayed
- How does your CRDT prevent breaking causality?
- Do you buffer? Reject? Apply out of order?

---

### 3.8 Structural Conflicts (Sequence CRDTs)

> Search:
> - "logoot concurrent inserts"
> - "RGA insert ordering"

- Simulate: insertions at same position from multiple nodes
- How is ordering preserved?
- What happens if two inserts have the same timestamp but different origins?

---

## Pattern Summary Matrix

| Conflict Type         | CvRDT | CmRDT | Delta | OR-Set | RGA | Logoot | LSEQ |
|-----------------------|-------|-------|-------|--------|-----|--------|------|
| Add/Add               | ✔     | ✔     | ✔     | ✔      | ~   | ✔      | ✔    |
| Add/Remove            | ✖     | ✔     | ✔     | ✔      | ✔   | ~      | ✔    |
| Out-of-Order Delivery | ✔     | ✖*    | ✔     | ✔      | ✔   | ✔      | ✔    |
| Network Partition     | ✔     | ✖*    | ✔     | ✔      | ✔   | ✔      | ✔    |
| Tombstone Drift       | ✔     | ✔     | ✔     | ~      | ✖   | ~      | ~    |
| Duplicate Delivery    | ✔     | ✔     | ✔     | ✔      | ✔   | ✔      | ✔    |
| Causal Violation      | ✔     | ✖     | ✔     | ~      | ✔   | ✔      | ✔    |
| Structural Conflict   | ✖     | ✔     | ✔     | ✖      | ✔   | ✔      | ✔    |

Legend:
- `~` = Depends on implementation
- `✖` = Inherently not supported without extra mechanisms

---

## Lessons Learned

> Questions to reflect on:
> - Which types of CRDTs are most sensitive to conflict ordering?
> - How do merge strategies scale with complexity?
> - What assumptions am I making about delivery guarantees?

---