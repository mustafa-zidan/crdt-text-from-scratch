# Intro to CRDTs

> A structured personal learning document to explore, compare, and deeply understand the major CRDT models in distributed systems.

---

## Table of Contents

1. [What is a CRDT?](#what-is-a-crdt)
2. [CvRDT – State-based CRDTs](02-crdt-models.md#cvrdt--state-based-crdts)
3. [CmRDT – Operation-based CRDTs](02-crdt-models.md#cmrdt--operation-based-crdts)
4. [Δ-CRDT – Delta-state CRDTs](02-crdt-models.md#δ-crdt--delta-state-crdts)
5. [Comparison Table](02-crdt-models.md#comparison-table)
6. [Advanced / Hybrid CRDTs](04-real-world-uses.md#advanced--hybrid-crdts)
7. [Use Cases by Model Type](04-real-world-uses.md#use-cases-by-model-type)
8. [Further Reading](04-real-world-uses.md#further-reading)

---

## What is a CRDT?
> Search for:
> - "CRDT definition"
> - "eventual consistency in distributed systems"
> - "CAP theorem and availability vs consistency"

### Your goal:
- Define CRDTs in your own words
- List the essential properties: convergence, idempotency, commutativity, associativity
- Explain **why they matter** in distributed systems (especially offline-first apps)

### Reading suggestions:
- [Wikipedia: Conflict-free replicated data type](https://en.wikipedia.org/wiki/Conflict-free_replicated_data_type)
- [Martin Kleppmann’s Blog on CRDTs](https://martin.kleppmann.com/2016/02/08/crdts.html)