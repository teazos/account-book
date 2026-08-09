package com.example.accountbook.user.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.accountbook.common.BizException;
import com.example.accountbook.user.dto.LoginRequest;
import com.example.accountbook.user.dto.LoginResponse;
import com.example.accountbook.user.dto.RegisterRequest;
import com.example.accountbook.user.dto.UserView;
import com.example.accountbook.user.entity.AccountUser;
import com.example.accountbook.user.mapper.AccountUserMapper;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.UUID;

@Service
public class UserService {
    private final AccountUserMapper userMapper;

    public UserService(AccountUserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public LoginResponse register(RegisterRequest req) {
        String email = req.getEmail().trim().toLowerCase();
        if (findByEmail(email) != null) throw new BizException("该邮箱已被注册");
        String salt = randomSalt();
        AccountUser user = new AccountUser();
        user.setEmail(email);
        user.setPasswordHash(hash(req.getPassword(), salt));
        user.setPasswordSalt(salt);
        user.setNickname(req.getNickname() == null || req.getNickname().isBlank() ? email : req.getNickname().trim());
        user.setToken(newToken());
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user.setDeleted(false);
        userMapper.insert(user);
        return toResponse(user);
    }

    public LoginResponse login(LoginRequest req) {
        String email = req.getEmail().trim().toLowerCase();
        AccountUser user = findByEmail(email);
        if (user == null) throw new BizException("邮箱或密码错误");
        if (!hash(req.getPassword(), user.getPasswordSalt()).equals(user.getPasswordHash())) throw new BizException("邮箱或密码错误");
        user.setToken(newToken());
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.updateById(user);
        return toResponse(user);
    }

    public AccountUser findByEmail(String email) {
        return userMapper.selectOne(new LambdaQueryWrapper<AccountUser>().eq(AccountUser::getEmail, email));
    }

    public AccountUser findByToken(String token) {
        return userMapper.selectOne(new LambdaQueryWrapper<AccountUser>().eq(AccountUser::getToken, token));
    }

    private LoginResponse toResponse(AccountUser user) {
        return new LoginResponse(user.getToken(), toView(user));
    }

    private UserView toView(AccountUser user) {
        return new UserView(user.getId(), user.getEmail(), user.getNickname());
    }

    private String newToken() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    private String randomSalt() {
        byte[] bytes = new byte[16];
        new SecureRandom().nextBytes(bytes);
        return HexFormat.of().formatHex(bytes);
    }

    private String hash(String password, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt.getBytes(StandardCharsets.UTF_8));
            byte[] digest = md.digest(password.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (Exception e) {
            throw new BizException("密码加密失败");
        }
    }
}
