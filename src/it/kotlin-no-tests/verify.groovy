// detekt-maven-plugin 1.23.8 runs only when every listed input directory exists. With a main+test input
// list, a Kotlin module carrying no test tree silently produced NO report at all — main sources included,
// logged at INFO. This fixture is that module.
def detekt = new File(basedir, 'target/detekt/detekt.xml')
assert detekt.exists() : 'detekt produced no report for a Kotlin module without src/test'
assert new File(basedir, 'target/ktlint/ktlint.xml').exists() : 'ktlint produced no report'
return true
