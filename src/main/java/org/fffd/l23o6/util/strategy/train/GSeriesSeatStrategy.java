package org.fffd.l23o6.util.strategy.train;

import java.util.Map;

import org.fffd.l23o6.pojo.entity.TrainEntity;

import jakarta.annotation.Nullable;

public class GSeriesSeatStrategy extends TrainSeatStrategy {
    public static GSeriesSeatStrategy instance;
    private static TrainEntity train = null;

    public static String GeneratedValue(int i, int j) {
        return i + "车" + j + (char) (Math.random() % 6 + 'A');
    }

    public static GSeriesSeatStrategy getInstance(TrainEntity t) {
        if (instance == null) {
            instance = new GSeriesSeatStrategy();
        }
        train = t;
        return instance;
    }

    private GSeriesSeatStrategy() {
        // for (String s : Arrays.asList("1车1A", "1车1C", "1车1F")) {
        // train.BUSINESS_SEAT_MAP.put(counter++, s);
        // }

        // for (String s : Arrays.asList("2车1A", "2车1C", "2车1D", "2车1F", "2车2A", "2车2C",
        // "2车2D", "2车2F", "3车1A", "3车1C",
        // "3车1D", "3车1F")) {
        // train.FIRST_CLASS_SEAT_MAP.put(counter++, s);
        // }

        // for (String s : Arrays.asList("4车1A", "4车1B", "4车1C", "4车1D", "4车2F", "4车2A",
        // "4车2B", "4车2C", "4车2D", "4车2F",
        // "4车3A", "4车3B", "4车3C", "4车3D", "4车3F")) {
        // train.SECOND_CLASS_SEAT_MAP.put(counter++, s);
        // }
    }

    public enum GSeriesSeatType implements SeatType {
        BUSINESS_SEAT("商务座"), FIRST_CLASS_SEAT("一等座"),
        SECOND_CLASS_SEAT("二等座"), NO_SEAT("无座");

        private String text;

        GSeriesSeatType(String text) {
            this.text = text;
        }

        public String getText() {
            return this.text;
        }

        public static GSeriesSeatType fromString(String text) {
            for (GSeriesSeatType b : GSeriesSeatType.values()) {
                if (b.text.equalsIgnoreCase(text)) {
                    return b;
                }
            }
            return null;
        }
    }

    public @Nullable void returnSeat(int startStationIndex, int endStationIndex, String seatType, String seat,
            boolean[][] seatMap) {
        if (seatType.equals("商务座")) {
            for (int i = 0; i < train.BUSINESS_SEAT_MAP.size(); i++) {
                if (seat.equals(train.BUSINESS_SEAT_MAP.get(i))) {
                    for (int j = startStationIndex; j < endStationIndex; j++) {
                        seatMap[j][i] = false;
                    }
                    break;
                }
            }
        } else if (seatType.equals("一等座")) {
            for (int i = train.BUSINESS_SEAT_MAP.size(); i < train.BUSINESS_SEAT_MAP.size()
                    + train.FIRST_CLASS_SEAT_MAP.size(); i++) {
                if (seat.equals(train.FIRST_CLASS_SEAT_MAP.get(i))) {
                    for (int j = startStationIndex; j < endStationIndex; j++) {
                        seatMap[j][i] = false;
                    }
                    break;
                }
            }
        } else if (seatType.equals("二等座")) {
            for (int i = train.BUSINESS_SEAT_MAP.size()
                    + train.FIRST_CLASS_SEAT_MAP.size(); i < seatMap[0].length; i++) {
                if (seat.equals(train.SECOND_CLASS_SEAT_MAP.get(i))) {
                    for (int j = startStationIndex; j < endStationIndex; j++) {
                        seatMap[j][i] = false;
                    }
                    break;
                }
            }
        }
    }

    public @Nullable String allocSeat(int startStationIndex, int endStationIndex, GSeriesSeatType type,
            boolean[][] seatMap) {
        // endStationIndex - 1 = upper bound
        // TODO
        System.out.println("1111111111111111111111111111111111111111111111");
        for (int i = 0; i < seatMap.length; i++) {
            for (int j = 0; j < seatMap[0].length; j++) {
                System.out.print(seatMap[i][j] + " ");
            }
            System.out.println();
        }
        boolean flag;
        switch (type) {
            case BUSINESS_SEAT -> {
                for (int i = 0; i < train.BUSINESS_SEAT_MAP.size(); i++) {
                    flag = true;
                    for (int j = startStationIndex; j < endStationIndex; j++) {
                        if (seatMap[j][i]) {
                            flag = false;
                            break;
                        }
                    }
                    if (flag) {
                        for (int j = startStationIndex; j < endStationIndex; j++) {
                            seatMap[j][i] = true;
                        }
                        return train.BUSINESS_SEAT_MAP.get(i);
                    }
                }
                return null;
            }
            case FIRST_CLASS_SEAT -> {
                for (int i = train.BUSINESS_SEAT_MAP.size(); i < train.BUSINESS_SEAT_MAP.size()
                        + train.FIRST_CLASS_SEAT_MAP.size(); i++) {
                    flag = true;
                    for (int j = startStationIndex; j < endStationIndex; j++) {
                        if (seatMap[j][i]) {
                            flag = false;
                            break;
                        }
                    }
                    if (flag) {
                        for (int j = startStationIndex; j < endStationIndex; j++) {
                            seatMap[j][i] = true;
                        }
                        return train.FIRST_CLASS_SEAT_MAP.get(i);
                    }
                }
                return null;
            }
            case SECOND_CLASS_SEAT -> {
                for (int i = train.BUSINESS_SEAT_MAP.size()
                        + train.FIRST_CLASS_SEAT_MAP.size(); i < seatMap[0].length; i++) {
                    flag = true;
                    for (int j = startStationIndex; j < endStationIndex; j++) {
                        if (seatMap[j][i]) {
                            flag = false;
                            break;
                        }
                    }
                    if (flag) {
                        for (int j = startStationIndex; j < endStationIndex; j++) {
                            seatMap[j][i] = true;
                        }
                        return train.SECOND_CLASS_SEAT_MAP.get(i);
                    }
                }
                return null;
            }
            default -> {
                return null;
            }
        }
    }

    public Map<GSeriesSeatType, Integer> getLeftSeatCount(int startStationIndex, int endStationIndex,
            boolean[][] seatMap) {
        // TODO
        return null;
    }

    public boolean[][] initSeatMap(int stationCount) {
        return new boolean[stationCount - 1][train.BUSINESS_SEAT_MAP.size() + train.FIRST_CLASS_SEAT_MAP.size()
                + train.SECOND_CLASS_SEAT_MAP.size()];
    }
}
