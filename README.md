# octopus-parent

Parent POM for all Octopus Maven-based projects: Maven Central publication, release metadata
checks, and — from 2.1.0 — the same quality gates the Gradle repositories get from the
`org.octopusden.octopus-quality` convention plugin in
[octopus-base](https://github.com/octopusden/octopus-base).

```xml
<parent>
    <groupId>org.octopusden.octopus</groupId>
    <artifactId>octopus-parent</artifactId>
    <version>2.1.0</version>
</parent>
```

## Quality gates

Everything is bound to `verify`, so `mvn verify` locally is exactly the gate CI runs.

| Profile | Activates when | Runs |
|---|---|---|
| `octopus-quality` | build JDK ≥ 11 and `octopus.quality.skip` unset | enforcer (`banDuplicatePomDependencyVersions`), Checkstyle, PMD, SpotBugs, JaCoCo report + line-coverage check |
| `octopus-kotlin-quality` | as above **and** `src/main/kotlin` exists in the module | detekt, ktlint |
| `octopus-mutation` | `-Poctopus-mutation` only | PIT mutation testing (`target/pit-reports/`), JUnit Platform adapter included — a JUnit 4-only repository adds `org.junit.vintage:junit-vintage-engine` (test scope) |

Rulesets are the files bundled in the Gradle convention plugin, pinned to an octopus-base tag
(`octopus.quality.config.url`), so a Maven and a Gradle repository judge code by identical rules.
Tool engines are pinned to the same versions as `gradle-quality-plugin/gradle.properties`.

### The JDK condition

The analyzers need a JDK 11+ *runtime*. A repository whose CI still builds on JDK 8 sees no
change from this parent until it moves the build JDK to 11 or 17 — the bytecode target is
separate (`maven.compiler.source/target` stay `1.8`). Moving the build JDK is what switches the
gates on; nothing else needs to change in the consumer.

This parent does not pick a baseline JDK and does not change the org's JVM policy: bytecode stays
Java 8, an ordinary build still runs on JDK 8, and the analyzers require 11+. Whether the org
standardises on a main JDK, and against which support matrix, is a separate decision this change
deliberately leaves open — the profiles work on whatever JDK ≥ 11 a repository chooses.

### Properties

| Property | Default | Meaning |
|---|---|---|
| `octopus.quality.failOnViolation` | `false` | Whether a finding fails the build. Same default as the Gradle plugin: a parent bump never reddens a repository; reports are produced on every build; flip to `true` per repository once it is clean. |
| `octopus.quality.skip` | unset | Set to anything to deactivate both quality profiles. |
| `octopus.coverage.minimumLine` | `0.10` | Per-module JaCoCo line-coverage floor (`BUNDLE` / `LINE` / `COVEREDRATIO`). |
| `octopus.mutation.threshold` | `0` | PIT mutation-score floor. Raise it as a ratchet, never lower it. |
| `octopus.quality.config.url` | octopus-base tag | Base URL of the ruleset files. Override to try a ruleset change before it is tagged. |

### Kotlin repositories

ktlint reads `.editorconfig` from the repository root and there is no way to point it elsewhere,
so copy the org one once:

```bash
curl -sO "$(mvn help:evaluate -Dexpression=octopus.quality.config.url -q -DforceStdout)/.editorconfig"
```

Kotlin files under `src/main/java` are analysed too (detekt input is `src/`). The profile registers
`src/main/kotlin` and `src/test/kotlin` as Maven source roots so ktlint sees them even when a repository
hands them to `kotlin-maven-plugin` through `<sourceDirs>` only.

### Reports

| Tool | Path (per module) |
|---|---|
| Checkstyle | `target/checkstyle-result.xml` |
| PMD | `target/pmd.xml` |
| SpotBugs | `target/spotbugsXml.xml` |
| JaCoCo | `target/site/jacoco/` |
| detekt | `target/detekt/detekt.{xml,html,sarif}` |
| ktlint | `target/ktlint/ktlint.xml` (Checkstyle format) |
| PIT | `target/pit-reports/` |

### Rolling a repository onto the gates

1. Bump the parent. On JDK 8 CI nothing changes; on JDK 11+ the build now prints findings and
   uploads reports, and still passes.
2. Fix the findings. Kotlin first: `mvn initialize ktlint:format` — nearly all Kotlin findings are
   formatting. The `initialize` phase is required, not decorative: it is where the Kotlin source roots
   are registered, and `mvn ktlint:format` alone reports `0 file(s) formatted` on a repository that
   declares Kotlin through `kotlin-maven-plugin` `<sourceDirs>`. Measured on octopus-releng-lib: 166
   ktlint findings before, 6 after — the remainder are wildcard imports and property naming, which
   ktlint cannot rewrite.
3. Set `<octopus.quality.failOnViolation>true</octopus.quality.failOnViolation>` in the repository POM.

Also in `dependencyManagement`: `nl.jqno.equalsverifier:equalsverifier-nodep` (test scope), for
equals/hashCode contract tests — the linters above only check that both methods are overridden.

## Release checks

The enforcer `validate` execution requires the POM metadata Maven Central rejects a release
without (name, description, url, license, developer, scm) and Maven ≥ 3.6.3.
`DependencyConvergence` is not a gate — on a legacy consumer it fails on dozens of transitive
Maven/Plexus splits — but stays available as `mvn enforcer:enforce`.
