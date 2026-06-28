package com.jiangou.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.jiangou.**.mapper")
public class MyBatisMapperConfig {
}
