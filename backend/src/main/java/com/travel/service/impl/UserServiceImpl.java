package com.travel.service.impl;

import com.travel.entity.Result;
import com.travel.entity.User;
import com.travel.mapper.UserMapper;
import com.travel.service.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Date;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;

    //=====原有方法=====
    @Override
    public User findUserByUsername(String username) {
        return userMapper.findUserByUsername(username);
    }

    @Override
    public User findById(Integer id) {
        return userMapper.findUserById(id);
    }

    @Override
    public boolean addUser(User user) {
        user.setPassword(md5(user.getPassword()));
        return userMapper.insertUser(user) > 0;
    }

    @Override
    public boolean updateUser(User user) {
        if(user.getPassword() != null && user.getPassword().trim().isEmpty()){
            user.setPassword(null);
        }else if(user.getPassword() != null && !user.getPassword().trim().isEmpty()){
            user.setPassword(md5(user.getPassword()));
        }
        return userMapper.updateUser(user) >0;
    }

    @Override
    public boolean deleteById(Integer id) {
        return userMapper.deleteById(id) >0;
    }

    @Override
    public List<User> findAll() {
        List<User> list = userMapper.selectAll();
        clearPassword(list);
        return list;
    }

    @Override
    public List<User> findByCondition(String role, String keyword) {
        List<User> list = userMapper.selectByCondition(role, keyword);
        clearPassword(list);
        return list;
    }

    // 列表接口不下发密码，避免密码哈希暴露到前端
    private void clearPassword(List<User> list) {
        if (list == null) {
            return;
        }
        for (User user : list) {
            user.setPassword(null);
        }
    }

    @Override
    public Result updateStatus(Integer id, Integer status) {
        if (id == null) {
            return Result.error("用户不存在");
        }
        if (status == null || (status != 0 && status != 1)) {
            return Result.error("状态参数不合法");
        }
        User user = userMapper.findUserById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }
        int affect = userMapper.updateStatus(id, status);
        if (affect <= 0) {
            return Result.error("操作失败，请重试");
        }
        return Result.success(status == 1 ? "账号已启用" : "账号已禁用");
    }

    @Override
    public Result resetPasswordByAdmin(Integer id, String newPassword) {
        if (id == null) {
            return Result.error("用户不存在");
        }
        if (newPassword == null || newPassword.trim().isEmpty()) {
            return Result.error("请输入新密码");
        }
        String pwd = newPassword.trim();
        if (pwd.length() < 6 || pwd.length() > 16) {
            return Result.error("新密码长度需6~16位");
        }
        User user = userMapper.findUserById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }
        User update = new User();
        update.setId(id);
        update.setPassword(md5(pwd));
        int affect = userMapper.updateUser(update);
        if (affect <= 0) {
            return Result.error("重置失败，请重试");
        }
        return Result.success("密码已重置，请通知用户使用新密码登录");
    }

    private String md5(String str) {
        if(str == null){
            return null;
        }
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytes = md.digest(str.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for(byte b : bytes){
                String hex = Integer.toHexString(0xff & b);
                if(hex.length() ==1) {
                    sb.append("0");
                }
                sb.append(hex);
            }
            return sb.toString();
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Result register(User user) {
        User oldUser = userMapper.findUserByUsername(user.getUsername());
        if(oldUser != null){
            return Result.error("用户名已存在");
        }
        user.setPassword(md5(user.getPassword()));
        user.setRole("普通用户");
        user.setAvatar("avatar1.jpeg");
        userMapper.insertUser(user);
        return Result.success("注册成功");
    }

    @Override
    public Result login(String username, String password) {
        User user = userMapper.findUserByUsername(username);
        if(user == null){
            return Result.error("用户名不存在,请先注册");
        }
        String encryptPwd = md5(password);
        if(!user.getPassword().equals(encryptPwd)){
            return Result.error("密码错误");
        }
        // 被管理员禁用的账号不允许登录
        if (user.getStatus() != null && user.getStatus() == 0) {
            return Result.error("账号已被禁用，请联系管理员");
        }
        // 记录最近登录时间与累计登录次数
        try {
            userMapper.recordLogin(user.getId());
            user.setLoginCount((user.getLoginCount() == null ? 0 : user.getLoginCount()) + 1);
            user.setLastLoginTime(new Date());
        } catch (Exception e) {
            System.out.println("【登录】记录登录信息失败：" + e.getMessage());
        }
        // 返回前端前清掉密码哈希，避免写进 localStorage
        user.setPassword(null);
        return Result.success(user);
    }

    @Override
    public User getUserById(Integer id) {
        return userMapper.findUserById(id);
    }

    @Override
    public Result updateUserInfo(User user) {
        User exist = userMapper.findUserById(user.getId());
        if(exist == null){
            return Result.error("用户不存在");
        }
        if(user.getPassword() != null && !user.getPassword().trim().isEmpty()){
            user.setPassword(md5(user.getPassword()));
        }else{
            user.setPassword(null);
        }
        int affect = userMapper.updateUser(user);
        if(affect > 0){
            return Result.success("修改成功");
        }else {
            return Result.error("修改失败");
        }
    }

    /**
     * 忘记密码：账号 + 手机号 匹配才允许重置
     */
    @Override
    public Result resetPassword(String username, String phone, String newPassword) {
        if (username == null || username.trim().isEmpty()) {
            return Result.error("请输入账号");
        }
        if (phone == null || phone.trim().isEmpty()) {
            return Result.error("请输入手机号");
        }
        if (newPassword == null || newPassword.trim().isEmpty()) {
            return Result.error("请输入新密码");
        }
        String newPwd = newPassword.trim();
        if (newPwd.length() < 6 || newPwd.length() > 16) {
            return Result.error("新密码长度需6~16位");
        }
        boolean hasLower = false, hasUpper = false, hasDigit = false;
        for (char c : newPwd.toCharArray()) {
            if (c >= 'a' && c <= 'z') {
                hasLower = true;
            } else if (c >= 'A' && c <= 'Z') {
                hasUpper = true;
            } else if (c >= '0' && c <= '9') {
                hasDigit = true;
            }
        }
        if (!hasLower || !hasUpper || !hasDigit) {
            return Result.error("新密码需同时包含大写字母、小写字母和数字");
        }

        User user = userMapper.findUserByUsername(username.trim());
        if (user == null) {
            return Result.error("账号不存在，请检查后重试");
        }
        String dbPhone = user.getPhone() == null ? "" : user.getPhone().trim();
        if (!dbPhone.equals(phone.trim())) {
            return Result.error("手机号与账号不匹配");
        }

        User update = new User();
        update.setId(user.getId());
        update.setPassword(md5(newPwd));
        int affect = userMapper.updateUser(update);
        if (affect > 0) {
            return Result.success("密码重置成功，请使用新密码登录");
        }
        return Result.error("密码重置失败，请重试");
    }
}
