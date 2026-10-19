// The build must pass with requirePluginVersions failing on any miss, and the rule must actually have run:
// a passing build with the rule skipped would prove nothing.
def log = new File(basedir, 'build.log').text
assert log.contains('Rule 0: org.apache.maven.enforcer.rules.RequirePluginVersions passed') :
        'requirePluginVersions did not run or did not pass'
return true
