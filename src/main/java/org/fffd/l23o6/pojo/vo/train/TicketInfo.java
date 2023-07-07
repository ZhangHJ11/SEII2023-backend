package org.fffd.l23o6.pojo.vo.train;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TicketInfo implements Serializable {
    private String type;
    private Integer count;
    private Integer price;


    public static List<String> toJsonString(List<TicketInfo> ticketInfoList) throws JsonProcessingException {
        ArrayList<String> jsonStrings=new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();
        for(TicketInfo ticketInfo:ticketInfoList) {
            jsonStrings.add(objectMapper.writeValueAsString(ticketInfo));
        }
        return jsonStrings;
    }

    public static List<TicketInfo> toTicketInfo(List<String> jsonStrings) throws JsonProcessingException {
        ArrayList<TicketInfo> ticketInfoList=new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();
        for(String jsonString:jsonStrings) {
            ticketInfoList.add(objectMapper.readValue(jsonString, TicketInfo.class));
        }
        return ticketInfoList;
    }



    //abandoned below
    public String toString() {
        return "TicketInfo{" + "type='" + type + '\'' + ", count=" + count + ", price=" + price + '}';
    }

    public static TicketInfo parseTicketInfo(String input) {
        String[] parts = input.split("'");

        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid input format. Expected format: 'TicketInfo{type='xxx', count=xxx, price=xxx}'.");
        }

        String type = parts[1];
        String[] countParts = parts[2].split(",");
        Integer count = Integer.valueOf(countParts[1].substring(7));
        Integer price = Integer.valueOf(countParts[2].substring(7, countParts[2].length() - 1));
        return new TicketInfo(type, count, price);
    }

    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        ObjectOutputStream objectStream = new ObjectOutputStream(byteStream);
        objectStream.writeObject(this);
        objectStream.close();
        return byteStream.toByteArray();
    }

    public static TicketInfo fromByteArray(byte[] byteArray) throws IOException, ClassNotFoundException {
        ByteArrayInputStream byteStream = new ByteArrayInputStream(byteArray);
        ObjectInputStream objectStream = new ObjectInputStream(byteStream);
        TicketInfo ticketInfo = (TicketInfo) objectStream.readObject();
        objectStream.close();
        return ticketInfo;
    }
}