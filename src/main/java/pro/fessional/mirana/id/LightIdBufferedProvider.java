package pro.fessional.mirana.id;

import org.jetbrains.annotations.NotNull;
import pro.fessional.mirana.pain.TimeoutRuntimeException;

import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author trydofor
 * @since 2019-05-26
 */
public class LightIdBufferedProvider implements LightIdProvider {

    private static final int MAX_COUNT = 10000;
    private static final int MAX_ERROR = 5;
    private static final long ERR_ALIVE = 120000; // 2分钟
    private static final long TIME_OUT = 1000; // 1秒

    private final ExecutorService executor;
    private final Loader loader;
    private final ConcurrentHashMap<String, SegmentBuffer> cache = new ConcurrentHashMap<>();

    private final AtomicLong loadTimeout = new AtomicLong(TIME_OUT);
    private final AtomicInteger loadMaxError = new AtomicInteger(MAX_ERROR);
    private final AtomicInteger loadMaxCount = new AtomicInteger(MAX_COUNT);
    private final AtomicLong loadErrAlive = new AtomicLong(ERR_ALIVE);

    /**
     * 序号加载器的线程池，默认使用线程模式 core-size=3, max-size=64, keep-alive 60S
     *
     * @param loader 序号加载器。
     */
    public LightIdBufferedProvider(Loader loader) {
        this(loader, new ThreadPoolExecutor(3, 64, 60L, TimeUnit.SECONDS, new SynchronousQueue<>(), new ThreadFactory() {
            private final AtomicInteger counter = new AtomicInteger(1);

            @Override
            public Thread newThread(@NotNull Runnable r) {
                return new Thread(r, "light-id-buffered-provider-" + counter.getAndIncrement());
            }
        }));
    }

    /**
     * 自定义线程模式
     *
     * @param loader   序号加载器
     * @param executor 序号加载器的线程池
     */
    public LightIdBufferedProvider(Loader loader, ExecutorService executor) {
        this.loader = loader;
        this.executor = executor;
    }

    public long getErrAlive() {
        return loadErrAlive.get();
    }


    public long getTimeout() {
        return loadTimeout.get();
    }

    public int getMaxError() {
        return loadMaxError.get();
    }

    public int getMaxCount() {
        return loadMaxCount.get();
    }

    /**
     * 设置错误状态保留时间，过期会清除，默认2分钟。
     * 小于0时表示不会清除
     *
     * @param t 毫秒数
     */
    public void setErrAlive(long t) {
        loadErrAlive.set(t);
    }

    /**
     * 设置请求超时毫秒数，默认1秒。
     *
     * @param t 毫秒数
     * @return 大于零成功，否则失败
     */
    public boolean setTimeout(long t) {
        if (t > 0) {
            loadTimeout.set(t);
            return true;
        } else {
            return false;
        }
    }

    /**
     * 设置加载序号中，容忍的最大错误数，默认5。
     *
     * @param n 数字
     * @return 大于等于零成功，否则失败
     */
    public boolean setMaxError(int n) {
        if (n >= 0) {
            loadMaxError.set(n);
            return true;
        } else {
            return false;
        }
    }

    /**
     * 设置加载序号时的最大加载数量，默认10000。
     *
     * @param n 数字
     * @return 大于等于零成功，否则失败
     */
    public boolean setMaxCount(int n) {
        if (n >= 0) {
            loadMaxCount.set(n);
            return true;
        } else {
            return false;
        }
    }

    /**
     * 预先加载分区中所有LightId，建议启动时初始化一次就够了。
     *
     * @param block 分区
     */
    public void preload(int block) {
        List<Segment> segments = loader.preload(block);
        for (Segment seg : segments) {
            SegmentBuffer buff = load(seg.getBlock(), seg.getName());
            buff.fillSegment(seg);
        }
    }

    /**
     * 清除掉异常信息，计数归零
     *
     * @param name  名字
     * @param block 区块
     */
    public void cleanError(@NotNull String name, int block) {
        load(block, name).handleError(null);
    }

    @Override
    public long next(@NotNull String name, int block) {
        return load(block, name).nextId(loadTimeout.get());
    }

    @Override
    public long next(@NotNull String name, int block, long timeout) {
        if (timeout <= 0) timeout = loadTimeout.get();
        return load(block, name).nextId(timeout);
    }

    // 加载或初始化
    private SegmentBuffer load(int block, String name) {
        return cache.computeIfAbsent(name + "@" + block, k -> new SegmentBuffer(name, block));
    }

    /////////////////////////////////////////////////
    private class SegmentStatus {
        private final long headSeq;
        private final long kneeSeq;
        private final long footSeq;
        private final long startMs;
        private final AtomicLong sequence;

        private SegmentStatus() {
            this.headSeq = -1;
            this.kneeSeq = -1;
            this.footSeq = -1;
            this.startMs = System.currentTimeMillis();
            sequence = new AtomicLong(0);
        }

        private SegmentStatus(Segment seg) {
            headSeq = seg.getHead();
            footSeq = seg.getFoot();
            kneeSeq = footSeq - (footSeq - headSeq) * 2 / 10; // 剩余20%
            startMs = System.currentTimeMillis();
            sequence = new AtomicLong(seg.getHead());
        }

        private int count60s() {
            long ms = (System.currentTimeMillis() - startMs);
            long count = footSeq - headSeq + 1;
            if (ms > 0) {
                count = count * 60000 / ms; //预留60秒
            }
            int max = loadMaxCount.get();
            if (count > max) {
                return max;
            } else {
                return (int) count;
            }
        }
    }

