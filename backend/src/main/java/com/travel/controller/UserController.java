package com.travel.controller;

import com.travel.entity.Result;
import com.travel.entity.User;
import com.travel.service.UserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    // ==========后台管理接口==========
    // 根据用户名查询
    @GetMapping("/find")
    public Result findUser(String username){
        User user = userService.findUserByUsername(username);
        if (user != null) {
            user.setPassword(null);
        }
        return Result.success(user);
    }

    // 查询全部用户（可按角色 / 关键词筛选）
    @GetMapping("/list")
    public Result list(@RequestParam(required = false) String role,
                       @RequestParam(required = false) String keyword){
        List<User> list = userService.findByCondition(role, keyword);
        return Result.success(list);
    }

    // 启用 / 禁用账号
    @PutMapping("/status")
    public Result updateStatus(@RequestParam Integer id, @RequestParam Integer status){
        if (status == null || (status != 0 && status != 1)) {
            return Result.error("状态值不合法");
        }
        User target = userService.findById(id);
        if (target == null) {
            return Result.error("用户不存在");
        }
        if (status == 0 && "管理员".equals(target.getRole())) {
            return Result.error("管理员账号不能禁用");
        }
        return userService.updateStatus(id, status);
    }

    // 后台重置指定用户密码
    @PostMapping("/adminResetPwd")
    public Result adminResetPwd(@RequestBody Map<String, Object> params){
        Integer id = params.get("id") == null ? null : Integer.valueOf(String.valueOf(params.get("id")));
        Object pwdObj = params.get("newPassword");
        return userService.resetPasswordByAdmin(id, pwdObj == null ? null : String.valueOf(pwdObj));
    }

    //新增用户
    @PostMapping("/add")
    public Result add(@RequestBody User user){
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            return Result.error("用户名不能为空");
        }
        if (userService.findUserByUsername(user.getUsername()) != null) {
            return Result.error("用户名已存在");
        }
        boolean res = userService.addUser(user);
        return res ? Result.success("新增用户成功") : Result.error("新增用户失败");
    }

    //根据id查询用户
    @GetMapping("/{id}")
    public Result getById(@PathVariable Integer id){
        User user = userService.findById(id);
        if (user != null) {
            user.setPassword(null);
        }
        return Result.success(user);
    }

    //修改用户
    @PutMapping("/update")
    public Result update(@RequestBody User user){
        User target = userService.findById(user.getId());
        if (target != null && "管理员".equals(target.getRole())
                && user.getRole() != null && !"管理员".equals(user.getRole())) {
            return Result.error("管理员账号不能改为普通用户");
        }
        boolean res = userService.updateUser(user);
        return res ? Result.success("修改用户成功") : Result.error("修改用户失败");
    }

    //删除用户
    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Integer id){
        User target = userService.findById(id);
        if (target != null && "管理员".equals(target.getRole())) {
            return Result.error("管理员账号不能删除");
        }
        boolean res = userService.deleteById(id);
        return res ? Result.success("删除用户成功") : Result.error("删除用户失败");
    }

    // ============注册、登录、个人资料接口============
    // 用户注册
    @PostMapping("/register")
    public Result register(@RequestBody User user){
        return userService.register(user);
    }

    // 登录接口
    @PostMapping("/login")
    public Result login(@RequestBody User user){
        return userService.login(user.getUsername(),user.getPassword());
    }

    // 根据id获取用户信息（给前端个人中心使用）
    @GetMapping("/getById")
    public Result getUserById(@RequestParam Integer id){
        User user = userService.getUserById(id);
        if (user != null) {
            user.setPassword(null);
        }
        return Result.success(user);
    }

    // 修改个人资料
    @PutMapping("/updateInfo")
    public Result updateInfo(@RequestBody User user){
        return userService.updateUserInfo(user);
    }

    // 忘记密码：校验账号+手机号后重置密码
    @PostMapping("/resetPwd")
    public Result resetPwd(@RequestBody Map<String, String> params){
        return userService.resetPassword(params.get("username"), params.get("phone"), params.get("newPassword"));
    }
}
