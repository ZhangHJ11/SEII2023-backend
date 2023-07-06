package org.fffd.l23o6.pojo.vo.user;

import lombok.Data;

@Data
public class UserVO {
    private String username;
    private String name;
    private String phone;
    private String idn;
    private int idType;
    private boolean isAdmin;

    //暂定 10 积分抵扣 1 元，消费一元得到 1 积分
    private int points;
}
