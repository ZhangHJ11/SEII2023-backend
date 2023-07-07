package org.fffd.l23o6;

import io.github.lyc8503.spring.starter.incantation.exception.BizException;
import org.fffd.l23o6.exception.BizError;
import org.fffd.l23o6.pojo.entity.UserEntity;

public class UserServiceStub {
    UserEntity user = UserEntity.builder().id(0L).name("张涵景").idn("222222222222222222")
            .password("Nju123456").createdAt(null).updatedAt(null).updatedAt(null).phone("13822222222")
            .isAdmin(false).points(0).username("user1").build();

    Long count = 1L;

    void login(String username, String password) {
        if (!username.equals(user.getUsername()) && password.equals(user.getPassword())) {
            throw new BizException(BizError.INVALID_CREDENTIAL);
        }
    }

    Long register(String username, String password, String name, String idn, String phone, String type) {
        user = UserEntity.builder().id(count).name(name).idn(idn)
                .password(password).createdAt(null).updatedAt(null).updatedAt(null).phone(phone)
                .isAdmin(false).points(0).username(username).build();;
        return count++;
    }

    UserEntity findByUserName(String username) {
        if (username.equals(user.getUsername())) {
            return user;
        }
        return null;
    }

    UserEntity editInfo(String username, String name, String idn, String phone, Boolean isAdmin) throws BizException {
        if (!username.equals(user.getUsername())) {
            throw new BizException(BizError.INVALID_CREDENTIAL);
        } else {
            user.setUsername(username);
            user.setUpdatedAt(null);
            user.setName(name);
            user.setIdn(idn);
            user.setAdmin(isAdmin);
            user.setPhone(phone);
        }
        return user;
    }
}
