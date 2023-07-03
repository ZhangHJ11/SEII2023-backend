package org.fffd.l23o6.controller;

import java.util.List;

import org.fffd.l23o6.pojo.vo.route.AddRouteRequest;
import org.fffd.l23o6.pojo.vo.route.RouteVO;
import org.fffd.l23o6.service.RouteService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.lyc8503.spring.starter.incantation.pojo.CommonResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RestController
@RequestMapping("/v1/")
@RequiredArgsConstructor
public class RouteController {
    private final RouteService routeService;

    @PostMapping("admin/route")
    public CommonResponse<?> addRoute(@Valid @RequestBody AddRouteRequest request) {
        routeService.addRoute(request.getName(), request.getStationIds());
        return CommonResponse.success();
    }

    @GetMapping("admin/route")
    public CommonResponse<List<RouteVO>> getRoutes() {
        return CommonResponse.success(routeService.listRoutes());
    }

    @GetMapping("admin/route/{routeId}")
    public CommonResponse<RouteVO> getRoute(@PathVariable("routeId") Long routeId) {
        return CommonResponse.success(routeService.getRoute(routeId));
    }

    @PutMapping("admin/route/{routeId}")
    public CommonResponse<?> editRoute(@PathVariable("routeId") Long routeId,
            @Valid @RequestBody AddRouteRequest request) {
        routeService.editRoute(routeId, request.getName(), request.getStationIds());
        return CommonResponse.success();
    }

    @DeleteMapping("admin/route/{routeId}")
    public CommonResponse<?> deleteRoute(@PathVariable("routeId") Long routeId) {
        routeService.deleteRoute(routeId);
        return CommonResponse.success();
    }
}