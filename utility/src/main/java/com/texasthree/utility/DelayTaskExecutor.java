package com.texasthree.utility;

import com.texasthree.utility.utlis.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.concurrent.*;
import java.util.function.BiPredicate;
import java.util.function.Consumer;

public class DelayTaskExecutor<T> {

    private static final Logger logger = LoggerFactory.getLogger(DelayTaskExecutor.class);

    private ThreadPoolExecutor threadPool;

    private Consumer<T> consumer;

    private BiPredicate<T, T> predicate;

    /**
     * 队列容器
     */
    private DelayQueue<Task> queue = new DelayQueue<>();

    public DelayTaskExecutor(Collection<T> wait,
                             Consumer<T> consumer,
                             BiPredicate<T, T> predicate,
                             ThreadPoolExecutor threadPool) {
        this.consumer = consumer;
        this.threadPool = threadPool;
        this.predicate = predicate;
        if (wait != null) {
            for (var v : wait) {
                this.put(v, ThreadLocalRandom.current().nextInt(10));
            }
        }

        if (threadPool != null) {
            threadPool.execute(() -> this.loop());
        }
    }

    private void loop() {
        logger.info("开始轮询任务");
        try {
            while (true) {
                //50毫秒执行一次
                Thread.sleep(50);

//                if (threadPool.getActiveCount() >= threadPool.getPoolSize()) {
//                    logger.info("当前活动线程等于最大线程，不执行. {} {}", threadPool.getActiveCount(), threadPool.getPoolSize());
//                    continue;
//                }
                final var task = this.queue.poll();
                if (task != null) {
                    logger.info("获取到任务");
                    threadPool.execute(() -> {
                        logger.info("准备执行任务, 活跃线程数: {}", threadPool.getActiveCount());
                        this.queue.remove(task);
                        this.consumer.accept(task.record);
                    });
                }
            }
        } catch (Exception e) {
            logger.error("系统异常", e);
            e.printStackTrace();
        }
    }

    /**
     * 将传过来的对象进行通知次数判断，之后决定是否放在任务队列中
     */
    public Delayed put(T record, int duration) {
        if (record == null) {
            throw new IllegalArgumentException();
        }
        if (this.contains(record)) {
            return null;
        }
        logger.info("任务放入队列 {}", record);
        var task = new Task(record, LocalDateTime.now().plusSeconds(duration));
        this.queue.put(task);
        return task;
    }

    public boolean contains(T other) {
        return this.queue.stream().anyMatch(v -> predicate.test(other, v.record));
    }

    /**
     * 通知任务
     */
    private class Task implements Delayed {
        /**
         * 任务执行的时间点
         */
        private LocalDateTime runAt;

        public T record;

        Task(T record, LocalDateTime runAt) {
            this.runAt = runAt;
            this.record = record;
        }

        @Override
        public int compareTo(Delayed delayed) {
            var task = (Task) delayed;
            return runAt.compareTo(task.runAt);
        }

        @Override
        public long getDelay(TimeUnit unit) {
            return unit.convert(getDoTimeMill() - System.currentTimeMillis(), unit.MILLISECONDS);
        }

        private Long doTimeMill;

        private Long getDoTimeMill() {
            if (doTimeMill == null) {
                doTimeMill = DateUtils.localDateTimeToMills(runAt);
            }
            return doTimeMill;
        }
    }
}
