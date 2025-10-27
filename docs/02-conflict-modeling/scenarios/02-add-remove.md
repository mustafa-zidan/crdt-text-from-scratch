# 3.2 Concurrent Add/Remove

This scenario explores what happens when one replica inserts at index 2 while another replica concurrently removes the element that was at index 2. We show two practical policies and how three CRDT styles can realize them.

- Start state (identical replicas):
```
[A, B, C]
```
- Concurrent ops at logical time T₁:
  - Node A: insert "X" at index 2 (between B and C)
  - Node B: remove element at index 2 (removes "B")

## Why this matters
A remove targets an existing element; an insert targets a position between elements. When they are concurrent, the position may refer to an element that disappears. We must define whether the newly inserted element should remain if the anchor position was removed.

## Two resolution policies
1) Insert-wins at position
- Keep the inserted element even if the anchor position was removed concurrently.
- Typical in tombstone-based sequence CRDTs (e.g., RGA): the removal tombstones the old element; inserts that anchor there still survive.
- Expected final state from `[A, B, C]`: `[A, X, C]` (B removed, X kept).

2) Remove-wins at position
- Drop the insert if it targets the same position as a concurrent remove of the anchor element.
- Useful when you want deletions to be authoritative for that spot.
- Expected final state from `[A, B, C]`: `[A, C]` (B removed, X discarded).

## Concepts used
- Causality: we treat the two operations as concurrent (no happens-before relation).
- Tombstones: removals mark elements as deleted but keep their identity so that concurrent inserts can reference their location.
- Tie-breaking: when the insert and remove are concurrent and target the same anchor, a policy decides whether to keep or drop the insert.

## How we model it (see simulation script)
The simulation demonstrates three styles:
- CvRDT (state-based): elements plus tombstones; merge via union; policy determines whether an insert that anchors on a tombstoned element is retained in the visible sequence.
- CmRDT (op-based): operations are replayed; when an insert and a remove are concurrent at the same index, a policy resolves the conflict.
- Sequence CRDT (position-based): maintains element identities and tombstones; index transforms ensure convergence; a policy flag toggles insert-wins vs remove-wins at the same anchor.

## Run the simulation
- File: `docs/02-conflict-modeling/simulations/02-add-remove.kts`
- Run with Kotlin scripting support, e.g.:
```
kotlinc -script docs/02-conflict-modeling/simulations/02-add-remove.kts
```
It prints both policy outcomes for each model and shows they converge per policy.

## Notes for implementers
- Make concurrency explicit (equal logical timestamps or separate replicas exchanging ops) so neither op causally precedes the other.
- Keep internal history/tombstones so that replays/merges are idempotent and converge.
- Document which policy your product uses; users will notice whether inserts survive deletes at the same spot.