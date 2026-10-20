# Establishing an installation baseline

Before extending this distribution, run the verification commands in README.md and record the
revision, toolchain, actual test results and known limitations in your own release process.

Use PostgreSQL integration tests as the persistence authority. Verify that container tests actually
ran, then run UI tests/lint/build, runner checks and Helm validation. Use a disposable approved target
for real API/browser flows, cancellation, restart recovery, target restrictions and team denial.
Keep installation-specific identifiers and credentials outside source control.

Do not treat sample screenshots or local fixture timings as evidence of your application capacity.
