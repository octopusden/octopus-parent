package fixture.internal;

import fixture.api.Port;

/** Implementation depending on the api - the allowed direction. */
public class Impl implements Port {
    @Override
    public String describe() {
        return "impl";
    }
}
