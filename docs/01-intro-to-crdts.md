# Intro to CRDTs

> A structured personal learning document to explore, compare, and deeply understand the major CRDT models in distributed systems.

---

## 📚 Table of Contents

1. [What is a CRDT?](#what-is-a-crdt)
2. [CvRDT – State-based CRDTs](#cvrdt--state-based-crdts)
3. [CmRDT – Operation-based CRDTs](#cmrdt--operation-based-crdts)
4. [Δ-CRDT – Delta-state CRDTs](#δ-crdt--delta-state-crdts)
5. [Comparison Table](#comparison-table)
6. [Advanced / Hybrid CRDTs](#advanced--hybrid-crdts)
7. [Use Cases by Model Type](#use-cases-by-model-type)
8. [Further Reading](#further-reading)

---

## 🧠 What is a CRDT?



> ✏️ Search for:
- "CRDT definition"
- "eventual consistency in distributed systems"
- "CAP theorem and availability vs consistency"

🎯 Your goal:
- Define CRDTs in your own words
- List the essential properties: convergence, idempotency, commutativity, associativity
- Explain **why they matter** in distributed systems (especially offline-first apps)

🔗 Reading suggestions:
- [Wikipedia: Conflict-free replicated data type](https://en.wikipedia.org/wiki/Conflict-free_replicated_data_type)
- [Martin Kleppmann’s Blog on CRDTs](https://martin.kleppmann.com/2016/02/08/crdts.html)

---

## 🧱 CvRDT – State-based CRDTs

> ✏️ Search for:
- "CvRDT merge join semilattice"
- "state-based crdt examples"
- "crdt full state merge"

🎯 Your goal:
- Explain how CvRDTs work (full-state sync, merge via join)
- Define the merge rule mathematically (least upper bound)
- Provide examples: G-Counter, G-Set
- Discuss tradeoffs: simplicity vs bandwidth

🔗 Reading suggestions:
- [A Comprehensive Study of CRDTs](https://hal.inria.fr/inria-00555588/document) — Section 3
- [CRDT.tech — CvRDT section](https://crdt.tech/#cvrdts)

---

## 🔧 CmRDT – Operation-based CRDTs

> ✏️ Search for:
- "CmRDT causal delivery"
- "operation-based crdt example"
- "event ordering in CRDTs"

🎯 Your goal:
- Explain how operations are propagated and applied
- Note requirement for **causal and reliable delivery**
- Describe op design: idempotent, commutative ops
- Examples: PN-Counter, RGA
- Pros/cons vs CvRDT

🔗 Reading suggestions:
- [CRDT Primer by Red Planet Labs](https://redplanetlabs.com/blog/2020/04/15/an-introduction-to-conflict-free-replicated-data-types-crdts/)
- [A Comprehensive Study of CRDTs](https://hal.inria.fr/inria-00555588/document) — Section 4

---

## 🌱 Δ-CRDT – Delta-state CRDTs

> ✏️ Search for:
- "delta crdt"
- "delta-state replication"
- "deltas vs ops vs full state"

🎯 Your goal:
- Describe what a delta is (minimal change to state)
- Compare merge behavior with CvRDT
- Show why it reduces bandwidth and how it maintains convergence
- Discuss use in edge computing or low-bandwidth environments

🔗 Reading suggestions:
- [Delta CRDTs: Microsoft Research Paper](https://www.microsoft.com/en-us/research/publication/delta-state-crdts/)
- [CRDT.tech — Delta CRDT](https://crdt.tech/#delta-crdts)

---

## 📊 Comparison Table

> ✏️ Create:
- A matrix/table comparing CvRDT, CmRDT, and Delta-CRDT
- Compare: delivery guarantees, payload size, merge function, pros/cons

🎯 Your goal:
- Create your *own table*, not mine
- Ensure you understand *why* each tradeoff matters

🔗 Tip: Use Notion or Markdown tables to visualize

---

## 🧪 Advanced / Hybrid CRDTs

> ✏️ Search for:
- "CRDT for collaborative text editing"
- "Logoot vs RGA vs LSEQ"
- "Automerge internal structure"
- "JSON CRDTs"
- "Undoable CRDT"

🎯 Your goal:
- Document at least 2 advanced or hybrid types
- Explain what problems they solve (e.g., sequences, nesting, rich documents)
- Mention design choices (positioning logic, tombstones, garbage collection)
- Optional: diagrams of how they order elements

🔗 Reading suggestions:
- [Logoot paper](https://hal.inria.fr/inria-00432368/document)
- [Automerge docs](https://automerge.org/docs/)
- [Yjs architecture](https://docs.yjs.dev/overview/architecture)

---

## 🔍 Use Cases by Model Type

> ✏️ Search for:
- "CRDTs in real-world apps"
- "offline first architecture"
- "real-time sync CRDT"

🎯 Your goal:
- For each CRDT type, list what real-world scenarios it fits best
- Bonus: find *actual tools* or databases that use it (e.g., Redis, Automerge, Yjs, Riak)

---

## 📚 Further Reading

> ✏️ Compile:
- Papers you didn’t read fully yet
- Blog posts you want to revisit
- YouTube videos or talks worth bookmarking

🎯 Your goal:
- Maintain a running log of “stuff I want to understand later”
- Optional: add a priority/star rating next to each

🔗 Add to:
- [crdt.tech](https://crdt.tech) — great index
- [CRDTs in Practice](https://queue.acm.org/detail.cfm?id=2851230) — ACM Queue article

---