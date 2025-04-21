# CRDT Text Editor: From Scratch to Reality

> A hands-on project to build a sequence CRDT for collaborative text editing from scratch and  benchmark it.
> This is an attempt to understand the principles of CRDTs and their practical applications in real-time collaborative systems.

## Purpose

This project is a personal learning journey into the world of CRDTs (Conflict-free Replicated Data Types) in the context of real-time collaborative applications.

## Documentation

All milestone notes and technical write-ups are in the [docs](./docs) folder.

- [CRDT Intro](./docs/01-crdt-intro): Introduction to CRDTs and their types, common properties, and use cases.
- [Conflict Modeling](./docs/02-conflict-modeling): Exploring different types of conflicts in CRDTs and how to handle them.
- [CRDT Sequence](./docs/03-crdt-sequence): Detailed exploration of sequence CRDTs, their properties, and design considerations.
- [CRDT Design](./docs/04-crdt-design): Design decisions and considerations for building a sequence CRDT.
- [CRDT Implementation](./docs/05-crdt-implementation): Implementation details and code structure for the sequence CRDT.
- [Editor Integration](./docs/06-editor-integration): Integrating the CRDT with a text editor for real-time collaboration.
- [Library Comparison](./docs/07-library-comparison): Comparing the implemented CRDT with existing libraries in terms of performance and correctness.
- [Benchmarks](./docs/08-benchmarks): Performance benchmarks of the implemented CRDT against existing libraries.
- [Lessons Learned](./docs/09-lessons-learned): Reflections on the learning journey, challenges faced, and key takeaways.

## Goals

- Build a working sequence CRDT from first principles
- Compare its correctness and performance with production libraries
- Document key decisions, failures, and wins