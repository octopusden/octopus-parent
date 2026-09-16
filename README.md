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

| Profile | Activates when | Runs |
|---|---|---|
| `octopus-quality` | build JDK ≥ 11 | enforcer (`banDuplicatePomDependencyVersions`), Checkstyle, PMD, SpotBugs, JaCoCo report + line-coverage check |
| `octopus-kotlin-quality` | build JDK ≥ 11 **and** `src/main/kotlin` exists in the module | detekt, ktlint; turns SpotBugs off for that module |
| `octopus-mutation` | `-Poctopus-mutation` only (opt-in; needs JDK ≥ 11) | PIT mutation testing |

Rulesets are the files bundled in the Gradle convention plugin, pinned to an octopus-base tag
(`octopus.quality.config.url`), so a Maven and a Gradle repository judge code by identical rules.
Tool engines are pinned to the same versions as `gradle-quality-plugin/gradle.properties`.

### Phase: `prepare-package`

The gates run at **`prepare-package`**, not `verify`, because the shared CI workflow
(`common-java-maven-build.yml`) invokes **`mvn package`** — and its `mvn-parameters` input is
appended after the goal, so a consumer cannot add a phase from the workflow either. A `verify`-bound
gate would never run on a push or a pull request; its first execution would be inside the release
job's `mvn deploy`, mid-publish. `prepare-package` runs after `test` and before `package`, so
`package`, `verify`, `install` and `deploy` all reach it.

So the command that reproduces CI locally is `mvn package`.

**Reports are not collected by CI yet.** `common-java-maven-build.yml` has no `upload-artifact`
step; the files below exist in the build workspace. Collecting them is the follow-up
`common-java-maven-quality-gates.yml` in octopus-base.

### SpotBugs is Java-only

`octopus-kotlin-quality` sets `spotbugs.skip=true`, matching the Gradle plugin, which runs SpotBugs
only where a module has Java and no Kotlin — on Kotlin bytecode it is overwhelmingly false positives
(`lateinit`, DSL getters, synthetic accessors). Checkstyle and PMD still run on a Kotlin module's
Java sources.

### The JDK condition

The analyzers need a JDK 11+ *runtime*. A repository whose CI still builds on JDK 8 sees no change
from this parent until it moves the build JDK — the bytecode target is separate
(`maven.compiler.source/target` stay `1.8`).

This parent does not pick a baseline JDK and does not change the org's JVM policy: bytecode stays
Java 8, an ordinary build still runs on JDK 8, and the analyzers require 11+. Whether the org
standardises on a main JDK, and against which support matrix, is a separate decision this change
deliberately leaves open — the profiles work on whatever JDK ≥ 11 a repository chooses.

### Properties

| Property | Default | Meaning |
|---|---|---|
| `octopus.quality.failOnViolation` | `false` | Whether a finding fails the build. Same default as the Gradle plugin: a parent bump never reddens a repository; reports are produced on every build; flip to `true` per repository once it is clean. |
| `octopus.quality.skip` | `false` | Turns every gate off. Works from the consumer POM, from `-D` and from `settings.xml`. |
| `octopus.coverage.minimumLine` | `0.10` | Per-module JaCoCo line-coverage floor (`BUNDLE` / `LINE` / `COVEREDRATIO`). |
| `octopus.mutation.threshold` | `0` | PIT mutation-score floor. Raise it as a ratchet, never lower it. |
| `octopus.quality.config.url` | octopus-base tag | Base URL of the ruleset files. |
| `octopus.quality.config.overwrite` | `false` | Re-download the rulesets every build, bypassing the download plugin's own `~/.m2` cache as well. Set it when pointing `config.url` at a moving branch; the pinned tag URL is immutable, so the cache is safe. |

`octopus.quality.skip` is a plugin parameter, not a profile activation condition, and deliberately
so: Maven evaluates profile property activation against system and user properties only, so a
POM-level property cannot deactivate a profile.

### Kotlin repositories

ktlint reads `.editorconfig` from the repository root and there is no way to point it elsewhere,
so copy the org one once:

```bash
curl -sO "$(mvn help:evaluate -Dexpression=octopus.quality.config.url -q -DforceStdout)/.editorconfig"
```

