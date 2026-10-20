# Contributor implementation instructions

Read README.md and docs/architecture/system-overview.md before editing. The historical target
architecture is background; current product behavior and explicit user instructions take precedence.

- Preserve existing changes and use small, reviewable increments.
- Keep the modular monolith, Spring Boot backend and React/TypeScript console. Domain dependencies point inward; the domain must not depend on Spring/JPA/Kubernetes.
- Web UI, REST and YAML use one versioned, engine-neutral specification. Keep versions immutable and execution plan digests verified.
- JMeter and Playwright generate hosted material load in isolated ephemeral Kubernetes Jobs. Local process adapters are development-only. Do not introduce another load engine without approval.
- PostgreSQL is authoritative for definitions, runs, transitions and summaries. Private object storage is authoritative for runner evidence. Pod-local files are temporary.
- Every trigger is idempotent. Cancellation/failure cleanup must be durable, confirmed before terminal state, and must not replay material load after restart.
- Enforce team authorization, quotas, target restrictions and secret references server-side. Never store credentials or private infrastructure coordinates in source or logs.
- Keep public branding separate from authenticated APIs. Name, logo and colors are operator configuration.
- Use Flyway for schema changes and preserve compatibility intentionally. Record unavailable measurements honestly; never substitute units/percentiles or invent passing results.
- Add meaningful regression tests, run the checks in README.md, and update the relevant usage/configuration/extension documentation.
- Preserve MIT and third-party licensing notices. Do not change dependency/platform baselines or publish/deploy/merge without authorization.
