package org.fffd.l23o6.util.strategy.train;

import java.util.HashMap;
import java.util.Map;

public class VKTrain {
    public final Map<Integer, String> SOFT_SLEEPER_SEAT_MAP;
    public final Map<Integer, String> HARD_SLEEPER_SEAT_MAP;
    public final Map<Integer, String> SOFT_SEAT_MAP;
    public final Map<Integer, String> HARD_SEAT_MAP;

    public VKTrain() {
        SOFT_SLEEPER_SEAT_MAP = new HashMap<>();
        HARD_SLEEPER_SEAT_MAP = new HashMap<>();
        SOFT_SEAT_MAP = new HashMap<>();
        HARD_SEAT_MAP = new HashMap<>();
    }
}
