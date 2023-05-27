package com.nowcoder.community;

import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.SimpleDateFormat;
import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class UserMapperTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    public void data() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");
        String format = sdf.format(date);
        System.out.println(format);
    }

    @Test
    public void select() {
        System.out.println("...............................................");
        User user1 = userMapper.selectById(1);
        System.out.println(user1);
        System.out.println("...............................................");
        User user2 = userMapper.selectByEmail("nowcoder113@sina.com");
        System.out.println(user2);
        System.out.println("...............................................");
        User user3 = userMapper.selectByUsername("eee");
        System.out.println(user3);
        System.out.println("...............................................");

    }

    @Test
    public void insert() {
        User user = new User();
        user.setUsername("test");
        user.setPassword("123456");
        user.setSalt("abc");
        user.setCreateTime(new Date());
        user.setEmail("test@qq.com");
        user.setHeaderUrl("http://images.nowcoder.com/head/180t.png");
        userMapper.insertUser(user);
        System.out.println(user);

    }

    @Test
    public void update() {
        userMapper.updateHeader(155, "http://images.nowcoder.com/head/189t.png");
        userMapper.updatePassword(155, "wwwwwww");
        userMapper.updateStatus(155, 1);
    }

}
