```
docs/
├── README.md                             # Entry point: describes the structure of docs folder
│
├── 00-crdt-intro/
│   ├── overview.md                       # What is a CRDT, why it matters
│   ├── crdt-models.md                    # CvRDT / CmRDT / Delta-CRDT comparison
│   ├── common-properties.md              # Idempotency, convergence, commutativity, etc.
│   └── real-world-uses.md                # Redis, Automerge, Yjs, Riak, etc.
│
├── 01-my-crdt-design/
│   ├── concept.md                        # Your idea: data type, structure, ops
│   ├── position-modeling.md              # (For sequences) How you'll order ops
│   ├── merge-strategy.md                 # Rules for conflict resolution
│   └── gc-and-tombstones.md              # Garbage collection, pruning, etc.
│
├── 02-implementation-notes/
│   ├── kotlin-core.md                    # Kotlin code structure, key classes
│   ├── integration-issues.md             # Frontend ↔ backend edge cases
│   ├── test-coverage.md                  # How and what you tested
│   └── limitations.md                    # What doesn't work yet and why
│
├── 03-editor-integration/
│   ├── react-integration.md              # How the CRDT ties into your frontend
│   ├── websocket-architecture.md         # Message flow, sync model
│   └── offline-handling.md               # (Optional) Local caching / sync on reconnect
│
├── 04-library-comparison/
│   ├── yjs.md                            # What it does well / pain points
│   ├── automerge.md                      # Merge strategy, performance
│   └── comparison-matrix.md              # Your side-by-side feature and perf table
│
├── 05-benchmarks/
│   ├── test-methodology.md               # What you're testing and why
│   ├── scenarios.md                      # Scenarios used during benchmarking
│   ├── results.md                        # Results table + graphs
│   └── analysis.md                       # Interpret results, draw conclusions
│
├── 06-lessons-learned/
│   ├── patterns-observed.md              # Repeated insights (about ops, merges, etc.)
│   ├── architecture-hindsight.md         # “If I did it again, I’d...”
│   └── future-ideas.md                   # Stretch goals, phase 2 plans
│
├── 99-dev-log/
│   ├── day-01.md                         # Daily/weekly logs, unfiltered notes
│   ├── day-02.md                         # ...
│   ├── bugs-found.md                     # Persistent issue tracker
│   └── glossary.md                       # Terms you keep re-Googling
│
├── conflict-modeling/
│   ├── README.md                         # What this subfolder is for
│   ├── framework.md                      # Modeling assumptions: delivery, causality, clocks
│   ├── scenarios/
│   │   ├── add-add.md
│   │   ├── add-remove.md
│   │   ├── out-of-order.md
│   │   ├── partition-merge.md
│   │   ├── tombstone-drift.md
│   │   ├── causal-violations.md
│   │   └── sequence-position-conflicts.md
│   └── simulations/
│       ├── add-add.kts
│       ├── add-remove.kts
│       ├── vector-clock-delay.kts
│       └── partition-scenario.kts
│
└── sequence-crdts/
    ├── README.md                         # Overview of CRDTs for sequences
    ├── rga.md                            # Replicated Growable Array
    ├── logoot.md                         # Logoot: position identifier-based
    ├── lseq.md                           # LSEQ: randomized allocation
    ├── comparison.md                     # Tradeoffs: ID growth, memory, complexity
    └── ordering-models.md                # Total vs partial order, tie-breaking
```