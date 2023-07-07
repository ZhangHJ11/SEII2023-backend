package utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.fffd.l23o6.pojo.vo.train.TicketInfo;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class ticketInfoTest {
    @Test
    void test_1() {
        TicketInfo ticketInfo=new TicketInfo("type1",10,100);
//        assert "123".equals(ticketInfo.toString());
        assert "TicketInfo{type='type1', count=10, price=100}".equals(ticketInfo.toString());
//        System.out.println(TicketInfo.parseTicketInfo(ticketInfo.toString()));
        assert "TicketInfo{type='type1', count=10, price=100}".equals(TicketInfo.parseTicketInfo(ticketInfo.toString()).toString());
    }

    @Test
    void test_2() throws IOException, ClassNotFoundException {
        TicketInfo ticketInfo=new TicketInfo("type1",10,100);
        System.out.println(TicketInfo.fromByteArray(ticketInfo.toByteArray()));
    }

    @Test
    void test_3() throws JsonProcessingException {
        TicketInfo ticketInfo1=new TicketInfo("type1",10,100);
        TicketInfo ticketInfo2=new TicketInfo("type1",10,100);
        TicketInfo ticketInfo3=new TicketInfo("type1",10,100);
        ArrayList<TicketInfo> ticketInfoList=new ArrayList<>();
        ticketInfoList.add(ticketInfo1);
        ticketInfoList.add(ticketInfo2);
        ticketInfoList.add(ticketInfo3);
        assert ticketInfoList.equals(TicketInfo.toTicketInfo(TicketInfo.toJsonString(ticketInfoList)));
        System.out.println(TicketInfo.toJsonString(ticketInfoList));
        System.out.println(TicketInfo.toJsonString(TicketInfo.toTicketInfo(TicketInfo.toJsonString(ticketInfoList))));
    }
}
