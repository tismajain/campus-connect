# AGENTS.md

You are implementing Campus Connect, a campus resource discovery MVP that must be demo-ready tonight.

Read `IMPLEMENTATION.md`, `database/schema.sql`, and the CSV files in `database/` before changing code.

Priorities:
1. Working end-to-end flow over architectural complexity.
2. Preserve all source-data facts exactly.
3. Never infer software-to-lab installation locations; the source does not contain them.
4. Use Spring Boot + PostgreSQL for backend and Next.js + TypeScript + Tailwind for frontend.
5. Keep the application a modular monolith.
6. Build responsive, professional UI without gradients or excessive decorative effects.
7. Add loading, empty, and error states.
8. Add straightforward admin CRUD sufficient to demonstrate a DB update appearing in the student UI.
9. Keep setup simple and document exact commands in README.
10. Before finishing, run the available backend and frontend builds/tests and fix compile errors.

Do not add OAuth, Redis, Kafka, Kubernetes, Elasticsearch, AI search, booking, maps, or unrelated features.

If a detail is ambiguous, choose the simplest implementation consistent with `IMPLEMENTATION.md` instead of blocking on clarification.
