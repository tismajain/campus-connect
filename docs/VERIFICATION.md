# Verification record

CI run [41325214113](https://github.com/tismajain/campus-connect/actions/runs/41325214113), commit `200586046e51f1c9b51327402336ea66a62093b1`, passed on 2026-11-01:

- Java 21 / PostgreSQL 17: `mvn --batch-mode verify`, six integration tests, zero failures/errors; production JAR packaged.
- Node 22: `npm ci`, `npm run build`, `npm run typecheck` passed.
- `npm audit --omit=dev --audit-level=high`: zero vulnerabilities.
- Production API startup and real HTTP readiness passed.
- Chromium: four Playwright tests passed across desktop and mobile; verified discovery, GPU rooms, subject software, admin RAM edit persisting to the public detail after refresh, wrong credentials, login clearing on navigation, escaped script-like text, and no page-level horizontal overflow.
- Reviewed eight actual desktop/mobile screenshots of home, GPU results, detail and admin sign-in using unslop. Screenshots and traces are in the run's `verification-evidence` artifact.

The expanded suite passed in [run 41325536480](https://github.com/tismajain/campus-connect/actions/runs/41325536480), commit `cd77c5e60c21814aec1f606d3a3f1cbc0a3bd8da`: seven backend integration tests, both production builds, TypeScript, zero audited runtime vulnerabilities, and all four browser tests. This adds lab/system creation/deletion, reversed RAM/GPU validation, the 64 KB body cap, HSTS and signed-in admin screenshots. The desktop/mobile signed-in tables and system editors were visually inspected; focus brings the active editor into view and fields remain readable. Twelve screen captures across six page/states were reviewed in total.

Final review changes sentence-case admin status labels and disables MockMvc request dumping so test failures cannot print Authorization headers. Screenshot artifacts remain available without dumping image bytes into normal CI logs.

The authoring sandbox itself has Node 18 but no Java/Maven/PostgreSQL/browser, and cannot resolve npm/GitHub through the shell. Executable verification was performed in GitHub Actions through the connected GitHub tools. No production deployment was requested or performed; live deployment TLS and host-level secret/network configuration remain the deployer's responsibility.
