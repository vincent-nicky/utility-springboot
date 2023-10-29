package com.wsj.learningredis.job;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 缓存预热任务
 *
 */
@Component
@Slf4j
public class RedissonLockJob {

    //@Resource
    //private UserService userService;

    //@Autowired
    //private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    // 重点用户
    //private List<Long> mainUserList = Arrays.asList(1L);

    // 每天执行，预热推荐用户
    //@Scheduled(cron = "0 31 0 * * *")
    public void doCacheRecommendUser() {
        // 获取锁的信息
        RLock lock = redissonClient.getLock("project:precachejob:lock");
        try {
            // 根据锁的信息获取锁
            boolean isLocked = lock.tryLock(0, 5, TimeUnit.SECONDS);
            /*
                tryLock 是尝试获取锁的方法。
                0 是等待时间，表示如果锁不可用，不要等待，立即返回结果。
                -1 表示锁的持续时间，即锁不会自动释放，必须手动释放。
                TimeUnit.SECONDS 表示等待时间的单位，这里是秒。
             */
            // 如果成功获取锁
            if (isLocked) {
                System.out.println("getLock: " + Thread.currentThread().getId() + " 抢到了");

                //// 执行业务逻辑
                //// 每天为每一位用户推荐20位佳友
                //List<Long> mainUserList = userService.selectAllUserId();
                //for (Long userId : mainUserList) {
                //    QueryWrapper<User> queryWrapper = new QueryWrapper<>();
                //    Page<User> userPage = userService.page(new Page<>(1, 20), queryWrapper);
                //    String redisKey = String.format("project:user:recommend:to:%s", userId);
                //    // 写缓存
                //    redisTemplate.opsForValue().set(redisKey, userPage, 1, TimeUnit.DAYS);
                //}
            }
        } catch (InterruptedException e) {
            log.error("doCacheRecommendUser error", e);
        } finally {
            // 释放自己的锁
            if (lock.isHeldByCurrentThread()) {
                //System.out.println("unLock: " + Thread.currentThread().getId());
                lock.unlock();
            }
        }
    }

}
