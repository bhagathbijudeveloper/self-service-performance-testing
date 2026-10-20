# Product plan

Deliver a self-hosted platform that lets consumers describe requirements, validate a canonical
specification, execute isolated API/browser workloads and inspect measured evidence.

Keep domain, engine SPI, orchestration, persistence, results and API responsibilities separate.
All consumer channels use the same immutable definition versions. Run state and evidence must
survive control-plane restarts. Failed runners must stop before admission capacity is released.

Near-term work: operator installation acceptance and controlled calibration for five simultaneous
team runs. Follow with reviewed provider adapters and consumer usability improvements.

The implemented runtime baseline is JDK 25 / Spring Boot 4.1.1. Virtual-thread HTTP dispatch and
its opt-out have embedded-server regression coverage; legacy storage names, canonical definitions and
runner contracts are preserved. Roll out new application images through normal review and
operator acceptance. Measure concurrency benefits separately from compatibility verification.
