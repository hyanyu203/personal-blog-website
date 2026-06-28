package com.jiangou.user.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PermissionMapper {

    @Select("SELECT DISTINCT p.code FROM permissions p "
            + "INNER JOIN role_permissions rp ON p.id = rp.permission_id "
            + "INNER JOIN user_roles ur ON rp.role_id = ur.role_id "
            + "WHERE ur.user_id = #{userId}")
    List<String> findPermissionCodesByUserId(@Param("userId") Long userId);
}
