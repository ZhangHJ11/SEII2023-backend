package org.fffd.l23o6.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.fffd.l23o6.dao.OrderDao;
import org.fffd.l23o6.dao.RouteDao;
import org.fffd.l23o6.dao.TrainDao;
import org.fffd.l23o6.dao.UserDao;
import org.fffd.l23o6.exception.BizError;
import org.fffd.l23o6.pojo.entity.OrderEntity;
import org.fffd.l23o6.pojo.entity.RouteEntity;
import org.fffd.l23o6.pojo.entity.TrainEntity;
import org.fffd.l23o6.pojo.entity.UserEntity;
import org.fffd.l23o6.pojo.enum_.OrderStatus;
import org.fffd.l23o6.pojo.enum_.TrainType;
import org.fffd.l23o6.pojo.vo.order.OrderVO;
import org.fffd.l23o6.service.OrderService;
import org.fffd.l23o6.util.strategy.payment.AliPaymentStrategy;
import org.fffd.l23o6.util.strategy.payment.PaymentStrategy;
import org.fffd.l23o6.util.strategy.payment.PointsPaymentStrategy;
import org.fffd.l23o6.util.strategy.train.GSeriesSeatStrategy;
import org.fffd.l23o6.util.strategy.train.KSeriesSeatStrategy;
import org.springframework.stereotype.Service;

