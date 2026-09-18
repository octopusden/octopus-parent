package fixture;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CalcTest {
    @Test
    void adds() {
        assertEquals(3, new Calc().add(1, 2));
    }
}
