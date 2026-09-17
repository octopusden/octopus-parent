// No other fixture proves SpotBugs actually analyses: the report-only one checks Checkstyle and PMD, the
// skip and Kotlin ones assert absence, and strict mode stops at Checkstyle first. Without this the SpotBugs
// execution could go inert while the suite stayed green.
def sb = new File(basedir, 'target/spotbugsXml.xml')
assert sb.exists() : 'SpotBugs produced no report on a Java module'
assert sb.text.contains('BugInstance') : 'SpotBugs ran but detected nothing — the analysis is inert'
return true
