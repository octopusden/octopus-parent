package fixture;

import java.util.ArrayList;

/**
 * Stands in for annotation-processor or groovy-stub output. It carries the same unused import and empty
 * catch as the hand-written sources, so if the analysers looked at generated code both reports would name
 * it. The pom copies this into target/generated-sources, which is where it has to live for the check to
 * mean anything: PMD excludes that directory by root, and Checkstyle filters registered roots inside the
 * build directory.
 */
public class Generated {
    public void run() {
        try {
            System.getProperty("x");
        } catch (RuntimeException e) {
        }
    }
}
