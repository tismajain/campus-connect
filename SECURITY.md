# Security baseline

Security is part of the MVP acceptance criteria. The app may expose public read-only campus resource data, but **all state-changing/admin operations must be authenticated and authorized**.

## Threat model for this MVP

Protect against the realistic risks for a public campus resource site:
- unauthenticated users modifying/deleting resource data
- leaked database/admin credentials
- overly broad CORS
- SQL injection / unsafe dynamic queries
- XSS from resource/admin text fields
- oversized or abusive search/input requests
- verbose exception/stack-trace leakage
- insecure deployment over plain HTTP
- accidental exposure of database internals or admin endpoints

This is not a banking system; keep the implementation simple, but do not ship open write endpoints.

## Backend requirements (Spring Boot)

### 1. Authentication + authorization
Use Spring Security.

Public, read-only routes may be anonymous:
- `GET /api/labs/**`
- `GET /api/software/**`
- `GET /api/equipment/**`
- `GET /api/search`

All `/api/admin/**` routes and all POST/PUT/PATCH/DELETE resource mutations must require an authenticated `ADMIN` role.

For tonight's MVP, use a single admin account loaded only from environment variables:
- `ADMIN_USERNAME`
- `ADMIN_PASSWORD`

Use Spring Security HTTP Basic for the admin API, only over HTTPS in deployed environments. Encode/verify the password with Spring Security's password encoder; never commit a password or password hash generated from a real secret.

Do not expose a public signup endpoint.

### 2. CORS
Do not use `*` for deployed CORS.

Read an exact frontend origin from `FRONTEND_ORIGIN` (default to `http://localhost:3000` for local development). Allow only required methods/headers. Credentials/Authorization should only be accepted from that configured origin.

### 3. CSRF
The API is stateless and admin credentials are sent explicitly in the Authorization header by the frontend. Configure Spring Security appropriately for a stateless REST API; do not add cookie/session authentication without revisiting CSRF protection.

### 4. Database safety
- Use Spring Data JPA repositories / parameterized queries only.
- Never build SQL by concatenating user input.
- Use a dedicated DB user with only the permissions the app needs.
- DB credentials come from environment variables only (`DB_URL`, `DB_USERNAME`, `DB_PASSWORD`).
- Do not expose DB ports publicly in production.

### 5. Input validation
Use request DTOs plus Jakarta Bean Validation instead of binding admin JSON directly to persistence entities.

At minimum:
- trim strings
- reject blank required names/codes
- cap text lengths to match schema
- validate numeric ranges (RAM/GPU memory/quantity >= 0 and reasonable upper bounds)
- validate status against an enum/allow-list
- search query: trim, reject/return empty for blank input, max length 100 characters
- reject unknown/malformed IDs cleanly

Never accept client-provided database IDs for create operations.

### 6. XSS / rendering
React escapes text by default. Keep resource content as plain text.
- Do not use `dangerouslySetInnerHTML` for DB content.
- Do not render arbitrary HTML/Markdown submitted from admin fields.
- Do not use `eval`, dynamic script injection, or untrusted URLs as script/image sources.

### 7. Errors and information leakage
Return consistent JSON error responses with appropriate HTTP codes (400/401/403/404/409/500).
Do not return stack traces, SQL errors, environment values, or internal exception messages to the browser.
Log server-side details without logging passwords or Authorization headers.

### 8. Security headers
Configure sensible headers through Spring Security / deployment platform:
- Content-Security-Policy suitable for the final frontend/backend split
- `X-Content-Type-Options: nosniff`
- frame protection (`frame-ancestors 'none'` or equivalent)
- `Referrer-Policy: no-referrer` or `strict-origin-when-cross-origin`
- HSTS in HTTPS production

Do not weaken these globally just to fix a local development issue.

### 9. Statelessness
Use stateless API security (`SessionCreationPolicy.STATELESS`). Do not create server sessions for Basic auth.

### 10. Admin frontend
- `/admin` must ask for/administer credentials locally for the session and attach the Basic Authorization header only to admin API requests.
- Do **not** persist the admin password to localStorage, IndexedDB, source code, or committed `.env` files.
- Prefer in-memory/session-tab state. Clearing/refreshing may require login again; that is acceptable for the MVP.
- Show generic `Invalid credentials` on 401/403.
- Student-facing pages must never need admin credentials.

## Frontend/deployment requirements

### Environment variables
Only expose values prefixed for public frontend use when they are genuinely public, e.g. API base URL. Never expose DB/admin secrets through `NEXT_PUBLIC_*`.

Provide `.env.example` files containing names/placeholders only, never real credentials.

### HTTPS
Production/deployed demo must use HTTPS. Do not recommend sending Basic credentials over HTTP outside localhost.

### Dependencies
Keep dependencies minimal and current. Do not add packages for capabilities already provided by Spring/Next.js. Run available package/build checks; if audit commands are available, record serious findings in README rather than hiding them.

## Acceptance tests

Before considering the MVP complete, verify:
1. Anonymous GET `/api/labs` succeeds.
2. Anonymous POST/PUT/DELETE under `/api/admin/**` returns 401/403.
3. Correct admin credentials can modify a resource.
4. Wrong credentials cannot modify a resource.
5. Changing the frontend origin away from the configured `FRONTEND_ORIGIN` is not accepted by CORS.
6. Search strings containing quotes / SQL-like characters do not alter query structure or error the DB.
7. HTML/script-like text stored as resource text is displayed as escaped text, not executed.
8. Invalid numeric/status values return 400.
9. Production config contains no committed secrets.
10. The deployed frontend/backend use HTTPS.
