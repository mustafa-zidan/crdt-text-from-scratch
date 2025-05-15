# 🧠 CRDT Mastery Roadmap

> A structured journey from fundamental CRDT concepts to a full-scale collaborative text editor—starting with
> primitives, then building toward practical, production-ready systems.

---

## 🧱 PHASE 1 — Pure CRDT Internals (No Libraries)

### 🔹 Day 1: Understand the CRDT Model Types

- Research and document:
    - State-based CRDTs (CvRDTs)
    - Operation-based CRDTs (CmRDTs)
    - Merge strategies and network assumptions
- Output: `/docs/01-intro-to-crdts.md` (with diagrams & examples)

---

### 🔹 Day 2: Implement G-Counter (Grow-only Counter)

- Single-op: `increment(nodeId)`
- Merge: take the max value per node
- Output: `/src/crdt/GCounter.kt` + test

---

### 🔹 Day 3: Implement PN-Counter (Positive-Negative Counter)

- Dual counters: `P` and `N`
- `increment`, `decrement`, `value()`, `merge()`
- Output: `/src/crdt/PNCounter.kt`

---

### 🔹 Day 4: Implement G-Set (Grow-only Set)

- Single-op: `add(element)`
- Merge: set union
- Output: `/src/crdt/GSet.kt`

---

### 🔹 Day 5: Implement 2P-Set (Two-Phase Set)

- Ops: `add()`, `remove()`
- Rule: once removed, can’t re-add
- Merge: union both addSet/removeSet
- Output: `/src/crdt/TwoPSet.kt`

---

### 🔹 Day 6: Implement OR-Set (Observed-Remove Set)

- Ops have unique IDs (UUIDs or timestamps)
- `remove(opId)` creates tombstones
- Merge must resolve conflicting add/remove
- Output: `/src/crdt/ORSet.kt`

---

## 🔁 PHASE 2 — Conflict Simulation & Causality

### 🔹 Day 7: Simulate Network Partition + Reconnection

- Create two node instances
- Simulate divergent ops while disconnected
- Merge logs upon reconnection
- Output: `/test/scenarios/partition-merge.kt`
- Log state before/after

---

### 🔹 Day 8: Implement Vector Clocks

- Track version vectors per node
- Implement `compare(vc1, vc2): <, >, =, ||`
- Output: `/src/util/VectorClock.kt`
- Use to detect causality between events

---

### 🔹 Day 9: Prototype Custom Text CRDT (Op-based)

- Define ops: `insert(char, position, nodeId, timestamp)`
- Implement log-based replay
- Deterministic ordering (timestamp, nodeId tie-breaker)
- Output: `/src/crdt/TextCRDT.kt`

---

## ✍️ PHASE 3 — Modeling Text & Real-Time Editing

### 🔹 Day 10–12: Design Real Text Editing Model

- Choose RGA / Logoot / LSEQ
- Define:
    - Op structure
    - Positioning logic
    - Merge strategy
- Output: `/docs/text-crdt-design.md`

---

### 🔹 Day 13–16: Implement CRDT Text Engine

- CRDT sequence data structure
- Local + remote op handling
- Merge logic for concurrent inserts/deletes
- Output: `/src/crdt/TextEngine.kt`

---

## 🌐 PHASE 4 — Frontend + Sync Integration

### 🔹 Day 17–20: Integrate with Editor (React)

- Use a simple `contenteditable` or textarea
- Bind to CRDT engine
- Reflect remote ops via WebSocket
- Output: `/frontend/src/components/Editor.tsx`

---

### 🔹 Day 21–23: WebSocket Sync (Kotlin Server)

- Broadcast ops to all peers
- Reconnect logic
- Optional: simulate packet loss or latency
- Output: `/backend/src/server/WebSocketHandler.kt`

---

## ⚔️ PHASE 5 — Compare With Yjs / Automerge

### 🔹 Day 24–27: Library Integration

- Swap frontend CRDT core with:
    - [ ] Yjs
    - [ ] Automerge
- Run identical tests & usage scenarios
- Output: `/docs/comparison.md`

---

### 🔹 Day 28+: Benchmarking & Polish

- Log: op counts, memory, latency
- Visualize operation timeline
- Document:
    - Strengths/weaknesses of your model
    - Final reflections

---

## 🧪 BONUS Phase — Advanced CRDTs (if not already crying)

- [ ] Implement delta-state CRDTs
- [ ] CRDT garbage collection
- [ ] Awareness protocol (cursor positions)
- [ ] Offline persistence (IndexedDB, file system)