# Campus Connect — Implementation Brief

## Goal
Ship a polished MVP tonight for a campus resource discovery platform serving ~3–4k students.

## Product promise
A student should be able to answer: **“I need X — where on campus can I find it?”**

Examples:
- Where can I use a GPU?
- Which lab has 16 GB+ RAM?
- What software is used for Computer Vision?
- What embedded equipment exists on campus?

## Tech stack
- Frontend: Next.js (App Router) + TypeScript + Tailwind CSS
- Backend: Spring Boot + Spring Data JPA + PostgreSQL
- Keep architecture as a modular monolith. Do not add microservices, Redis, Kafka, Elasticsearch, Kubernetes, or AI search.

## Source-data facts
- 28 computer labs across AB4 and AB5.
- Explicit GPU labs: AB4-406 and AB4-411; each source row says HP G9 Tower, 16 GB RAM, 12 GB GPU.
- AB5-303 contains Apple M3 systems, 8 GB RAM.
- AB5-401 source row contains HP i7, Dell i7 Tower (16 GB), Dell i9 (64 GB RAM).
- Embedded source contains 7 equipment entries including ARM Cortex M3 boards, a 50 MHz digital storage oscilloscope, fingerprint, RFID, Zigbee, graphic LCD, and ARM Cortex M3 project board.
- There are 36 subject -> software mappings.
- **Critical:** the software spreadsheet does NOT say which physical lab each software package is installed in. Do not invent lab/software installation relationships. Display these as “software used by subjects” unless lab installation data is explicitly added later.

## Required frontend screens

### 1. `/` Home
Design: clean, modern, calm institutional UI. Avoid gradients/glassmorphism/overly colorful dashboards.

Include:
- Top nav: Campus Connect, Home, Explore, Labs, Admin
- Hero heading: “Find what you need on campus.”
- Large search input with placeholder: `Search GPU, software, lab, equipment...`
- Quick category chips/cards: Hardware, Software, Labs, Equipment
- Featured cards for GPU Computing, Apple M3 Systems, Embedded Systems
- Search submits to `/explore?q=...`

### 2. `/explore`
- Search bar persists query.
- Tabs or filters for Labs, Software, Equipment.
- Lab filters: Building AB4/AB5 and GPU only.
- Responsive result cards.
- Lab card should show building-room, system summary, RAM range, GPU badge when applicable, and link to detail page.
- Software results should be factual and not claim physical lab availability unless a real lab_software row exists.

### 3. `/labs/[id]`
- Building + room number
- status badge
- system configuration
- min/max RAM
- GPU and GPU memory when present
- source configuration text
- simple breadcrumb back to Explore

### 4. `/admin`
Basic demo CRUD; no authentication required for tonight.
- List labs/systems
- Edit lab status/name/description
- Edit lab-system RAM/GPU fields and summary
- Add/edit/delete equipment
- Add/edit/delete software
- Use confirmation before delete
- Show success/error toast

## Backend API contract
Existing/required GET endpoints:
- `GET /api/labs`
- `GET /api/labs?building=AB4`
- `GET /api/labs?gpu=true`
- `GET /api/labs/{id}`
- `GET /api/software`
- `GET /api/equipment`
- `GET /api/search?q=GPU`

Add admin CRUD endpoints:
- `POST /api/admin/labs`
- `PUT /api/admin/labs/{id}`
- `DELETE /api/admin/labs/{id}` only if safe; otherwise omit deleting labs
- `POST /api/admin/lab-systems`
- `PUT /api/admin/lab-systems/{id}`
- `DELETE /api/admin/lab-systems/{id}`
- `POST /api/admin/software`
- `PUT /api/admin/software/{id}`
- `DELETE /api/admin/software/{id}`
- `POST /api/admin/equipment`
- `PUT /api/admin/equipment/{id}`
- `DELETE /api/admin/equipment/{id}`

Use DTOs for write requests where practical. Validate required fields and return sensible HTTP status codes.

## Search
Search should query lab building/room/system summary/source config + software names + equipment names. Case-insensitive matching is enough for tonight. PostgreSQL trigram indexes already exist for later fuzzy search.

## Frontend API configuration
Use environment variable:
`NEXT_PUBLIC_API_BASE_URL=http://localhost:8080`

Do not hardcode production URLs.

## Styling
- Light background (#F8FAFC-style neutral)
- White cards, subtle borders, small shadows
- Deep navy/blue primary accent
- Inter or system sans font
- 8–12px radii
- Strong spacing and typography hierarchy
- Responsive from mobile through desktop

## Acceptance criteria
1. `mvn spring-boot:run` starts backend with PostgreSQL configured.
2. Next.js frontend starts independently.
3. `/explore` displays real lab data from backend.
4. `GPU` search surfaces AB4-406 and AB4-411.
5. Clicking a lab opens `/labs/[id]` with DB-backed configuration.
6. Admin can edit at least one system field and the student-facing lab detail reflects the change after refresh.
7. Software and equipment are browsable.
8. No fabricated lab/software relationships.
9. README contains exact local setup commands.
10. Prefer a working end-to-end demo over extra features.

## Out of scope tonight
- OAuth/JWT
- favorites
- booking
- real-time occupancy
- maps
- QR codes
- notifications
- chat/forum
- AI chatbot/recommendations
- microservices

## Recommended order
1. Finish JPA entities/repositories/controllers so current GET API compiles.
2. Add CRUD API.
3. Build Next.js frontend.
4. Wire API calls and error/loading states.
5. Add seed data and setup instructions.
6. Run/build both projects and fix compile/runtime issues.
