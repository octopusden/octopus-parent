package fixture;

public class Calc {
    public int add(int a, int b) {
        return a + b;
    }

    public int unusedPath(int a) {
        if (a > 0) {
            return a;
        }
        return -a;
    }
}
