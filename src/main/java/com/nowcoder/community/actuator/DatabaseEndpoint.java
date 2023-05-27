package com.nowcoder.community.actuator;

import com.nowcoder.community.util.CommunityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Component
@Endpoint(id="database")
public class DatabaseEndpoint {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseEndpoint.class);

    // 怎么查看数据库当前的连接是否正常？
    // 尝试在端点中获取连接，如果不能够获取，那么连接可能就出现了问题
    // 1.使用数据连接池，然后getConnection的方式获取连接
    // 2.直接把连接参数注入进来，然后使用DriverManager去访问。
    @Autowired
    private DataSource dataSource;

    // get请求使用@ReadOperation ， post请求使用WriteOperation
    @ReadOperation
    public String checkConnection() {

        try(
                Connection conn = dataSource.getConnection();
        ){
            return CommunityUtil.getJSONString(0, "获取连接成功");
        }catch (SQLException e){
            logger.error("获取连接失败： " + e.getMessage());
            return CommunityUtil.getJSONString(1, "获取连接失败！");
        }
    }

}
