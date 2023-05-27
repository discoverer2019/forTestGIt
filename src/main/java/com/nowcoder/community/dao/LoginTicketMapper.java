package com.nowcoder.community.dao;

import com.nowcoder.community.entity.LoginTicket;
import org.apache.ibatis.annotations.*;

// 使用注解的形式编写sql，使用逗号隔开，会自动拼接一起
// 优化重构之后，使用redis进行存储

@Mapper
@Deprecated
public interface LoginTicketMapper {
    // 插入登录凭证
    @Insert({
            "insert into login_ticket(user_id,ticket,status,expired)",
            "values(#{userId},#{ticket},#{status},#{expired})"
    })
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertLoginTicket(LoginTicket loginTicket);

    // 为什么不判断一下status？，是要业务层检查一下过期时间么？过期的再逻辑删除？
    @Select({
            "select id,user_id,ticket,status,expired",
            "from login_ticket where ticket=#{ticket}"
    })
    LoginTicket selectByTicket(String ticket);

    @Update({
            "update login_ticket set status=#{status} where ticket=#{ticket}"
    })
    int updateStatus(String ticket, int status);
}
