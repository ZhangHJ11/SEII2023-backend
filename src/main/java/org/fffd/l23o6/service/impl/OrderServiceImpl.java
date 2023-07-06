package org.fffd.l23o6.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import cn.dev33.satoken.secure.BCrypt;
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
            Long seatNumber,int money) {
        Long userId = userDao.findByUsername(username).getId();
        UserEntity user = userDao.findByUsername(username);
        TrainEntity train = trainDao.findById(trainId).get();
        RouteEntity route = routeDao.findById(train.getRouteId()).get();
        int startStationIndex = route.getStationIds().indexOf(fromStationId);
        int endStationIndex = route.getStationIds().indexOf(toStationId);
        String seat = null;
        switch (train.getTrainType()) {
            case HIGH_SPEED:
                seat = GSeriesSeatStrategy.INSTANCE.allocSeat(startStationIndex, endStationIndex,
                        GSeriesSeatStrategy.GSeriesSeatType.fromString(seatType), train.getSeats());
                break;
            case NORMAL_SPEED:
                seat = KSeriesSeatStrategy.INSTANCE.allocSeat(startStationIndex, endStationIndex,
                        KSeriesSeatStrategy.KSeriesSeatType.fromString(seatType), train.getSeats());
                break;
        }
        if (seat == null) {
            throw new BizException(BizError.OUT_OF_SEAT);
        }
        if(train.getTrainType() == TrainType.HIGH_SPEED){
            //两站间50，一等座为二等座的两倍，商务座为二等座三倍，无座等同于二等座
            money = 50;
            if(seatType.equals("一等座")){
                money *= 2;
            }
            if(seatType.equals("商务座")) {
                money *= 3;
            }
        }
        else{
            money = 10;
            if(seatType.equals("硬卧")){
                money *= 2;
            }
            if(seatType.equals("软卧")){
                money *= 3;
            }
        }
        money *= (endStationIndex - startStationIndex);

        user.setPoints(money);

        OrderEntity order = OrderEntity.builder().trainId(trainId).userId(userId).seat(seat)
                .status(OrderStatus.PENDING_PAYMENT).arrivalStationId(toStationId).departureStationId(fromStationId)
                .money(money).build();
        train.setUpdatedAt(null);// force it to update
        trainDao.save(train);
        orderDao.save(order);
        return order.getId();
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
                .build();
    }

    public void cancelOrder(Long id) {
        OrderEntity order = orderDao.findById(id).get();

        if (order.getStatus() == OrderStatus.COMPLETED || order.getStatus() == OrderStatus.CANCELLED) {
            throw new BizException(BizError.ILLEAGAL_ORDER_STATUS);
        }

        // TODO: refund user's money and credits if needed
        UserEntity user = userDao.findById(order.getUserId()).get();

        order.setStatus(OrderStatus.CANCELLED);
        orderDao.save(order);
    }

    public String payOrder(Long id) {
        OrderEntity order = orderDao.findById(id).get();

        if (order.getStatus() != OrderStatus.PENDING_PAYMENT) {
            throw new BizException(BizError.ILLEAGAL_ORDER_STATUS);
        }

        // TODO: use payment strategy to pay!
        int orderMoney = order.getMoney();
        this.paymentStrategy = new AliPaymentStrategy();
        String url = paymentStrategy.doPostTest(String.valueOf(orderMoney));

        // TODO: update user's credits, so that user can get discount next time
        UserEntity user = userDao.findById(order.getUserId()).get();
        // 根据不同的起始站和到达站给用户增加不同积分

        order.setStatus(OrderStatus.COMPLETED);
        orderDao.save(order);
        return url;
    }

    public double priceCalculator(double price, int userMiles) {
        // 定义里程积分范围和对应的折扣率表格 表驱动
        int[] ranges = { 0, 1000, 3000, 10000, 50000, Integer.MAX_VALUE };
        double[] discounts = { 0, 0.001, 0.0015, 0.002, 0.0025, 0.003 };
        // 判断用户的里程积分属于哪个范围
        int range = 0;
        while (range < ranges.length - 1 && userMiles >= ranges[range + 1]) {
            range++;
        }
        // 根据折扣率计算订单价格
        return price * (1 - discounts[range]);
    }
}
