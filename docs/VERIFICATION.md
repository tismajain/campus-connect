# Verification record

CI run [41325214113](https://github.com/tismajain/campus-connect/actions/runs/41325214113), commit `200586046e51f1c9b51327402336ea66a62093b1`, passed on 2026-11-01:

- Java 21 / PostgreSQL 17: `mvn --batch-mode verify`, six integration tests, zero failures/errors; production JAR packaged.
- Node 22: `npm ci`, `npm run build`, `npm run typecheck` passed.
- `npm audit --omit=dev --audit-level=high`: zero vulnerabilities.
- Production API startup and real HTTP readiness passed.
- Chromium: four Playwright tests passed across desktop and mobile; verified discovery, GPU rooms, subject software, admin RAM edit persisting to the public detail after refresh, wrong credentials, login clearing on navigation, escaped script-like text, and no page-level horizontal overflow.
- Reviewed eight actual desktop/mobile screenshots of home, GPU results, detail and admin sign-in using unslop. Screenshots and traces are in the run's `verification-evidence` artifact.

An additional verification pass covers keyboard focus for editor/confirmation, lab/system creation/deletion, a 64 KB body cap, HSTS and signed-in admin screenshots. Results will be appended when it completes.

The authoring sandbox itself has Node 18 but no Java/Maven/PostgreSQL/browser, and cannot resolve npm/GitHub through the shell. Executable verification was performed in GitHub Actions through the connected GitHub tools. No production deployment was requested or performed; live deployment TLS and host-level secret/network configuration remain the deployer's responsibility.
