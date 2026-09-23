package fixture;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Deliberately touches no production code. Its only job is to make surefire run so the JaCoCo agent
 * writes a jacoco.exec: with no exec file JaCoCo SKIPS its check, the build goes green, and this
 * fixture would then prove the opposite of what it exists to prove. Do not "fix" it by calling Calc -
 * the uncovered Calc is what puts the ratio under the inherited 0.10 floor.
 */
class SmokeTest {
    @Test
    void the_agent_is_armed() {
        assertEquals(2, 1 + 1);
    }
}
