package org.fffd.l23o6.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.fffd.l23o6.dao.RouteDao;
import org.fffd.l23o6.dao.TrainDao;
import org.fffd.l23o6.mapper.TrainMapper;
import org.fffd.l23o6.pojo.entity.RouteEntity;
import org.fffd.l23o6.pojo.entity.TrainEntity;
import org.fffd.l23o6.pojo.enum_.TrainType;
import org.fffd.l23o6.pojo.vo.train.AdminTrainVO;
import org.fffd.l23o6.pojo.vo.train.TicketInfo;
import org.fffd.l23o6.pojo.vo.train.TrainDetailVO;
import org.fffd.l23o6.pojo.vo.train.TrainVO;
import org.fffd.l23o6.service.TrainService;
import org.fffd.l23o6.util.strategy.train.GSeriesSeatStrategy;
import org.fffd.l23o6.util.strategy.train.KSeriesSeatStrategy;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import io.github.lyc8503.spring.starter.incantation.exception.BizException;
import io.github.lyc8503.spring.starter.incantation.exception.CommonErrorType;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TrainServiceImpl implements TrainService {
    private final TrainDao trainDao;
    private final RouteDao routeDao;

    @Override
    public TrainDetailVO getTrain(Long trainId) {
        TrainEntity train = trainDao.findById(trainId).get();
        RouteEntity route = routeDao.findById(train.getRouteId()).get();
        return TrainDetailVO.builder().id(trainId).date(train.getDate()).name(train.getName())
                .stationIds(route.getStationIds()).arrivalTimes(train.getArrivalTimes())
                .departureTimes(train.getDepartureTimes()).extraInfos(train.getExtraInfos()).build();
    }

    @Override
    public List<TrainVO> listTrains(Long startStationId, Long endStationId, String date) {
        // TODO
        // 6/30
        // First, get all routes contains [startCity, endCity]
        // Then, Get all trains on that day with the wanted routes
        List<TrainEntity> trainEntitiesList = trainDao.findAll();
        List<TrainVO> trainVOList = new ArrayList<>();
        for (TrainEntity train : trainEntitiesList) {
            // for each train get the route by routeID
            Long routeID = train.getRouteId();
            RouteEntity routeEntity = routeDao.findById(routeID).get();
            // find startStation and endStation in the route
            int start = routeEntity.getStationIds().indexOf(startStationId);
            int end = routeEntity.getStationIds().indexOf(endStationId);
            if (start != -1 && end != -1 && start <= end) {
                // success find station && start before end
                if (Objects.equals(train.getDate(), date)) {
                    // success find date
                    System.out.println(train.getName() + " " + train.getDate() + " " + date);
                    TrainVO trainVO = TrainMapper.INSTANCE.toTrainVO(train);
                    trainVO.setStartStationId(startStationId);
                    trainVO.setEndStationId(endStationId);
                    trainVO.setDepartureTime(train.getDepartureTimes().get(start));
                    trainVO.setArrivalTime(train.getArrivalTimes().get(end));
                    trainVO.setTicketInfos(train.getTicketInfos());
                    trainVOList.add(trainVO);
                }
            }
        }
        return trainVOList;
    }

    @Override
    public List<AdminTrainVO> listTrainsAdmin() {
        return trainDao.findAll(Sort.by(Sort.Direction.ASC, "name")).stream()
                .map(TrainMapper.INSTANCE::toAdminTrainVO).collect(Collectors.toList());
    }

    @Override
    public void addTrain(String name, Long routeId, TrainType type, String date, List<Date> arrivalTimes,
            List<Date> departureTimes, List<String> ticketInfos) {
        TrainEntity entity = TrainEntity.builder().name(name).routeId(routeId).trainType(type)
                .date(date).arrivalTimes(arrivalTimes).departureTimes(departureTimes).ticketInfos(ticketInfos).build();
        RouteEntity route = routeDao.findById(routeId).get();
        if (route.getStationIds().size() != entity.getArrivalTimes().size()
                || route.getStationIds().size() != entity.getDepartureTimes().size()) {
            throw new BizException(CommonErrorType.ILLEGAL_ARGUMENTS, "列表长度错误");
        }
        entity.setExtraInfos(new ArrayList<>(Collections.nCopies(route.getStationIds().size(), "预计正点")));
        switch (entity.getTrainType()) {
            case HIGH_SPEED:
                try {
                    System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                    System.out.println(entity.getName());
                    entity.setSeats(
                            GSeriesSeatStrategy.getInstance(entity, TicketInfo.toTicketInfo(ticketInfos))
                                    .initSeatMap(route.getStationIds().size()));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
            case NORMAL_SPEED:
                try {
                    entity.setSeats(KSeriesSeatStrategy.getInstance(entity, TicketInfo.toTicketInfo(ticketInfos))
                            .initSeatMap(route.getStationIds().size()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
        trainDao.save(entity);
    }

    @Override
    public void changeTrain(Long id, String name, Long routeId, TrainType type, String date, List<Date> arrivalTimes,
            List<Date> departureTimes, List<String> ticketInfos) {
        // TODO: edit train info, please refer to `addTrain` above
        // 6/30
        // select the train by id and delete 这个方法如果更改消息出错，会把原来火车信息删除，可能有点问题
        // trainDao.delete(trainDao.getReferenceById(id));
        // addTrain(name, routeId, type, date, arrivalTimes, departureTimes);

        // 这个方法看上去正确一点
        TrainEntity entity = trainDao.findById(id).get();
        RouteEntity route = routeDao.findById(routeId).get();
        if (route.getStationIds().size() != entity.getArrivalTimes().size()
                || route.getStationIds().size() != entity.getDepartureTimes().size()) {
            throw new BizException(CommonErrorType.ILLEGAL_ARGUMENTS, "列表长度错误");
        }
        entity.setName(name);
        entity.setRouteId(routeId);
        entity.setTrainType(type);
        entity.setDate(date);
        entity.setArrivalTimes(arrivalTimes);
        entity.setDepartureTimes(departureTimes);
        entity.setTicketInfos(ticketInfos);
        // entity.setTest(test);

        entity.setExtraInfos(new ArrayList<>(Collections.nCopies(route.getStationIds().size(), "预计正点")));
        switch (type) {
            case HIGH_SPEED:
                try {
                    entity.setSeats(GSeriesSeatStrategy.getInstance(entity, TicketInfo.toTicketInfo(ticketInfos))
                            .initSeatMap(route.getStationIds().size()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case NORMAL_SPEED:
                try {
                    entity.setSeats(KSeriesSeatStrategy.getInstance(entity, TicketInfo.toTicketInfo(ticketInfos))
                            .initSeatMap(route.getStationIds().size()));
                    break;
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }
        trainDao.save(entity);
    }

    @Override
    public void deleteTrain(Long id) {
        trainDao.deleteById(id);
    }

}
