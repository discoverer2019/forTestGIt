package com.nowcoder.community;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisTest {

    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void testStrings() {
        String redisKey = "test:spring";
        ValueOperations ops = redisTemplate.opsForValue();
        ops.set(redisKey, 1);
        System.out.println(ops.get(redisKey));
        System.out.println(ops.increment(redisKey));
        System.out.println(ops.decrement(redisKey));
    }

    @Test
    public void testHashes() {
        String redisKey = "test:count";

        HashOperations ops = redisTemplate.opsForHash();
        ops.put(redisKey, "id", 1);
        ops.put(redisKey, "username", "username");

        System.out.println(ops.get(redisKey, "id"));
        System.out.println(ops.get(redisKey, "username"));
    }

    @Test
    public void testLists() {
        String redisKey = "test:ids";

        ListOperations ops = redisTemplate.opsForList();
        ops.leftPush(redisKey, 1);
        ops.leftPush(redisKey, 2);
        ops.leftPush(redisKey, 3);

        System.out.println(ops.size(redisKey));
        System.out.println(ops.index(redisKey, 0));
        System.out.println(ops.range(redisKey, 0, 2));

        System.out.println(ops.leftPop(redisKey));
        System.out.println(ops.rightPop(redisKey));
    }

    @Test
    public void testSets() {
        String redisKey = "test:teachers";
        SetOperations ops = redisTemplate.opsForSet();

        ops.add(redisKey, "刘备", "关羽", "张飞", "赵云", "诸葛亮");

        System.out.println(ops.size(redisKey));
        System.out.println(ops.pop(redisKey));
        System.out.println(ops.members(redisKey));

    }

    @Test
    public void testSortedSets() {
        String redisKey = "test:students";

        ZSetOperations ops = redisTemplate.opsForZSet();

        ops.add(redisKey, "唐僧", 80);
        ops.add(redisKey, "悟空", 90);
        ops.add(redisKey, "八戒", 50);
        ops.add(redisKey, "沙僧", 70);

        System.out.println(ops.zCard(redisKey));
        System.out.println(ops.score(redisKey, "八戒"));
        System.out.println(ops.reverseRank(redisKey, "八戒")); // 统计八戒排名
        System.out.println(ops.reverseRange(redisKey, 0, 2)); // 取排名范围内的key
    }

    @Test
    public void testKeys() {
        redisTemplate.delete("test:user");

        System.out.println(redisTemplate.hasKey("test:user"));
        redisTemplate.expire("test:students", 10, TimeUnit.SECONDS);
    }

    // 多次访问同一个key
    @Test
    public void testBoundOperations() {
        String redisKey = "test:count";
        BoundValueOperations operations = redisTemplate.boundValueOps(redisKey);


        // 再进行方法调用的时候不需要传入key了
        System.out.println(operations.increment());;
        System.out.println(operations.decrement());;
    }

    @Test
    public void testTransactional() {
        Object obj = redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String redisKey = "test:tx";

                operations.multi();

                operations.opsForSet().add(redisKey, "zhangsan");
                operations.opsForSet().add(redisKey, "lisi");
                operations.opsForSet().add(redisKey, "wangwu");

                // redis管理事务的时候，中间一定不要做查询，因为他事务中的语句都是

                // 在开启事务和提交事务之间，事把命令放到队列中，不会立刻执行
                System.out.println(operations.opsForSet().members(redisKey));
                return operations.exec();

            }
        });
        System.out.println(obj);
    }


    @Test
    public void testHyperLogLog() {
        String redisKey = "test:hll:01";

        for (int i = 0; i <= 100000; i++) {
            redisTemplate.opsForHyperLogLog().add(redisKey,i);
        }
        System.out.println(redisTemplate.opsForHyperLogLog().size(redisKey));
        for (int i = 0; i <= 100000; i++) {
            int r = (int)(Math.random()*100000 + 1);
            redisTemplate.opsForHyperLogLog().add(redisKey,r);
        }
        System.out.println(redisTemplate.opsForHyperLogLog().size(redisKey));
    }

    @Test
    public void testHyperLogLogUnion() {
        String redisKey2 = "test:hll:02";
        for (int i = 1; i <= 10000; i++) {
            redisTemplate.opsForHyperLogLog().add(redisKey2, i);
        }

        String redisKey3 = "test:hll:03";
        for (int i = 5001; i <= 15000; i++) {
            redisTemplate.opsForHyperLogLog().add(redisKey3, i);
        }

        String redisKey4 = "test:hll:04";
        for (int i = 10001; i <= 20000; i++) {
            redisTemplate.opsForHyperLogLog().add(redisKey4, i);
        }

        String unionKey = "test:hll:union";
        redisTemplate.opsForHyperLogLog().union(unionKey, redisKey2);
        long size = redisTemplate.opsForHyperLogLog().size(unionKey);
        System.out.println(size);

    }

    // 统计一组数据得布尔值
    @Test
    public void testBitmap() {
        String redisKey = "test:bm:01";

        // 记录
        redisTemplate.opsForValue().setBit(redisKey, 1, true);
        redisTemplate.opsForValue().setBit(redisKey, 4, true);
        redisTemplate.opsForValue().setBit(redisKey, 7, true);

        // 查询
        System.out.println(redisTemplate.opsForValue().getBit(redisKey, 0));
        System.out.println(redisTemplate.opsForValue().getBit(redisKey, 1));
        System.out.println(redisTemplate.opsForValue().getBit(redisKey, 1));

        // 统计[不在redisTemplate中，需要使用reids底层的连接才能访问到对应的方法]
        // 1的个数
        Object obj = redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                return connection.bitCount(redisKey.getBytes());
            }
        });
        System.out.println(obj);

    }

    // 统计3组数据得布尔值，并对这3组数据做OR运算【也是要在redis底层连接才能够访问到对应得方法】
    @Test
    public void testBitmapOperation() {
        String rediskey2 = "test:bm:02";
        redisTemplate.opsForValue().setBit(rediskey2,0,true);
        redisTemplate.opsForValue().setBit(rediskey2,1,true);
        redisTemplate.opsForValue().setBit(rediskey2,2,true);

        String rediskey3 = "test:bm:03";
        redisTemplate.opsForValue().setBit(rediskey3,2,true);
        redisTemplate.opsForValue().setBit(rediskey3,3,true);
        redisTemplate.opsForValue().setBit(rediskey3,4,true);

        String rediskey4 = "test:bm:04";
        redisTemplate.opsForValue().setBit(rediskey4,4,true);
        redisTemplate.opsForValue().setBit(rediskey4,5,true);
        redisTemplate.opsForValue().setBit(rediskey4,6,true);

        String rediskey = "test:bm:or";
        Object obj = redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                connection.bitOp(RedisStringCommands.BitOperation.OR,
                        rediskey.getBytes(), rediskey2.getBytes(), rediskey3.getBytes(), rediskey4.getBytes());
                return connection.bitCount(rediskey.getBytes());
            }
        });
        System.out.println(obj);
    }



}
