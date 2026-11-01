# Campus Connect

A campus resource directory built with Spring Boot, PostgreSQL and Next.js. Students can search labs, compare RAM/GPU specifications, browse embedded equipment and find software used by subjects. Admin changes persist in PostgreSQL and appear on the next student page refresh.

## Source facts

The committed CSVs are authoritative: 28 labs, 7 equipment entries, and **60 subject/software associations across 36 subjects**. The earlier brief called these 36 mappings; the import preserves every CSV association. AB4-406 and AB4-411 each list HP G9 Tower systems, 16 GB RAM and 12 GB GPU. AB5-303 lists Apple M3 systems with 8 GB RAM. AB5-401 spans 16–64 GB RAM.

Software records describe subject use, never inferred physical installations. `lab_software` remains empty. Equipment has no seeded lab location or quantity because the source provides neither. Initial directory status is the schema default `AVAILABLE`, not a claim of live occupancy. Source configuration is a lossless labeled transcription of each CSV row, not an invented original spreadsheet.

## Local setup

Requirements: Java 21, Maven 3.9+, Node.js 22+, npm and PostgreSQL 17 (with the pg_trgm extension available). Run all commands from the repository root unless noted.

Create a dedicated local database account using a PostgreSQL administrator. `createuser` prompts for a new local password; use that same value in the backend environment. The account has no superuser, database-creation or role-creation privileges.

```sh
createuser --pwprompt --no-superuser --no-createdb --no-createrole campus_app
createdb --owner=campus_app campus_connect
```

Flyway creates the schema and imports the CSV-derived data exactly once on an empty database. Do not run `database/schema.sql` manually first. Restarting does not overwrite admin edits. An existing prototype database with manually created tables should be replaced with a new empty development database; migrations intentionally do not baseline unknown state.

Start the API in terminal 1:

```sh
cd backend
cp .env.example .env
# Fill DB_PASSWORD, ADMIN_USERNAME and ADMIN_PASSWORD (12+ characters) in .env.
# Values must be shell-safe/quoted because the next command sources this file.
set -a
. ./.env
set +a
mvn spring-boot:run
```

DB/admin configuration has no credential defaults. Startup fails if required variables are missing; admin passwords are encoded with Spring Security's delegating password encoder in memory.

Start the frontend in terminal 2:

```sh
cd frontend
cp .env.example .env.local
npm install
npm run dev
```

Visit http://localhost:3000. Search `GPU`, open AB4 · 406, then visit `/admin`, sign in, choose **Lab systems**, edit its RAM values and save. Refresh the student detail page to see the change. Source configuration is retained independently of current edited specifications. Restore demo changes after testing if you need the original inventory again.

## API

Public GET routes: `/api/labs` (optional `q`, `building=AB4|AB5`, `gpu=true`, `minRam=16`), `/api/labs/{id}`, `/api/software`, `/api/equipment`, `/api/search?q=GPU`. Software searches names and subjects; equipment searches names/categories; lab searches include building, room, summaries and source configuration. Search is case-insensitive literal matching, limited to 100 characters. Blank `/search` returns empty groups; unfiltered browse routes list records. The small source inventory is filtered in application memory after JPA reads; no user text is interpolated into SQL.

Authenticated admin routes require HTTP Basic and role `ADMIN`: GET `/api/admin/session`, GET `/api/admin/buildings`, POST `/api/admin/{labs|lab-systems|software|equipment}`, PUT `/api/admin/{type}/{id}`, DELETE `/api/admin/{lab-systems|software|equipment}/{id}`. Lab deletion is intentionally omitted to protect related records. Software deletion also deletes its subject associations; the UI confirms this. Software edits preserve existing subject associations. No public signup or session cookies.

Write DTOs reject unknown properties (including create IDs), blank required fields, invalid statuses, out-of-range integers, fractional integer input, reversed RAM ranges and GPU memory without a GPU. Strings are trimmed; names and categories respect schema lengths; descriptions/source text are capped at 4,000 characters. Errors use generic JSON messages, with server details logged only server-side. UI content is plain React text.

## Verification

With the backend environment exported and a disposable PostgreSQL database configured:

```sh
cd backend
mvn --batch-mode verify
```

