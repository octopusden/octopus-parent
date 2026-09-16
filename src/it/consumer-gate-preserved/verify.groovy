// invoker.properties expects a failure; without this the fixture would only assert "failed somehow".
// The failure must come from the CONSUMER's own PMD execution, proving the parent did not downgrade it.
def log = new File(basedir, 'build.log').text
assert log.contains('consumer-pmd') : "the consumer's own PMD execution did not run"
assert log.contains('(consumer-pmd)') && log.contains('PMD') : 'the build did not fail on the consumer PMD gate'
return true
