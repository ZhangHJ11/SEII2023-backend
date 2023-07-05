package org.fffd.l23o6.util.strategy.payment;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradePagePayModel;
import com.alipay.api.request.AlipayTradePagePayRequest;
import jakarta.servlet.ServletException;
import org.mapstruct.Mapping;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173")
public class AliPaymentStrategy extends PaymentStrategy{
    String OrderNo = "202006120020";
    String LastOrderNo = "202006120020";
    String URL = "https://openapi-sandbox.dl.alipaydev.com/gateway.do";
    String APP_ID = "9021000122697449";
    String APP_PRIVATE_KEY = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCeEzqsFvdumM7H30eNi2eS8+srk1qwC1Bi5D6J5uhbGlUOF0qQt5N2IWuKz/xqB3qJuIffy58wHkqMvsAnn+IbfUqJn07U+Ji6sJhYL3VCt0UxJAi9Lc5xHUNe22mbzdeZmxevvMYOxmGL9vZBFO6+bNCk9A4/rWAIIK9+nkLSvgsgZE2YRzdVhXAR2XtBBkSPuaFsDpp1kcZZ0g9kIsyHF/VKYCqHNAFMB8gBAEIdalGjHHzmWpMT2SbLVrcAAAyjVnEfN/K3o2g1SRt3COsmfphrZy9jHmVmKf88ahSsKRoUrNIJDsuarLPD9hTe3JegmAJ5dEOGP9vYs8P1jW+ZAgMBAAECggEAL1/B8Ls9Umb3ac1Py2rhEpiJfVi/EA2D/O88kuWh81Oz8/IlBbQOvJONgF+O3VP6dHXaOsbyoWAipYWxXu66u9RT12ToIkmStDiin6MxeFomoXCUMeYdfabwfugxAWGyYLWrZp6jmN/5chIEgUK9vlIsQZNnBcx3AFiTX9YcXr3d0+3rcL0Tit5hmp7lBmckQ+8mN/QqoA9BeUPCH2EKKQ7j/7k8CZlYABOZFAlanra8ft4AnlpAdeTK1bkAcEo2r8MtTJ93fkqZyMtFv3uwe5l6ZEBVMDxyW/029mMTYP7xweFNbHSKq/bRdACLDzNFoujABt8D11WCp2PIpHvgRQKBgQDrZ2glLPPB4MAWbZn8OO2PFc2mp5MWOTPnide4HhqRgCK/h17kNYPodcyETo5biiquG1eMcTqZE3aAy3zA72nvCd4Bdhni7DtLup9RtdTiUfNa9LqT454EKL5bJnTTdk5xWOyGaqNSGz1D3NhjGBgn7zts8W1CQTXJbqEwQmlIowKBgQCr585nZAiF3doHZbRgWIbjp5MIqQ3UBtnNpycK1zPIdRgZ5nD3PoXin4UqO8Pc2dlRyVmFJPER3WLIRaMqEQVjCB4VfondMys3MZFrqm1fDyUlK/rvj0b/IKxQ69hFL06Pb9CEyfd71lo2X4zBia+Vhy3BASoYm+SgIkabrE3+kwKBgGOeUQ5aZZVIXUGghCeel7mTioHaLH6dGoADb0y1QJ0OUqYmzVtZ4kZ/g1sCO3A1mjYvz+7ZA+k/uUhr1JF5P6Uu0cpghjfnrHgxs7ZKcpqL/6YgJszq6DXuYBAGyC2sxSlmbkG74v2eFWEgllnpGl/b95P03z3IhtCFhqT7lb5tAoGAPlwi1gLFzLoEA7bzThgPJrp9biDVcVri2+WNFjOnB0ri6VJdHHPD4cf4aK+xh4ZHfZHLNa+G+/IRiZEpdXMjPx+MukNjA6kyPh1vIV17u4Yk9W0U5J5XJOnXWKuVktNsbkAjpmgVrQi/RQY4a2TlGxskj+U6Ph8fOq/0aTiCOp0CgYEA3Sj12+24z2T78uetllJGRNjW0wVuolv0uTntp1G6cEsSAVS4Cx5uLjRjv+MZOVLElPQNPb6naS8rOT2FVIlsp7nXAl9oZbWZIpXtZ4d+lr27hBP9Z+0ONs62jruypROyQCTfbUroG3ITqm+yFKdJat5G4KdlKwYrquGARAOV1/4=";
    String ALIPAY_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA0Fd1psv/8KTiTqMcdSB2Z68he06yAG6QdhT89nozZuG68T02490YQNzMxllSHSkHz/KrYnATa10ZU+fIEXoDFAENH4izm2C3wKs4AJ/3th0mkrUrNPNIJUtwQLeNuQC7dj352E6Hxr867FsuCAIhlM7xxrzNUiGY3nl6dmO/O+yH1PHkrNDueP8tGk+IjpJdzTXxQlXeVTNh5FpMgagTUkuL5TmMZRJSojC7A/VgUWl/ty6r4SBPzPPjCCW9TG+2x3lQUAw7Vn2eAwXcpNXTq4j11kagROcFCZiAedqOioz7rX0MdIwagZV6R9eSwXXz8BfPdV76QRBmgDL7JkJJqQIDAQAB";

    @PostMapping("/do-post-test")
    public String doPostTest(String price){
//        返回支付链接
        /** 初始化 **/
        AlipayClient alipayClient = new DefaultAlipayClient(URL,APP_ID,
                APP_PRIVATE_KEY,"json","UTF-8",ALIPAY_PUBLIC_KEY,"RSA2");
        /** 实例化具体API对应的request类，类名称和接口名称对应,当前调用接口名称：alipay.trade.page.pay（电脑网站支付） **/
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        /** 设置业务参数  **/
        AlipayTradePagePayModel model = new AlipayTradePagePayModel();
        /** 商户订单号,商户自定义，需保证在商户端不重复，如：20200612000001 **/
        LastOrderNo = OrderNo;
        model.setOutTradeNo(OrderNo);
        OrderNo = String.valueOf(Long.parseLong(LastOrderNo) + 1);

        model.setProductCode("FAST_INSTANT_TRADE_PAY");
        model.setSubject("购买车票");
        /** 订单金额，精确到小数点后两位 **/
        price = "500";
        model.setTotalAmount(price);
        model.setBody("AliPay");
        alipayRequest.setBizModel(model);
/** 注：支付结果以异步通知为准，不能以同步返回为准，因为如果实际支付成功，但因为外力因素，如断网、断电等导致页面没有跳转，则无法接收到同步通知；**/
/** 同步通知地址，以http或者https开头，支付完成后跳转的地址，用于用户视觉感知支付已成功，传值外网可以访问的地址，如果同步未跳转可参考该文档进行确认：https://opensupport.alipay.com/support/helpcenter/193/201602474937 **/
        alipayRequest.setReturnUrl("http://localhost:5173/user");
/** 异步通知地址，以http或者https开头的，商户外网可以post访问的异步地址，用于接收支付宝返回的支付结果，如果未收到该通知可参考该文档进行确认：https://opensupport.alipay.com/support/helpcenter/193/201602475759 **/
        alipayRequest.setNotifyUrl("http://localhost:5173/user");

//        Z:支付网址 前端跳转页面 支付成功代码逻辑没写
        String urlForm = null;

        try {
            urlForm = alipayClient.pageExecute(alipayRequest, "GET").getBody();
        } catch (AlipayApiException e) {
            throw new RuntimeException(e);
        }
        return urlForm;
    }
}
