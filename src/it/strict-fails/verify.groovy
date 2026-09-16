// The build must fail (invoker.properties), and it must fail on a LINTER rather than on coverage or any
// other unrelated gate — otherwise this fixture would pass for the wrong reason.
def log = new File(basedir, 'build.log').text
assert log.contains('maven-checkstyle-plugin') && log.contains('Checkstyle violation') :
        'strict mode did not fail on Checkstyle'
return true
