// The build must fail, and on the COVERAGE rule rather than on an analyser: octopus.quality.failOnViolation
// stays false here, so this proves the coverage switch works independently.
def log = new File(basedir, 'build.log').text
assert log.contains('Rule violated') : 'the coverage rule did not fire'
assert log.contains('jacoco') : 'the failure did not come from JaCoCo'
return true
