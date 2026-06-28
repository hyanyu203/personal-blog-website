package com.jiangou.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiangou.user.entity.RoleEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface RoleMapper extends BaseMapper<RoleEntity> {

    @Select("SELECT r.code FROM roles r INNER JOIN user_roles ur ON r.id = ur.role_id "
            + "WHERE ur.user_id = #{userId} AND r.deleted_at IS NULL")
    List<String> findRoleCodesByUserId(@Param("userId") Long userId);

    @Select("SELECT * FROM roles WHERE code = #{code} AND deleted_at IS NULL LIMIT 1")
    RoleEntity findByCode(@Param("code") String code);
}