Kotlin files under `src/main/java` are analysed too (detekt reads `src/main` and `src/test`). The
profile registers `src/main/kotlin` and `src/test/kotlin` as Maven source roots so ktlint sees them
even when a repository hands them to `kotlin-maven-plugin` through `<sourceDirs>` only.

### Mutation testing

```bash
mvn -Poctopus-mutation verify
```

`verify`, not `package`: PIT is bound to `verify` deliberately — it re-runs the covering tests once
per mutant, so it stays off the `package` path the gates use. The profile is opt-in only and has no
`<activation>`; it needs a JDK 11+ build (pitest-maven 1.30.0 is Java 11 bytecode).

`pitest-junit5-plugin` declares `junit-platform-launcher` as `provided` and, unlike surefire, PIT
does not auto-provision it. A repository whose test classpath has no launcher adds
`org.junit.platform:junit-platform-launcher` in test scope; a JUnit 4-only repository adds
`org.junit.vintage:junit-vintage-engine` as well.

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

### Known limitation: JaCoCo and a late surefire

The coverage gate reads `jacoco.exec`, which surefire writes when tests run. A module that moves its
tests after `prepare-package` — `octopus-artifactory-npm-maven-plugin` disables `default-test` and
runs surefire at `verify` — produces no execution data by the time the gate runs, and JaCoCo skips
with "Skipping JaCoCo execution due to missing execution data file". A skip reads the same as a pass
in the log. Such a module should move its tests back to the `test` phase or override the JaCoCo
executions to a later phase.

### Rolling a repository onto the gates

1. Bump the parent. On JDK 8 CI nothing changes; on JDK 11+ the build now prints findings and writes
   reports, and still passes.
2. Fix the findings. Kotlin first: `mvn initialize ktlint:format` — nearly all Kotlin findings are
   formatting. The `initialize` phase is required, not decorative: it is where the Kotlin source roots
   are registered, and `mvn ktlint:format` alone reports `0 file(s) formatted` on a repository that
   declares Kotlin through `kotlin-maven-plugin` `<sourceDirs>`. Measured on octopus-releng-lib: 166
   ktlint findings before, 6 after — the remainder are wildcard imports and property naming, which
   ktlint cannot rewrite.
3. Set `<octopus.quality.failOnViolation>true</octopus.quality.failOnViolation>` in the repository POM.

Also in `dependencyManagement`: `nl.jqno.equalsverifier:equalsverifier-nodep` (test scope), for
equals/hashCode contract tests — the linters above only check that both methods are overridden.

## The gate contract is tested

`mvn -Pit verify` (JDK 11+) runs the fixtures in `src/it` as real consumer projects. Each asserts one
promise this README makes, and each encodes a defect that actually shipped during review — the rules
were never the problem, the wiring was. The CI build runs them on every push and pull request.

| Fixture | Asserts |
|---|---|
| `violation-detected` | report-only finds and reports violations without failing; the org ruleset and `includeTests=true` reach the **forked** analysis goals |
| `strict-fails` | `failOnViolation=true` fails the build |
| `skip-disables` | `octopus.quality.skip` set in the consumer POM disables everything and writes no report |
| `consumer-gate-preserved` | a consumer's own PMD gate keeps its own failing semantics; the parent does not downgrade it |
| `pit-opt-in` | PIT does not execute without `-Poctopus-mutation` |
| `kotlin-gates` | ktlint scans `src/test/kotlin` with Kotlin declared only via `<sourceDirs>`; detekt reports; SpotBugs is off for a Kotlin module |

Each fixture was verified to fail when its defect is reintroduced, not merely to pass today.

## Release checks

The enforcer `validate` execution requires the POM metadata Maven Central rejects a release without
(name, description, url, license, developer, scm) and Maven ≥ 3.6.3. `build-helper-maven-plugin` is
pinned to 3.4.0 for that floor: 3.5.0+ declares `requiredMavenVersion` 3.9.0, which would put the
real requirement above the enforced one.

`DependencyConvergence` is not a gate — on a legacy consumer it fails on dozens of transitive
Maven/Plexus splits — but stays available as `mvn enforcer:enforce`.
