# Campus Connect

Campus Connect helps students discover hardware, software, labs, and specialist equipment available on campus.

## MVP architecture
- Frontend: Next.js + TypeScript + Tailwind CSS
- Backend: Spring Boot + Spring Data JPA
- Database: PostgreSQL
- Scale target: 3–4k students

## Current verified source data
- 28 SOCE computer labs across AB4 and AB5
- 2 explicitly GPU-equipped labs: AB4-406 and AB4-411, each listed with 16 GB RAM and 12 GB GPU
- 7 Embedded Systems Lab equipment items
- 36 subject-to-software mappings

Important: the source spreadsheet maps subjects to software, but does **not** specify which physical labs have each software package installed. Do not invent software-to-lab relationships.

## MVP user flow
1. Search for `GPU`, `CUDA`, a room, software package, or equipment item.
2. Browse/filter labs by building and GPU availability.
3. Open a lab detail page and see hardware configuration.
4. Browse software and equipment.
5. Admin can manage data through basic CRUD.

## Structure
- `backend/` — Spring Boot API
- `database/` — PostgreSQL schema and seed data extracted from the provided spreadsheets
- `frontend/` — Next.js app (to be implemented)
- `IMPLEMENTATION.md` — exact remaining scope for the coding agent

## Tonight's priority
Make the end-to-end demo work before adding extras: frontend -> REST API -> PostgreSQL -> admin edit -> refreshed student view.
