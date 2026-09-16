package fixture;

public class SampleTest {
    public void probe() {
        try {
            System.getProperty("x");
        } catch (RuntimeException e) {
            // Violation in a TEST source: only reported when includeTests=true reaches the
            // forked analysis goal. This is the discriminator for configuration loss.
        }
    }
}
