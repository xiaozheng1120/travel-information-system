package com.travel.service;

import com.travel.entity.Result;
import com.travel.entity.User;
import java.util.List;

public interface UserService {
    // ==========你原来就有的后台管理方法，全部保留==========
    User findUserByUsername(String username);
    User findById(Integer id);
    boolean addUser(User user);
    boolean updateUser(User user);
    boolean deleteById(Integer id);
    List<User> findAll();

    // 后台按角色 / 关键词筛选
    List<User> findByCondition(String role, String keyword);

    // 启用 / 禁用账号
    Result updateStatus(Integer id, Integer status);

    // 后台重置指定用户密码
    Result resetPasswordByAdmin(Integer id, String newPassword);

    // =========业务接口：登录注册、个人资料=========
    Result login(String username, String password);
    Result register(User user);
    User getUserById(Integer id);
    // 这里改回返回Result，和Controller匹配
    Result updateUserInfo(User user);

    /**
     * 忘记密码：校验账号+手机号，重置为新密码
     */
    Result resetPassword(String username, String phone, String newPassword);
}
