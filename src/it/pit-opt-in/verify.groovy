// octopus-mutation must stay opt-in: any <activation> on that profile turns `mvn verify` into a
// mutation run. Asserting only on the report directory is too weak — PIT can fail before writing one —
// so assert the plugin never executed at all.
def log = new File(basedir, 'build.log').text
assert !log.contains('pitest-maven') : 'pitest-maven executed without -Poctopus-mutation'
assert !log.contains('mutationCoverage') : 'mutationCoverage ran without -Poctopus-mutation'
assert !new File(basedir, 'target/pit-reports').exists() : 'PIT produced reports without -Poctopus-mutation'
return true
