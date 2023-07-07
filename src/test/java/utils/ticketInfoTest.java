package utils;

import org.fffd.l23o6.pojo.vo.train.TicketInfo;
import org.junit.jupiter.api.Test;

import java.io.IOException;

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
        System.out.println(ticketInfo.toByteArray());
        System.out.println(TicketInfo.fromByteArray(ticketInfo.toByteArray()));
    }
}
