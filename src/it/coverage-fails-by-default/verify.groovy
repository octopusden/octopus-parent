// invoker.properties expects a failure; without the assertions below the fixture would only prove
// "failed somehow", and every plausible wrong reason here is a way for it to pass vacuously.
def log = new File(basedir, 'build.log').text

// The failure must be COVERAGE, not an analyser: the fixture sets no switches, so a stray Checkstyle
// finding would end the build at prepare-package and this fixture would silently stop testing coverage.
assert log.contains('octopus-jacoco-check') : 'the build did not reach the coverage gate'
assert log.contains('Coverage checks have not been met') : 'the build failed for some reason other than coverage'

// A missing jacoco.exec makes JaCoCo skip, and a skip reads like a pass - so the ratio has to have been
// measured against real execution data rather than inferred from nothing.
assert new File(basedir, 'target/jacoco.exec').exists() : 'prepare-agent did not arm: no jacoco.exec'
assert log.contains('Tests run: 1') : 'the fixture test did not actually run'

// Pin the inherited floor itself. If octopus.coverage.minimumLine changes, this fixture should be
// updated deliberately rather than keep passing against a different number.
assert log.contains('expected minimum is 0.10') : 'the fixture is not being judged against the inherited 0.10 floor'
return true
