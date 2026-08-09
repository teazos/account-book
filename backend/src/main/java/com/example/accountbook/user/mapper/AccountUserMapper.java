package com.example.accountbook.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.accountbook.user.entity.AccountUser;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AccountUserMapper extends BaseMapper<AccountUser> {}
