package fixture;

import java.util.ArrayList;

/**
 * The same stand-in as Generated.java, but for a generated TEST root. PMD names
 * target/generated-test-sources as a second excludeRoot, and nothing exercised it: with only a generated
 * MAIN source planted, that second root could be deleted and every assertion would still pass.
 */
public class GeneratedHelper {
    public void run() {
        try {
            System.getProperty("x");
        } catch (RuntimeException e) {
        }
    }
}