    private class SegmentBuffer {
        private final String name;
        private final int block;

        private final LinkedList<Segment> segmentPool = new LinkedList<>();
        private final AtomicReference<SegmentStatus> segmentSlot = new AtomicReference<>(new SegmentStatus());

        private final AtomicBoolean loaderIdle = new AtomicBoolean(true);
        private final AtomicBoolean switchIdle = new AtomicBoolean(true);

        // 载入时错误信息，不太需要一致性。
        private final AtomicInteger errorCount = new AtomicInteger(0);
        private final AtomicReference<RuntimeException> errorNewer = new AtomicReference<>();
        private final AtomicLong errorEpoch = new AtomicLong(0);

        private SegmentBuffer(String name, int block) {
            this.name = name;
            this.block = block;
        }

        private long nextId(final long timeout) {

            checkError();

            // not need sync
            final SegmentStatus slot = segmentSlot.get();
            final long seq = slot.sequence.getAndIncrement();

            // 未初始化或序号枯竭，等待重装。
            if (seq > slot.footSeq) {
                pollSegment(timeout);
                return nextId(timeout); // 重新获得
            }

            // 预加载
            if (seq > slot.kneeSeq && loaderIdle.get()) {
                loadSegment(slot.count60s());
            }

            return LightIdUtil.toId(block, seq);
        }

        // 异步加载，只有一个活动
        private void loadSegment(final int count) {
            // 加载一次
            synchronized (loaderIdle) {
                if (loaderIdle.get()) {
                    loaderIdle.set(false);
                } else {
                    return;
                }
            }

            executor.submit(() -> {
                try {
                    Segment seg = loader.require(name, block, count);
                    handleError(null); // before fillSegment
                    fillSegment(seg);
                } catch (RuntimeException e) {
                    handleError(e);
                } finally {
                    loaderIdle.set(true); // 不必sync
                }
            });
        }

        // 向pool末端补充
        private void fillSegment(final Segment seg) {
            if (seg == null) {
                return;
            }
            // 保证插入顺序
            synchronized (segmentPool) {
                RuntimeException err = null;
                if (!segmentPool.isEmpty()) {
                    final Segment last = segmentPool.getLast();

                    if (seg.getHead() <= last.getFoot()) {
                        err = new IllegalStateException("seg.start must bigger than last.endin, name=" + name + ",block=" + block);
                    } else if (seg.getBlock() != block) {
                        err = new IllegalStateException("difference block, name=" + name + ", block=" + block + ",seg.block=" + seg.getBlock());
                    } else if (!name.equalsIgnoreCase(seg.getName())) {
                        err = new IllegalStateException("difference name, name=" + name + ", block=" + block + ",seg.name=" + seg.getName());
                    }
                }

                handleError(err);
                if (err == null) {
                    segmentPool.add(seg);
                }
            }
        }

        // 序号用尽，切换
        private void pollSegment(long timeout) {

            final long throwMs = System.currentTimeMillis() + timeout;

            synchronized (switchIdle) {
                if (switchIdle.get()) {
                    switchIdle.set(false);
                } else {
                    try {
                        // 等待超时或成功切换时被唤醒
                        switchIdle.wait(timeout);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw new IllegalStateException("dont interrupt me", e);
                    }
                    long now = System.currentTimeMillis();
                    if (now > throwMs) {
                        throw new TimeoutRuntimeException("waiting segment loadTimeout=" + (now - throwMs + timeout));
                    } else {
                        return;
                    }
                }
            }

            // 只有一个线程可达
            try {
                while (true) {
                    checkError();

                    boolean empty;
                    int count;
                    synchronized (segmentPool) {
                        empty = segmentPool.isEmpty();
                        count = segmentSlot.get().count60s();
                    }

                    if (empty) {
                        if (loaderIdle.get()) {
                            loadSegment(count);
                        }

                        try {
                            Thread.sleep(20);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();  // ignore
                        }
                    } else {
                        synchronized (segmentPool) {
                            Segment seg = segmentPool.poll();
                            if (seg != null) {
                                segmentSlot.set(new SegmentStatus(seg));
                                switchIdle.set(true);
                            }
                        }
                    }

                    long now = System.currentTimeMillis();
                    if (now > throwMs) {
                        throw new TimeoutRuntimeException("switching segment loadTimeout=" + (now - throwMs + timeout));
                    } else if (switchIdle.get()) {
                        return;
                    }
                }
            } finally {
                synchronized (switchIdle) {
                    switchIdle.set(true);
                    switchIdle.notifyAll();
                }
            }
        }

        // 不需要锁
        private void handleError(RuntimeException e) {
            if (e == null) {
                errorCount.set(0);
                errorNewer.set(null);
                errorEpoch.set(0);
            } else {
                errorCount.incrementAndGet();
                errorNewer.set(e);
                errorEpoch.set(System.currentTimeMillis());
            }
        }

        private void checkError() {
            long lf = loadErrAlive.get();
            long ep = errorEpoch.get();
            if (lf > 0 && ep > 0 && (System.currentTimeMillis() - ep) > lf) {
                errorCount.set(0);
                errorNewer.set(null);
                errorEpoch.set(0);
                return;
            }

            RuntimeException err = errorNewer.get();
            if (err == null) {
                return;
            }

            // 不存在
            if (err instanceof NoSuchElementException) {
                throw err;
            }

            // 超过次数
            if (errorCount.get() > loadMaxError.get()) {
                throw err;
            }

        }
    }
}
