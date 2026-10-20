# Product plan

Deliver a self-hosted platform that lets consumers describe requirements, validate a canonical
specification, execute isolated API/browser workloads and inspect measured evidence.

Keep domain, engine SPI, orchestration, persistence, results and API responsibilities separate.
All consumer channels use the same immutable definition versions. Run state and evidence must
survive control-plane restarts. Failed runners must stop before admission capacity is released.

Near-term work: operator installation acceptance and controlled calibration for five simultaneous
team runs. Follow with reviewed provider adapters and consumer usability improvements.
