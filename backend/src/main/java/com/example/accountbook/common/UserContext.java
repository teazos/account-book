package com.example.accountbook.common;

import com.example.accountbook.user.entity.AccountUser;

public class UserContext {
    private static final ThreadLocal<AccountUser> CURRENT = new ThreadLocal<>();

    public static void set(AccountUser user) { CURRENT.set(user); }
    public static AccountUser get() { return CURRENT.get(); }
    public static Long requireUserId() {
        AccountUser user = CURRENT.get();
        if (user == null) throw new BizException("未登录");
        return user.getId();
    }
    public static void clear() { CURRENT.remove(); }
}
