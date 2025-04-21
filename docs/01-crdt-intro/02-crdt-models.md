## CvRDT – State-based CRDTs

> Search for:
> - "CvRDT merge join semilattice"
> - "state-based crdt examples"
> - "crdt full state merge"

### Your goal:
- Explain how CvRDTs work (full-state sync, merge via join)
- Define the merge rule mathematically (least upper bound)
- Provide examples: G-Counter, G-Set
- Discuss tradeoffs: simplicity vs bandwidth

### Reading suggestions:
- [A Comprehensive Study of CRDTs](https://hal.inria.fr/inria-00555588/document) — Section 3
- [CRDT.tech — CvRDT section](https://crdt.tech/#cvrdts)

---

## CmRDT – Operation-based CRDTs

> Search for:
> - "CmRDT causal delivery"
> - "operation-based crdt example"
> - "event ordering in CRDTs"

### Your goal:
- Explain how operations are propagated and applied
- Note requirement for **causal and reliable delivery**
- Describe op design: idempotent, commutative ops
- Examples: PN-Counter, RGA
- Pros/cons vs CvRDT

### Reading suggestions:
- [CRDT Primer by Red Planet Labs](https://redplanetlabs.com/blog/2020/04/15/an-introduction-to-conflict-free-replicated-data-types-crdts/)
- [A Comprehensive Study of CRDTs](https://hal.inria.fr/inria-00555588/document) — Section 4

---

## Δ-CRDT – Delta-state CRDTs

> Search for:
> - "delta crdt"
> - "delta-state replication"
> - "deltas vs ops vs full state"

### Your goal:
- Describe what a delta is (minimal change to state)
- Compare merge behavior with CvRDT
- Show why it reduces bandwidth and how it maintains convergence
- Discuss use in edge computing or low-bandwidth environments

### Reading suggestions:
- [Delta CRDTs: Microsoft Research Paper](https://www.microsoft.com/en-us/research/publication/delta-state-crdts/)
- [CRDT.tech — Delta CRDT](https://crdt.tech/#delta-crdts)

---

## Comparison Table

> Create:
> - A matrix/table comparing CvRDT, CmRDT, and Delta-CRDT
> - Compare: delivery guarantees, payload size, merge function, pros/cons

### Your goal:
- Create your *own table*, not mine
- Ensure you understand *why* each tradeoff matters

> Tip: Use Notion or Markdown tables to visualize