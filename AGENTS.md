# AGENTS.md

You are implementing Campus Connect, a campus resource discovery MVP that must be demo-ready tonight.

Read `IMPLEMENTATION.md`, `SECURITY.md`, `UI_DIRECTION.md`, `database/schema.sql`, and the CSV files in `database/` before changing code.

Priorities:
1. Working end-to-end flow over architectural complexity.
2. Preserve all source-data facts exactly.
3. Never infer software-to-lab installation locations; the source does not contain them.
4. Use Spring Boot + PostgreSQL for backend and Next.js + TypeScript + Tailwind for frontend.
5. Keep the application a modular monolith.
6. Build responsive, professional UI with a distinct campus-resource identity. Follow `UI_DIRECTION.md`; avoid generic AI/vibecoded styling.
7. Add loading, empty, and error states.
8. Add straightforward admin CRUD sufficient to demonstrate a DB update appearing in the student UI.
9. Security is a release requirement, not optional polish. Follow `SECURITY.md` and do not expose unauthenticated write endpoints.
10. Keep setup simple and document exact commands in README.
11. Before finishing, run backend and frontend builds/tests and fix compile errors.

Do not add Redis, Kafka, Kubernetes, Elasticsearch, AI search, booking, maps, or unrelated features.

If a detail is ambiguous, choose the simplest implementation consistent with `IMPLEMENTATION.md`, `SECURITY.md`, and `UI_DIRECTION.md` instead of blocking on clarification.
