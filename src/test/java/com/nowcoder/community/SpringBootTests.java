package com.nowcoder.community;

import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.service.DiscussPostService;
import org.elasticsearch.action.ingest.PutPipelineRequest;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class SpringBootTests {

    @Autowired
    private DiscussPostService discussPostService;

    private DiscussPost data;

    @BeforeClass
    public static void beforeClass() {
        System.out.println("beforeClass.....................................");
    }

    @AfterClass
    public static void afterClass() {
        System.out.println("afterClass......................................");
    }

    @Before
    public void before() {
        System.out.println("before..........................................");
        // 初始化测试数据
        data = new DiscussPost();
        data.setUserId(111);
        data.setTitle("Test  Title");
        data.setContent("Test  Content");
        data.setCreateTime(new Date());
        discussPostService.addDiscussPost(data);
    }

    @After
    public void after() {
        System.out.println("after............................................");
        // 删除测试数据
        discussPostService.updateStatus(data.getUserId(), 2);
    }

    @Test
    public void test1() {
        System.out.println("Test1.............................................");
    }

    @Test
    public void test2() {
        System.out.println("test2.............................................");
    }

    @Test
    public void testFindById() {
        DiscussPost post = discussPostService.selectDiscussPostById(data.getId());
        Assert.assertNotNull(post);
        Assert.assertEquals(data.getTitle(), post.getTitle());
        Assert.assertEquals(data.getContent(), post.getContent());
    }

    @Test
    public void testUpdateScore() {
        int rows = discussPostService.updateScore(data.getId(), 2000.00);
        Assert.assertEquals(1, rows);

        DiscussPost discussPost = discussPostService.selectDiscussPostById(data.getId());
        // 对于浮点类型的断言，因为浮点数都是不精确表示的，所以可以通过指定精度进行比较
        Assert.assertEquals(2000.00, discussPost.getScore(),2);
    }
}
