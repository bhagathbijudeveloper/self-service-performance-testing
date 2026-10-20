# Maintainer starting points

1. Read README.md, AGENTS.md and docs/architecture/system-overview.md.
2. Run git status --short and preserve existing work.
3. Select JDK 25 using JAVA_HOME, then run the backend, demo, UI and runner checks in README.md.
4. For deployment, copy helm/performance-platform/examples/company-values.yaml and follow the company installation guide.
5. Validate small real runs, target authorization, failure cleanup, cancellation and restart recovery before calibrating larger workloads.
6. Compare virtual threads enabled/disabled on representative operator infrastructure before claiming
   latency, memory or throughput gains. Keep database pools and five-run admission limits explicit.
7. Plan Jackson 3 adoption with persisted JSON/digest fixtures before removing the compatibility module.