Tests use the real PostgreSQL schema/migrations with transactional rollback. They check data counts, GPU discovery, persistence through a cleared JPA context, CRUD, wrong/no credentials, non-admin denial, exact-origin CORS, headers, SQL-like searches, unknown IDs, malformed JSON, numeric/status validation and safe conflicts.

```sh
cd frontend
npm install
npm run build
npm run typecheck
npm audit --omit=dev --audit-level=high
npx playwright install --with-deps chromium
# Start the packaged backend first; export the same ADMIN_USERNAME/ADMIN_PASSWORD here.
npm run test:e2e
```

Playwright starts the production frontend. Desktop and mobile tests exercise student discovery, RAM edits through the admin UI, refreshed public detail, wrong credentials, memory-only login, escaped script-like resource text, and horizontal overflow. Test mutations are restored/deleted in `finally`. Screenshots of home, GPU results, detail and admin are saved under `frontend/test-results/screenshots/`.

`.github/workflows/verify.yml` runs these checks with PostgreSQL, Java 21, Node 22 and ephemeral admin credentials. The build environment used for initial authoring has no Java/Maven/browser or network access to npm; see `docs/VERIFICATION.md` for actual run evidence and remaining limits. Do not equate the existence of tests with passing results.

## Deployment and security

Use HTTPS for both services. Set `REQUIRE_HTTPS=true` on the API and frontend; configure an HTTPS listener or trusted TLS-terminating proxy. Spring does not trust forwarded headers by default. Behind a proxy, set `SERVER_FORWARD_HEADERS_STRATEGY=framework` **only when the API is network-restricted to that proxy and the proxy removes/replaces client forwarding headers**; otherwise an attacker can spoof HTTPS. Configure the platform to redirect HTTP to HTTPS. HSTS is emitted on secure API responses and on the production frontend when enabled. Basic authentication is acceptable over localhost HTTP for development only.

Set `FRONTEND_ORIGIN` to exactly the frontend HTTPS origin, without a trailing slash. Only that origin may send browser admin API requests. All API mutations and admin reads require `ADMIN`; unspecified routes are denied. CSRF is disabled for explicitly header-authenticated, stateless requests; do not add cookie/session auth without revisiting it. The frontend uses `credentials: omit` and attaches Authorization only on admin calls. Passwords live in tab memory, discarded on refresh/navigation/sign-out; never localStorage, IndexedDB or a public environment variable.

Only `NEXT_PUBLIC_API_BASE_URL` is public. Set it at frontend build time to the HTTPS API origin, reachable from both the Next server and browsers. Supply `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `ADMIN_USERNAME`, `ADMIN_PASSWORD` through the host's secret manager. Do not commit `.env` files. The CI database password is an isolated throwaway service fixture, never a deployed credential; CI generates the admin secret at runtime.

Keep PostgreSQL private. For production, run Flyway with a dedicated migration owner, then run the API with a separate restricted runtime role: CONNECT to this DB, USAGE on the application schema, SELECT/INSERT/UPDATE/DELETE on resource/join tables, SELECT on `flyway_schema_history`, and USAGE/SELECT on relevant sequences. Set `SPRING_FLYWAY_ENABLED=false` for the runtime process after migrations. Do not grant schema creation or database ownership to the runtime role. The local owner account above simplifies first-run setup only.

Spring supplies nosniff, frame denial and HSTS; both services set CSP and referrer policy. Next's CSP allows its framework's inline hydration scripts; database text is never rendered as HTML. Apply request-size limits (64 KB admin JSON is sufficient), request/connection timeouts, and rate limits at the deployment proxy. Keep platform logs free of Authorization headers. There is no live occupancy, booking, map, or inferred software installation feature.

## UI review

Direction: practical, exact, quietly academic. Source-backed location labels, equipment-card specifications, compact inventory rows, hairline borders, graphite text and one restrained campus-blue accent. No decorative metrics, gradients, emoji, generic marketing visuals, or animated filler. Mobile filters stack above results; hardware details stay visible.

The requested [unslop skill](https://github.com/heydamir/unslop/tree/main/skills/unslop) by [Damir Sokolovsky](https://x.com/heydamir), CC BY 4.0, informed the review. See `docs/UI_REVIEW.md` for direction and pass-by-pass evidence.
