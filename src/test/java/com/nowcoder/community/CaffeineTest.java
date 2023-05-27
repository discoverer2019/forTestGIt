package com.nowcoder.community;

import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.service.DiscussPostService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class CaffeineTest {

    @Autowired
    private DiscussPostService discussPostService;

    @Test
    public void test() {
        Set<Object> set = new HashSet<>();
    }


    // 插入30万条数据，然后进行压力测试
    @Test
    public void initDataForTest() {
        for (int i = 0; i < 300000; i++) {
            DiscussPost post = new DiscussPost();
            post.setUserId(111);
            post.setTitle("互联网求职暖春计划");
            post.setContent("今年的就业形式，确实不容乐观，过了个年，仿佛跳水一般，整个讨论区哀鸿遍野");
            post.setCreateTime(new Date());
            post.setScore(Math.random() * 2000);
            discussPostService.addDiscussPost(post);
        }
    }

    // 测试缓存是否生效：对于第一个因为是热帖，然后从缓存中获取数据，但是缓存种没有数据，所以会从DB种加载数据到缓存种
    // 所以第二和第三个请求都可以从缓存中直接获取
    // 第四条请求，因为不是热帖，不会尝试从缓存中查询数据，所以会直接请求DB。
    // 所以四条请求，实际上就查询了两次DB，缓存生效了两次
    @Test
    public void testCache() {
        System.out.println(discussPostService.findDiscussPosts(0,0,10,1));
        System.out.println(discussPostService.findDiscussPosts(0,0,10,1));
        System.out.println(discussPostService.findDiscussPosts(0,0,10,1));
        System.out.println(discussPostService.findDiscussPosts(0,0,10,0));
    }
}
