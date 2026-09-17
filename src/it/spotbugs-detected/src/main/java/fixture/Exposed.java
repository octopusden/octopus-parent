package fixture;

import java.util.Date;

public class Exposed {
    private final Date when;

    public Exposed(Date when) {
        this.when = when;
    }

    // EI_EXPOSE_REP: returns the internal mutable Date directly.
    public Date getWhen() {
        return when;
    }
}
