package org.fffd.l23o6.util.strategy.train;

import java.util.HashMap;
import java.util.Map;

public class VGTrain {

    public Map<Integer, String> BUSINESS_SEAT_MAP;

    public Map<Integer, String> FIRST_CLASS_SEAT_MAP;

    public Map<Integer, String> SECOND_CLASS_SEAT_MAP;

    public VGTrain() {
        BUSINESS_SEAT_MAP = new HashMap<>();

        FIRST_CLASS_SEAT_MAP = new HashMap<>();

        SECOND_CLASS_SEAT_MAP = new HashMap<>();
    }
}
