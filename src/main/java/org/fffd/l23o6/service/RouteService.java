package org.fffd.l23o6.service;

import java.util.List;

import org.fffd.l23o6.pojo.vo.route.RouteVO;

public interface RouteService {
    void addRoute(String name, List<Long> stationIds);

    List<RouteVO> listRoutes();

    RouteVO getRoute(Long id);

    void editRoute(Long id, String name, List<Long> stationIds);

    void deleteRoute(Long id);
}
