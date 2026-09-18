// The consumer declares archunit-junit5 with NO version, so this fails if the parent stops managing it.
// Both rules must actually execute - a passing build with no tests run would prove nothing.
def log = new File(basedir, 'build.log').text
assert log.contains('Tests run: 2') : 'the ArchUnit rules did not run'
assert !log.contains("'dependencies.dependency.version' for com.tngtech.archunit") : 'the version is not managed by the parent'
return true
