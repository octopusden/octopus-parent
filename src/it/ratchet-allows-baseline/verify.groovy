// Strict mode with a frozen backlog must PASS while still reporting, so a repository can enable
// failOnViolation before the backlog is gone. `strict-fails` covers the other direction: strict with a
// zero ratchet fails on the same sources.
def cs = new File(basedir, 'target/checkstyle-result.xml')
def pmd = new File(basedir, 'target/pmd.xml')
assert cs.exists() && pmd.exists() : 'reports missing — the gates did not run'
assert cs.text.contains('UnusedImports') : 'checkstyle found nothing, so the ratchet proves nothing'
assert pmd.text.contains('EmptyCatchBlock') : 'pmd found nothing, so the ratchet proves nothing'
return true
