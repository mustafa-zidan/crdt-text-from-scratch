# Introduction to CRDTs

## Architecture

### CRDT Model (Conflict-free Replicated Data Types)

CRDTs are data structures that can be replicated across multiple computers in a network, and they can be updated independently
and concurrently without the need for synchronization. They are designed to ensure that all replicas converge to the
same state, even in the presence of network partitions and concurrent updates.

#### Types of CRDTs:

* **State-Based CRDTs (CvRDTs)**: Also Known as Convergent Replicated Data Types,
* **Operation Based CRDTs (CmRDTs)**: Also known as Commutative Replicated Data Types,
* **Delta CRDTs**: A hybrid approach

### The difference between OT and CRDT
* **Operational Transformation (OT)**: A technique used in collaborative applications to 
* ensure that all users see the same changes in the same order, even if they are made concurrently.


### Why sequence CRDTs are special/difficult
* **Sequence CRDTs**: A type of CRDT that is specifically designed to handle ordered sequences of elements, such as text or lists.