package com.nowcoder.community.controller;

import com.google.code.kaptcha.Producer;
import com.nowcoder.community.entity.Comment;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.Event;
import com.nowcoder.community.event.EventProducer;
import com.nowcoder.community.service.CommentService;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.HostHolder;
import com.nowcoder.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

@Controller
@RequestMapping("/comment")
public class CommentController implements CommunityConstant {

    @Autowired
    private CommentService commentService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private EventProducer eventProducer;

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private RedisTemplate redisTemplate;

    // 三种评论类型：1.直接对帖子进行评论 2.直接对评论进行评论 3.指向某个人的 对评论的评论
    // 增加评论之后要重定向到当前页面，所以要有帖子id，可以在路径变量中进行传递，然后接收，在重定向的时候使用
    // 页面除了要传递，评论的内容content，还要传递评论的对象类型，对象的id   ，评论的userId要我们自己补充 HostHolder 。状态、时间......
    @RequestMapping(path = "/add/{discussPostId}", method = RequestMethod.POST)

    public String addComment(@PathVariable("discussPostId") int discussPostId, Comment comment) {
        comment.setCreateTime(new Date());
        comment.setStatus(0);
        comment.setUserId(hostHolder.getUser().getId());
        commentService.addComment(comment);

        // 触发评论事件
        // 因为我们要把评论者的评论这件事 通知给  被评论者 也就是实体的拥有者【被评论的实体可能是帖子，也有可能是评论】
        // 所以对应的实体的userId，我们可以先查到实体【根据entityType和EntityId进行查询】然后根据post或者comment获取userId，
        // 不过comment中有一个targetId字段没有用到，应该在评论的时候就进行对应的冗余保存
        Event event = new Event()
                .setTopic(TOPIC_COMMENT)
                .setEntityId(comment.getEntityId())
                .setEntityType(comment.getEntityType())
                .setUserId(hostHolder.getUser().getId())
                // 如果是帖子，就需要帖子的id值，然后连接到帖子的详情页面
                .setData("postId", discussPostId);
        // 还有EntityUserId，如果实体是帖子，那么就要从帖子表中查询，如果实体是评论就要从评论表中查询
        if(comment.getEntityType() == ENTITY_TYPE_POST){
            DiscussPost target = discussPostService.selectDiscussPostById(comment.getEntityId());
            event.setEntityUserId(target.getUserId());
        }else if(comment.getEntityType() == ENTITY_TYPE_COMMENT){
            Comment target = commentService.findCommentById(comment.getEntityId());
            event.setEntityUserId(target.getUserId());
        }
        eventProducer.fireEvent(event);
        //消息队列起到了缓冲的作用，如果短时间并发量很大的话，可以只需要将消息丢到消息队列中
        // 进行异步处理，而不是使用当前线程完成所有的工作。 等到空闲时，再将消息发送到目标用户，有一定的延迟。

        // 当评论的对象是帖子[不是回复]的时候，触发发帖事件
        // 触发发帖事件【发帖事件用不上entityUserId，所以就不获取了】
        if(comment.getEntityType() == ENTITY_TYPE_POST){
           event = new Event()
                   .setTopic(TOPIC_PUBLISH)
                   .setUserId(comment.getUserId())
                   .setEntityType(ENTITY_TYPE_POST)
                   .setEntityId(discussPostId);
            eventProducer.fireEvent(event);

            // 计算帖子分数[这个redisKey是固定的，两次操作会是同一个set集合中的？]
            String redisKey = RedisKeyUtil.getPostScoreKey();
            redisTemplate.opsForSet().add(redisKey, discussPostId);
        }

        return "redirect:/discuss/detail/" + discussPostId;
    }
}
