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
