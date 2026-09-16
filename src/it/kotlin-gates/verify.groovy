// 1. ktlint must see src/TEST/kotlin, not only src/main/kotlin. The violation lives in the test file.
def ktlint = new File(basedir, 'target/ktlint/ktlint.xml')
assert ktlint.exists() : 'ktlint report missing'
assert ktlint.text.contains('src/test/kotlin') : 'ktlint did not scan src/test/kotlin (source root not registered)'

// 2. detekt must run and report.
assert new File(basedir, 'target/detekt/detekt.xml').exists() : 'detekt report missing'

// 3. SpotBugs must be OFF for a Kotlin module, matching the Gradle convention plugin.
assert !new File(basedir, 'target/spotbugsXml.xml').exists() : 'SpotBugs ran on a Kotlin module'

return true
