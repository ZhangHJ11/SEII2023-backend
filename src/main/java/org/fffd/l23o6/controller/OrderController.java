package org.fffd.l23o6.controller;

import java.util.List;

import org.fffd.l23o6.pojo.vo.order.CreateOrderRequest;
import org.fffd.l23o6.pojo.vo.order.OrderIdVO;
import org.fffd.l23o6.pojo.vo.order.OrderVO;
import org.fffd.l23o6.pojo.vo.order.PatchOrderRequest;
import org.fffd.l23o6.service.OrderService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.dev33.satoken.stp.StpUtil;
import io.github.lyc8503.spring.starter.incantation.exception.BizException;
import io.github.lyc8503.spring.starter.incantation.exception.CommonErrorType;
import io.github.lyc8503.spring.starter.incantation.pojo.CommonResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RestController
@RequestMapping("/v1/")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping("order")
    public CommonResponse<OrderIdVO> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        StpUtil.checkLogin();
        return CommonResponse
                .success(new OrderIdVO(orderService.createOrder(StpUtil.getLoginIdAsString(), request.getTrainId(),
                        request.getStartStationId(), request.getEndStationId(), request.getSeatType(), null,request.getMoney())));
    }

    @GetMapping("order")
    public CommonResponse<List<OrderVO>> listOrders() {
        StpUtil.checkLogin();
        return CommonResponse.success(orderService.listOrders(StpUtil.getLoginIdAsString()));
    }

    @GetMapping("order/{orderId}")
    public CommonResponse<OrderVO> getOrder(@PathVariable("orderId") Long orderId) {
        return CommonResponse.success(orderService.getOrder(orderId));
    }

    @PostMapping("/payOrder")
    @PatchMapping("order/{orderId}")
    public CommonResponse<?> patchOrder(@PathVariable("orderId") Long orderId,
            @Valid @RequestBody PatchOrderRequest request) {

        switch (request.getStatus()) {
            case PAID:
//                System.out.println(CommonResponse.success(orderService.payOrder(orderId)));
                return CommonResponse.success(orderService.payOrder(orderId));
//                orderService.payOrder(orderId);
//                break;
            case CANCELLED:
                orderService.cancelOrder(orderId);
                break;
            default:
                throw new BizException(CommonErrorType.ILLEGAL_ARGUMENTS, "Invalid order status.");
        }

        return CommonResponse.success();
    }
}