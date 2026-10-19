// By default a consumer's own unversioned plugin is reported, not fatal (the build passing is the
// invoker default). The warning must name the consumer's plugin and nothing the parent pins.
def log = new File(basedir, 'build.log').text
def missing = log.readLines().findAll { it.contains('The version currently in use is') }
assert missing.any { it.contains('exec-maven-plugin') } : 'no requirePluginVersions warning for exec-maven-plugin'
assert missing.every { it.contains('exec-maven-plugin') } : "parent-managed plugins reported as unpinned: ${missing}"
return true
