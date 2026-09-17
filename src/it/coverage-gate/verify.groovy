// No fixture ran a real test, so prepare-agent / report / check could all have been broken while every
// fixture stayed green — JaCoCo skips silently on a missing exec file, and a skip reads like a pass.
assert new File(basedir, 'target/jacoco.exec').exists() : 'prepare-agent did not arm: no jacoco.exec'
def report = new File(basedir, 'target/site/jacoco/jacoco.xml')
assert report.exists() : 'JaCoCo produced no XML report'
assert report.text.contains('Calc') : 'the coverage report does not mention the class under test'
def log = new File(basedir, 'build.log').text
assert log.contains('Tests run: 1') : 'the fixture test did not actually run'
assert log.contains('All coverage checks have been met') : 'the coverage gate did not evaluate against real data'
return true
