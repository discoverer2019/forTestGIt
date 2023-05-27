package com.nowcoder.community.config;

import com.nowcoder.community.quartz.AlphaJob;
import com.nowcoder.community.quartz.PostScoreRefreshJob;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;

// 配置 -- >  数据库 --> 调用
@Configuration
public class QuartzConfig {

//  FactoryBean可简化Bean的实例化过程
//  1.通过FactoryBean封装Bean的实例化过程
//  2.将FactoryBean赚不赔到Spring容器里
//  3.将FactoryBean注入给其它的Bean
//  4.该Bean得到的是FactoryBean所管理的对象实例

    // 配置JobDetail
    //@Bean
    public JobDetailFactoryBean alphaJobDetail() {
        JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
        factoryBean.setJobClass(AlphaJob.class);
        factoryBean.setName("alphaJob");
        factoryBean.setGroup("alphaJobGroup");
        // 任务是否是持久保存，如果为true任务不再运行也会保存
        factoryBean.setDurability(true);
        // 任务是否是可恢复的
        factoryBean.setRequestsRecovery(true);
        return factoryBean;
    }

    // 配置Trigger(SimpleTriggerFactoryBean,CronTriggerFactoryBean)
    // 第一个简单的，可以配置固定时间间隔的：比如说，每隔十分钟执行一次
    // 第二个复杂的，可以配置特定日期的某个时间点：比如每周晚上五点执行一次，每月月初，，，执行一次 【有表达式】
    //@Bean
    // Spring优先把同名的JobDetail注入进来
    public SimpleTriggerFactoryBean alphaTrigger(JobDetail alphaJobDetail) {
        SimpleTriggerFactoryBean factoryBean = new SimpleTriggerFactoryBean();
        factoryBean.setJobDetail(alphaJobDetail);
        factoryBean.setName("alphaTrigger");
        factoryBean.setGroup("alphaTriggerGroup");
        // 任务的执行时间间隔
        factoryBean.setRepeatInterval(3000);
        // Trigger底层需要存储Job的状态,需要一个map
        factoryBean.setJobDataMap(new JobDataMap());
        return factoryBean;
    }

    // 刷新帖子分数任务
    @Bean
    public JobDetailFactoryBean postScoreRefreshJobDetail() {
        JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
        factoryBean.setJobClass(PostScoreRefreshJob.class);
        factoryBean.setName("postScoreRefreshJob");
        factoryBean.setGroup("communityJobGroup");
        // 任务是否是持久保存，如果为true任务不再运行也会保存
        factoryBean.setDurability(true);
        // 任务是否是可恢复的
        factoryBean.setRequestsRecovery(true);
        return factoryBean;
    }

    //
    @Bean
    public SimpleTriggerFactoryBean postScoreRefreshTrigger(JobDetail postScoreRefreshJobDetail) {
        SimpleTriggerFactoryBean factoryBean = new SimpleTriggerFactoryBean();
        factoryBean.setJobDetail(postScoreRefreshJobDetail);
        factoryBean.setName("postScoreRefreshTrigger");
        factoryBean.setGroup("communityTriggerGroup");
        // 任务的执行时间间隔
        factoryBean.setRepeatInterval(1000 * 60 * 5);
        // Trigger底层需要存储Job的状态,需要一个map
        factoryBean.setJobDataMap(new JobDataMap());
        return factoryBean;
    }
}
