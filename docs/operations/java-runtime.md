# Java runtime and framework upgrades

## Supported baseline

| Component | Runtime |
|---|---|
| Control plane and Java modules | JDK 25; Spring Boot 4.1.1 / Spring Framework 7 |
| Demo service | JDK 25; Spring Boot 4.1.1 |
| Production JMeter Job | Separate pinned Java 17 image; JMeter 5.6.3 |
| Production browser Job | Separate pinned Node.js / Playwright image |

Spring Boot 4.1.1 is the stable release selected for this revision, not the 4.2 milestone.
See [Spring's system requirements](https://docs.spring.io/spring-boot/system-requirements.html).
Java bytecode targets 25, so the application jars cannot run on JDK 17 or 21. CI builds and tests
on JDK 25. The control-plane and demo Dockerfiles use an approved Temurin 25 multi-platform image
pinned by digest. Rebuild these application images for an upgrade; changing only Helm values does
not replace the JVM in an existing image.

Install an approved JDK 25 distribution and set `JAVA_HOME` to its JDK directory. Confirm with
`"$JAVA_HOME/bin/java" -version` and `mvn -version`. On macOS with a registered JDK installation,
`export JAVA_HOME=$(/usr/libexec/java_home -v 25)` selects it. The local launcher prefers JDK 25
and stops with an actionable message if another JDK is selected. No preview flags are needed.

## Virtual threads

The control plane enables Spring's `spring.threads.virtual.enabled` setting. Embedded HTTP request
handlers and Boot-managed asynchronous work use virtual threads. Existing explicitly constructed
executors retain their own behavior. Fixed-delay reconciliation keeps its sequential semantics;
durable leases and admission policy continue to decide which material runs may execute.

JDK 25 includes the synchronization improvements introduced in JDK 24: a virtual thread waiting
inside a `synchronized` block usually releases its carrier OS thread. See
[Oracle's JDK migration notes](https://docs.oracle.com/en/java/javase/25/migrate/significant-changes-jdk-24.html)
and [Spring's virtual-thread guidance](https://docs.spring.io/spring-boot/reference/features/spring-application.html#features.spring-application.virtual-threads).
These changes primarily help blocking I/O concurrency. They do not add CPU cores, increase
database connections, accelerate CPU-bound analysis automatically, or turn JMeter threads into
virtual threads. No throughput improvement is claimed without a comparable workload measurement.

Opt out for diagnosis or infrastructure validation:

```bash
SPRING_THREADS_VIRTUAL_ENABLED=false java -jar platform-core/target/platform-core-1.0.0.jar
```

For Kubernetes use the existing application configuration extension:

```yaml
applicationConfig:
  spring:
    threads:
      virtual:
        enabled: false
```

Restart/roll the control plane after a change. Keep `spring.main.keep-alive=true`: virtual threads
are daemon threads, and scheduler-only operation must not exit just because all remaining threads
are daemons. The five-run admission limit, per-team limit, protocol-user ceiling, browser budget,
database connection pool, timeouts and runner resources still require independent calibration.

## Spring Boot 4 compatibility decisions

- Web MVC, JPA, Flyway and security use the Boot 4 modular starters and test modules. PostgreSQL's
  Flyway database extension is explicit. Production identities and target/run authorization stay
  enforced; the migration does not enable a development identity in production.
- Springdoc 3.1.1 matches the Boot 4 / Spring Framework 7 API. Verify `/api-docs` after upgrading.
- Fabric8 7.9.0 replaces the older Kubernetes client to support the managed Jackson 2 version.
  Job creation, inspection, cancellation and no-replay contracts remain regression-tested.
- Specification/YAML parsing, stored progress JSON and the existing HTTP representation retain
  Jackson 2 through Spring's compatibility module and
  `spring.http.converters.preferred-json-mapper=jackson2`. The domain's independent canonicalizer
  is unchanged. Spring deprecates this compatibility module; a later Jackson 3 migration needs
  parsing, digest and stored-document compatibility fixtures.
- Mockito's Spring integration uses `@MockitoBean`. Tests use the matching Boot 4 test packages
  and the framework-managed JUnit/Surefire versions.
- H2 2.5.252 avoids the managed 2.4.240 cross-connection `CHECK` constraint regression
  ([upstream issue](https://github.com/h2database/h2database/issues/4302)). PostgreSQL remains the
  production database. The legacy p95 column is mapped explicitly to its original Flyway name;
  Hibernate's changed naming strategy must not rename stored columns.

## Verify and roll out

1. Back up PostgreSQL and preserve object-storage evidence and the previous application image.
2. Build on JDK 25 with `mvn clean verify` and
   `mvn -f demo-service/pom.xml clean verify`. Docker must be running: PostgreSQL/S3 tests must
   actually execute. Also run the UI and both runner checks in the repository README.
3. Build the combined control-plane image from `docker/Dockerfile.control-plane`. Build the demo
   image separately if using the bundled target. Publish to your approved registry under immutable
   versions; do not change runner images as an incidental JVM upgrade.
4. Deploy to a disposable environment first. Check startup, readiness, scoped authentication,
   `/api-docs`, saved-definition reads, a small real API/browser run, cancellation and restart
   recovery. Compare historical and new summaries before increasing traffic.
5. Compare API latency, memory, CPU, database-pool wait and reconciliation duration with virtual
   threads enabled and disabled under the same workload. Adjust limits from measured evidence.
6. Roll out through your normal approval process. Keep schema changes separate from this runtime
   upgrade; no Flyway migration or existing definition/plan digest is rewritten by this change.

The HTTP integration test checks actual embedded-server request threads with the default enabled
configuration and with the opt-out; MockMvc alone cannot prove the server executor uses virtual
threads. The probe exists only in test sources, outside the application component scan.
