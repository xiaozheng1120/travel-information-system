package com.travel.mapper;

import com.travel.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface UserMapper {
    // 原 selectByUsername 改名为 findUserByUsername（登录、注册查重）
    User findUserByUsername(@Param("username") String username);

    // 原 selectById 改名为 findUserById
    User findUserById(@Param("id") Integer id);

    // 原 insert 改名为 insertUser
    int insertUser(User user);

    // 原 updateById 改名为 updateUser
    int updateUser(User user);

    // --------下面是你原来就有的，保留不动（后台管理用）--------
    int deleteById(Integer id);
    List<User> selectAll();

    // 后台按角色 / 关键词筛选用户
    List<User> selectByCondition(@Param("role") String role, @Param("keyword") String keyword);

    // 启用 / 禁用账号
    int updateStatus(@Param("id") Integer id, @Param("status") Integer status);

    // 登录成功后记录登录时间与次数
    int recordLogin(@Param("id") Integer id);

    /**
     * 查询全部用户总数量
     */
    Long countUserAll();
}
