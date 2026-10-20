# Maintainer starting points

1. Read README.md, AGENTS.md and docs/architecture/system-overview.md.
2. Run git status --short and preserve existing work.
3. Run the backend, UI and runner checks documented in README.md.
4. For deployment, copy helm/performance-platform/examples/company-values.yaml and follow the company installation guide.
5. Validate small real runs, target authorization, failure cleanup, cancellation and restart recovery before calibrating larger workloads.
