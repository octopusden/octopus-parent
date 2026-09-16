// Report-only is the default: violations must be FOUND and REPORTED without failing the build.
def cs = new File(basedir, 'target/checkstyle-result.xml')
def pmd = new File(basedir, 'target/pmd.xml')
assert cs.exists() : 'checkstyle report missing — the gate did not run under `mvn verify`'
assert pmd.exists() : 'pmd report missing — the gate did not run under `mvn verify`'
assert cs.text.contains('UnusedImports') : 'checkstyle did not apply the org ruleset'
assert pmd.text.contains('EmptyCatchBlock') : 'pmd reported nothing'

// `pmd:check` FORKS `pmd:pmd`, and the fork does not read the execution's configuration. A violation in
// a TEST source is only reported when includeTests=true actually reaches the forked goal, so this is the
// discriminator: with the configuration on the execution instead of the plugin, PMD silently analysed
// main sources only, with its DEFAULT ruleset.
assert pmd.text.contains('SampleTest') : 'pmd did not analyse test sources — the forked goal lost its configuration'

// Checkstyle does not fork, but the same promise applies to it.
assert cs.text.contains('SampleTest') : 'checkstyle did not analyse test sources'
return true
