# Maintainer handoff

This distribution is a standalone self-service performance testing platform. Start with README.md
and docs/deployment/company-installation.md. Configure your own Kubernetes, PostgreSQL, private
S3-compatible storage, registry, identities, networking and branding.

The implementation includes the canonical specification, durable reconciliation, JMeter and
Playwright runners, scoped authorization, importers, consumer wizard, reports and a portable CLI.
Default admission is five runs, one per team; per-run protocol limits are 1,000 users and one hour.
Browser concurrency is separate. Failure cleanup retains capacity until termination is observed.

Use the verification commands in README.md before publishing changes. Human OIDC, native Blob/GCS,
private Git credentials, BrowserStack and remote runner federation remain extension work.
No cloud infrastructure or credentials are included in the source distribution.

## Java runtime baseline

The Java application and demo service now require JDK 25 and Spring Boot 4.1.1. Control-plane
virtual threads default on with an operator opt-out; material-load quotas and runner isolation
are unchanged. JMeter keeps its separate pinned Java 17 runner image. See
[runtime and migration notes](docs/operations/java-runtime.md).

Verification: all 1,392 baseline backend/demo test cases retained, plus three real HTTP runtime
cases (1,395 total; no failures, one optional live-recorder skip). PostgreSQL/S3 tests executed.
The 205 UI tests, UI lint/build, six Playwright runner tests, Python runner check, Helm lint and
CLI smoke check passed. The local JMeter Groovy compatibility sampler also completed without error.
The runtime change is not a measured throughput improvement or a five-team capacity certification.
The combined container also passed readiness, bundled-console, OpenAPI and scoped API-access
smoke checks on Temurin 25.0.4.1 / Spring Boot 4.1.1.

Jackson 2 remains an explicit compatibility bridge for existing canonical/stored documents and
REST representations. Its eventual removal requires persisted-document and digest compatibility
fixtures. No schema migrations or saved-definition/plan digests are rewritten by this upgrade.
