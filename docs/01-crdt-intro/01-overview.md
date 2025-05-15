# Intro to CRDTs

> A structured personal learning document to explore, compare, and deeply understand the major CRDT models in
> distributed systems.
---

## Introduction

In distributed systems, data is often replicated across multiple nodes, locations, or devices.  
Keeping these replicas in sync is challenging, especially when changes happen independently and concurrently across
nodes.

Concurrent updates to the same data structure can lead to divergent states, particularly in offline-first applications
where nodes may go out of sync and later reconnect with conflicting updates.

A common approach to managing this is through **eventual consistency**—a model that allows temporary inconsistencies,
with the guarantee that all replicas will ultimately converge to the same state.

## Characteristics of Eventual Consistency

- **Temporary inconsistencies are allowed:** Systems tolerate out-of-sync data for a period of time without treating it
  as a failure.
- **Convergence is guaranteed:** All replicas will eventually synchronize, assuming no new updates and sufficient
  communication.
- **No need for global coordination:** Nodes can accept writes without locking or waiting for consensus.
- **Automatic conflict resolution:** The system resolves conflicts using algorithms or specialized data structures, such
  as CRDTs.

## Real-World Examples of Eventual Consistency

- **Distributed databases (e.g., Apache Cassandra):** Data written to one node propagates asynchronously to others.
- **DNS systems:** DNS record changes can take time to update globally, leading to temporary mismatches.
- **Collaborative editing tools:** Applications like Google Docs or Notion allow concurrent edits, which are eventually
  merged into a consistent shared document.

## Impact on Performance, Scalability, and Availability

- **Improved performance:** Faster writes and lower latency, since nodes don’t wait for global confirmation.
- **Increased scalability:** Systems can scale horizontally and operate independently.
- **High availability:** Systems remain responsive even during network partitions.
- **Trade-off:** Temporary inconsistencies must be carefully managed to avoid confusing or incorrect user experiences.

## Eventual Consistency vs. Strong Consistency

- **Strong Consistency:** Guarantees that all reads reflect the latest write. Ideal for systems requiring strict
  accuracy (e.g., banking or financial systems).
- **Eventual Consistency:** Prioritizes availability and fault tolerance. Suitable for systems where temporary
  differences are acceptable (e.g., social media, collaborative tools).

## Implementing Eventual Consistency

One common approach is through **Conflict-Free Replicated Data Types (CRDTs)**—data structures designed for safe,
concurrent updates across distributed systems. These ensure all replicas reach the same state automatically, even if
updates arrive in different orders or at different times.

## Challenges of Eventual Consistency

- **Stale data:** Users may see outdated information temporarily.
- **Harder to reason about state:** Understanding the "true" system state can be complex when updates propagate
  asynchronously.
- **Conflict resolution complexity:** Systems must automatically and safely merge concurrent changes.
- **Partition tolerance requirements:** Systems must be designed to handle network failures without data loss or
  corruption.

## What is a CRDT?

CRDTs solve many challenges of eventual consistency by allowing multiple replicas of a data structure to be updated
independently, while still ensuring that all versions eventually converge.

### CRDT Features

- Replicas can be updated concurrently and independently, without needing coordination.
- Conflicts are resolved using algorithms that merge changes safely.
- The system guarantees convergence regardless of the order in which updates are received.

### CRDT Properties

- **Convergence:** All replicas eventually reach the same state.
- **Idempotency:** Reapplying the same operation multiple times has no additional effect.
- **Commutativity:** The order of operations doesn’t affect the final outcome.
- **Associativity:** Grouping of operations doesn’t impact the result.

### CRDT Types

- **State-based CRDTs (CvRDTs):** Merge the entire state of replicas to resolve conflicts.
- **Operation-based CRDTs (CmRDTs):** Send operations to other replicas, which apply them to their local state.

### Reading suggestions:

- [Wikipedia: Conflict-free replicated data type](https://en.wikipedia.org/wiki/Conflict-free_replicated_data_type)
- [Conflict Resolution for Eventual Consistency • Martin Kleppmann • GOTO 2016](https://www.youtube.com/watch?v=yCcWpzY8dIA&ab_channel=GOTOConferences)