package org.fffd.l23o6.service.impl;

import org.fffd.l23o6.dao.UserDao;
import org.fffd.l23o6.exception.BizError;
import org.fffd.l23o6.pojo.entity.UserEntity;
import org.fffd.l23o6.service.UserService;
import org.springframework.stereotype.Service;

import cn.dev33.satoken.secure.BCrypt;
import io.github.lyc8503.spring.starter.incantation.exception.BizException;
import io.github.lyc8503.spring.starter.incantation.exception.CommonErrorType;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserDao userDao;

    @Override
    public void register(String username, String password, String name, String idn, String phone, int idType,
            boolean isAdmin) {
        UserEntity user = userDao.findByUsername(username);

        if (user != null) {
            throw new BizException(BizError.USERNAME_EXISTS);
        }

        // 注册时积分设置为0
        userDao.save(UserEntity.builder().username(username).password(BCrypt.hashpw(password))
                .name(name).idn(idn).phone(phone).idType(idType).isAdmin(isAdmin).points(0).build());
    }

    @Override
    public UserEntity findByUserName(String username) {
        return userDao.findByUsername(username);
    }

    @Override
    public void login(String username, String password) {
        UserEntity user = userDao.findByUsername(username);
        if (user == null || !BCrypt.checkpw(password, user.getPassword())) {
            throw new BizException(BizError.INVALID_CREDENTIAL);
        }
    }

    @Override
    public void editInfo(String username, String name, String idn, String phone, int id_type) {
        UserEntity user = userDao.findByUsername(username);

        if (user == null) {
            throw new BizException(CommonErrorType.ILLEGAL_ARGUMENTS, "用户不存在");
        }
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        System.out.println(user.getUsername());
        userDao.save(user.setIdn(idn).setName(name).setPhone(phone).setIdType(id_type));
    }
}