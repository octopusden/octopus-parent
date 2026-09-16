// octopus.quality.skip must work from the consumer POM, not only from -D.
// NB: an invoker post-build script must return null/true — a returned value is treated as a failure message.
def produced = ['target/checkstyle-result.xml', 'target/pmd.xml', 'target/spotbugsXml.xml'].findAll {
    new File(basedir, it).exists()
}
assert produced.isEmpty() : "reports produced although octopus.quality.skip=true: ${produced}"
return true
