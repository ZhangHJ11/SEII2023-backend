package org.fffd.l23o6.util.strategy.train;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fffd.l23o6.pojo.entity.TrainEntity;
import org.fffd.l23o6.pojo.vo.train.TicketInfo;

import jakarta.annotation.Nullable;

public class GSeriesSeatStrategy extends TrainSeatStrategy {
    public static GSeriesSeatStrategy instance;
    private static VGTrain train = null;
    private static Map<String, VGTrain> trainMap;

    public static String GeneratedValue(int i, int j) {
        return i + "车" + j + (char) (Math.random() % 6 + 'A');
    }

    public static GSeriesSeatStrategy getInstance(TrainEntity t, List<TicketInfo> ticketInfoList) {
        if (instance == null) {
            instance = new GSeriesSeatStrategy();
        }
        System.out.println("*************************");
        System.out.println(t.getName());

        train = trainMap.get(t.getName());
        if (train == null) {
            initGTrainSeat(t, ticketInfoList);
            System.out.println("fuck!!!");
        }
        return instance;
    }

    private static void initGTrainSeat(TrainEntity entity, List<TicketInfo> ticketInfoList) {
        int counter = 1;

        train = new VGTrain();

        for (TicketInfo t : ticketInfoList) {
            System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%5555");
            System.out.println(t.type + "   " + t.count);
            if (t.type.equals("商务座")) {
                for (int j = 1; j <= t.count; j++) {
                    train.BUSINESS_SEAT_MAP.put(counter, GSeriesSeatStrategy.GeneratedValue(counter, j));
                    counter++;
                }
            } else if (t.type.equals("一等座")) {
                for (int j = 1; j <= t.count; j++) {
                    train.FIRST_CLASS_SEAT_MAP.put(counter,
                            GSeriesSeatStrategy.GeneratedValue(counter, j));
                    counter++;
                }
            } else if (t.type.equals("二等座")) {
                for (int j = 1; j <= t.count; j++) {
                    train.SECOND_CLASS_SEAT_MAP.put(counter,
                            GSeriesSeatStrategy.GeneratedValue(counter, j));
                    counter++;
                }
            }
        }

        trainMap.put(entity.getName(), train);
    }

    private GSeriesSeatStrategy() {
        trainMap = new HashMap<>();
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
        System.out.println(seatMap.length + "   " + seatMap[0].length);
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
        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@222");
        System.out
                .println(stationCount - 1 + "    " + train.BUSINESS_SEAT_MAP.size() + train.FIRST_CLASS_SEAT_MAP.size()
                        + train.SECOND_CLASS_SEAT_MAP.size());
        return new boolean[stationCount - 1][train.BUSINESS_SEAT_MAP.size() + train.FIRST_CLASS_SEAT_MAP.size()
                + train.SECOND_CLASS_SEAT_MAP.size()];
    }
}