import io.github.lyc8503.spring.starter.incantation.exception.BizException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderDao orderDao;
    private final UserDao userDao;
    private final TrainDao trainDao;
    private final RouteDao routeDao;
    private PaymentStrategy paymentStrategy;

    public Long createOrder(String username, Long trainId, Long fromStationId, Long toStationId, String seatType,
            Long seatNumber, int money, List<String> jsonStrings) {
        Long userId = userDao.findByUsername(username).getId();
        TrainEntity train = trainDao.findById(trainId).get();
        RouteEntity route = routeDao.findById(train.getRouteId()).get();
        int startStationIndex = route.getStationIds().indexOf(fromStationId);
        int endStationIndex = route.getStationIds().indexOf(toStationId);
        String seat = null;
        System.out.println("正在分配");

        print(train.getSeats());
        switch (train.getTrainType()) {
            case HIGH_SPEED:
                System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
                System.out.println(train.getName());
                seat = GSeriesSeatStrategy.getInstance(train, null).allocSeat(startStationIndex, endStationIndex,
                        GSeriesSeatStrategy.GSeriesSeatType.fromString(seatType), train.getSeats());
                break;
            case NORMAL_SPEED:
                seat = KSeriesSeatStrategy.getInstance(train, null).allocSeat(startStationIndex, endStationIndex,
                        KSeriesSeatStrategy.KSeriesSeatType.fromString(seatType), train.getSeats());
                break;
        }
        if (seat == null) {
            throw new BizException(BizError.OUT_OF_SEAT);
        }

        // 这个后面可以抽象出方法，已有方法要求距离，但是好像我们没那玩意
        money = calculateMoney(50, train.getTrainType(), seatType, endStationIndex - startStationIndex);

        OrderEntity order = OrderEntity.builder().trainId(trainId).userId(userId).seat(seat)
                .status(OrderStatus.PENDING_PAYMENT).arrivalStationId(toStationId).departureStationId(fromStationId)
                .money(money).seatType(seatType).build();
        train.setUpdatedAt(null);// force it to update
        trainDao.save(train);
        orderDao.save(order);
        return order.getId();
    }

    private int calculateMoney(int priceBetweenTwoStations, TrainType trainType, String seatType, int stations) {
        int money = priceBetweenTwoStations;
        if (trainType == TrainType.HIGH_SPEED) {
            if (seatType.equals("一等座")) {
                money *= 2;
            }
            if (seatType.equals("商务座")) {
                money *= 3;
            }
        } else {
            if (seatType.equals("硬卧")) {
                money *= 2;
            }
            if (seatType.equals("软卧")) {
                money *= 3;
            }
        }
        return money * stations;
    }

    public List<OrderVO> listOrders(String username) {
        Long userId = userDao.findByUsername(username).getId();
        List<OrderEntity> orders = orderDao.findByUserId(userId);
        orders.sort((o1, o2) -> o2.getId().compareTo(o1.getId()));
        return orders.stream().map(order -> {
            TrainEntity train = trainDao.findById(order.getTrainId()).get();
            RouteEntity route = routeDao.findById(train.getRouteId()).get();
            int startIndex = route.getStationIds().indexOf(order.getDepartureStationId());
            int endIndex = route.getStationIds().indexOf(order.getArrivalStationId());
            return OrderVO.builder().id(order.getId()).trainId(order.getTrainId())
                    .seat(order.getSeat()).status(order.getStatus().getText())
                    .createdAt(order.getCreatedAt())
                    .startStationId(order.getDepartureStationId())
                    .endStationId(order.getArrivalStationId())
                    .departureTime(train.getDepartureTimes().get(startIndex))
                    .arrivalTime(train.getArrivalTimes().get(endIndex))
                    .money(order.getMoney())
                    .seatType(order.getSeatType())
                    .build();
        }).collect(Collectors.toList());
    }

    public OrderVO getOrder(Long id) {
        OrderEntity order = orderDao.findById(id).get();
        TrainEntity train = trainDao.findById(order.getTrainId()).get();
        RouteEntity route = routeDao.findById(train.getRouteId()).get();
        int startIndex = route.getStationIds().indexOf(order.getDepartureStationId());
        int endIndex = route.getStationIds().indexOf(order.getArrivalStationId());
        return OrderVO.builder().id(order.getId()).trainId(order.getTrainId())
                .seat(order.getSeat()).status(order.getStatus().getText())
                .createdAt(order.getCreatedAt())
                .startStationId(order.getDepartureStationId())
                .endStationId(order.getArrivalStationId())
                .departureTime(train.getDepartureTimes().get(startIndex))
                .arrivalTime(train.getArrivalTimes().get(endIndex))
                .money(order.getMoney())
                .seatType(order.getSeatType())
                .build();
    }

    public void cancelOrder(Long id, int payType, List<String> jsonStrings) {
        OrderEntity order = orderDao.findById(id).get();
        if (order.getStatus() == OrderStatus.COMPLETED || order.getStatus() == OrderStatus.CANCELLED) {
            throw new BizException(BizError.ILLEAGAL_ORDER_STATUS);
        }

        // TODO: refund user's money and credits if needed
        UserEntity user = userDao.findById(order.getUserId()).get();
        if (order.getStatus().equals(OrderStatus.PAID)) {
            if (payType == 1) {
                // alipay
                user.setPoints(user.getPoints() - order.getMoney());
            } else {
                user.setPoints(user.getPoints() + order.getMoney() * 10);
            }
        }

        TrainEntity train = trainDao.findById(order.getTrainId()).get();
        RouteEntity route = routeDao.findById(train.getRouteId()).get();
        int startStationIndex = route.getStationIds().indexOf(order.getDepartureStationId());
        int endStationIndex = route.getStationIds().indexOf(order.getArrivalStationId());

        System.out.println(startStationIndex);
        System.out.println(endStationIndex);
        System.out.println(order.getSeatType());

        print(train.getSeats());
        if (train.getTrainType().equals(TrainType.HIGH_SPEED)) {
            GSeriesSeatStrategy.getInstance(train, null).returnSeat(startStationIndex, endStationIndex,
                    order.getSeatType(), order.getSeat(), train.getSeats());
        } else {
            KSeriesSeatStrategy.getInstance(train, null).returnSeat(startStationIndex, endStationIndex,
                    order.getSeatType(), order.getSeat(), train.getSeats());
        }
        print(train.getSeats());

        order.setStatus(OrderStatus.CANCELLED);
        train.setUpdatedAt(null);
        orderDao.save(order);
        trainDao.save(train);
    }

    public String payOrder(Long id, int payType) {
        OrderEntity order = orderDao.findById(id).get();

        if (order.getStatus() != OrderStatus.PENDING_PAYMENT) {
            throw new BizException(BizError.ILLEAGAL_ORDER_STATUS);
        }

        // TODO: use payment strategy to pay!
        int orderMoney = order.getMoney();
        if (payType == 1) {
            this.paymentStrategy = new AliPaymentStrategy();
        } else {
            this.paymentStrategy = new PointsPaymentStrategy();
        }
        double calculatedMoney = priceCalculator(orderMoney, userDao.findById(order.getUserId()).get().getPoints());
        String url = paymentStrategy.doPostTest(String.valueOf(calculatedMoney));

        // TODO: update user's credits, so that user can get discount next time
        UserEntity user = userDao.findById(order.getUserId()).get();
        // 根据不同的起始站和到达站给用户增加不同积分,1 元计 1 积分
        if (paymentStrategy instanceof AliPaymentStrategy) {
            user.setPoints(user.getPoints() + orderMoney);
        } else {
            user.setPoints(user.getPoints() - orderMoney * 10);
        }
        // order.setStatus(OrderStatus.COMPLETED);
        order.setStatus(OrderStatus.PAID);
        orderDao.save(order);
        return url;
    }

    public double priceCalculator(double price, int credits) {
        // 定义里程积分范围和对应的折扣率表格 表驱动
        int[] ranges = { 0, 1000, 3000, 10000, 50000, Integer.MAX_VALUE };
        double[] discounts = { 0, 0.001, 0.0015, 0.002, 0.0025, 0.003 };
        // 判断用户的里程积分属于哪个范围
        int range = 0;
        while (range < ranges.length - 1 && credits >= ranges[range + 1]) {
            range++;
        }
        // 根据折扣率计算订单价格
        return price * (1 - discounts[range]);
    }

    private void print(boolean[][] list) {
        for (int i = 0; i < list.length; i++) {
            for (int t = 0; t < list[0].length; t++) {
                if (list[i][t])
                    System.out.print("1 ");
                else
                    System.out.print("0 ");
            }
            System.out.println();
        }
    }
}
