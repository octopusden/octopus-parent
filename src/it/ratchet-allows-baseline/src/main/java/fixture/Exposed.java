package fixture;

import java.util.Date;

// SpotBugs has to find something here or the SpotBugs half of the ratchet proves nothing: with a bug
// count of zero the check goal returns before it ever compares against maxAllowedViolations, which is
// how a broken ratchet stayed invisible (spotbugs-maven-plugin 4.9.8.0 threw "No such property: EOF"
// on exactly the path that reports a ratchet being satisfied).
public class Exposed {
    private final Date when;

    public Exposed(Date when) {
        this.when = when;
    }

    // EI_EXPOSE_REP: hands out the internal mutable Date.
    public Date getWhen() {
        return when;
    }
}
