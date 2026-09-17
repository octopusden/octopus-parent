package fixture;

import java.util.ArrayList;

public class Sample {
    public void run() {
        try {
            System.getProperty("x");
        } catch (RuntimeException e) {
            // PMD's EmptyCatchBlock exempts variables named `ignored` or `expected` by default,
            // so the fixture deliberately uses `e` to produce a real finding.
        }
    }
}
