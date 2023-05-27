package com.nowcoder.community.dao;

import com.nowcoder.community.entity.Message;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
public interface MessageMapper {

    // 查询当前用户的会话列表，针对每个会话只返回一条最新的私信 // 分页
    List<Message> selectConversations(int userId, int offset, int limit);

    // 查询当前用户的会话的数量
    int selectConversationCount(int userId);

    // 查询某个会话所包含的私信列表
    List<Message> selectLetters(String conversationId, int offset, int limit);

    // 查询某个会话所包含的私信的数量
    int selectLetterCount(String conversationId);

    // 查询未读私信的数量 // 为什么有userId？ 因为会话id是所有的消息，userId用来区分人的。
    int selectLetterUnreadCount(int userId, String conversationId);

    // 新增消息
    int insertMessage(Message message);

    // 修改消息得状态
    int updateStatus(List<Integer> ids, int status);

    // 查询某个主题下最新得通知
    Message selectLatestNotice(@Param("userId") int userId,@Param("topic") String topic);

    // 查询某个主题所包含得通知数量
    int selectNoticeCount(@Param("userId")int userId,@Param("topic") String topic);

    // 查询未读得通知得数量
    // 其中topic查询条件使用了动态SQL，
    // 如果topic不为空，则为具体某个主题得未读数量，
    // 如果topic为空，则为所有主题得未读数量  ，这样功能更强大了
    int selectNoticeUnreadCount(@Param("userId")int userId,@Param("topic") String topic);

    // 查询某个主题所包含得通知列表[支持分页]
    List<Message> selectNotices(int userId, String topic, int offset, int limit);

}
