package com.sxwl.common.utils;

import com.sxwl.common.exception.SxwlBusinessException;

import java.lang.management.ManagementFactory;
import java.net.NetworkInterface;
import java.time.Instant;
import java.util.Enumeration;
import java.util.Objects;
import java.util.concurrent.locks.LockSupport;

/**
 * 雪花算法工具类
 * <p>
 * 默认 64 位布局：
 * 1 bit 符号位（始终为 0）
 * 41 bit 时间戳差值（毫秒）
 * 5 bit 数据中心
 * 5 bit 机器节点
 * 12 bit 序列号
 * </p>
 *
 * @author shitianyang
 * @date 2026/6/13
 */
public final class SxwlSnowFlakeUtils {

    /**
     * 自定义起始时间戳（2025-03-24T00:00:00Z）
     */
    public static final long DEFAULT_EPOCH = 1742774400000L;

    /**
     * 数据中心位数
     */
    private static final long DATA_CENTER_ID_BITS = 5L;

    /**
     * 机器节点位数
     */
    private static final long WORKER_ID_BITS = 5L;

    /**
     * 序列号位数
     */
    private static final long SEQUENCE_BITS = 12L;

    /**
     * 数据中心最大值（31）
     */
    private static final long MAX_DATA_CENTER_ID = ~(-1L << DATA_CENTER_ID_BITS);

    /**
     * 机器节点最大值（31）
     */
    private static final long MAX_WORKER_ID = ~(-1L << WORKER_ID_BITS);

    /**
     * 序列号掩码（4095）
     */
    private static final long SEQUENCE_MASK = ~(-1L << SEQUENCE_BITS);

    /**
     * 机器节点左移位数
     */
    private static final long WORKER_ID_SHIFT = SEQUENCE_BITS;

    /**
     * 数据中心左移位数
     */
    private static final long DATA_CENTER_ID_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS;

    /**
     * 时间戳左移位数
     */
    private static final long TIMESTAMP_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS + DATA_CENTER_ID_BITS;

    /**
     * 允许的最大回拨毫秒数
     */
    private static final long MAX_BACKWARD_MS = 5L;

    /**
     * 默认数据中心 ID（可通过 -Dsxwl.snowflake.datacenter-id 覆盖）
     */
    private static final long DEFAULT_DATA_CENTER_ID = resolveDataCenterId();

    /**
     * 默认机器 ID（可通过 -Dsxwl.snowflake.worker-id 覆盖）
     */
    private static final long DEFAULT_WORKER_ID = resolveWorkerId(DEFAULT_DATA_CENTER_ID);

    /**
     * 默认 ID 生成器
     */
    private static final SnowFlakeGenerator DEFAULT_GENERATOR =
            new SnowFlakeGenerator(DEFAULT_EPOCH, DEFAULT_DATA_CENTER_ID, DEFAULT_WORKER_ID);

    /**
     * 工具类不允许实例化
     */
    private SxwlSnowFlakeUtils() {
        throw new UnsupportedOperationException("SxwlSnowFlakeUtils 工具类，不允许实例化");
    }

    /**
     * 生成下一个雪花 ID
     *
     * @return long 类型雪花 ID
     */
    public static long nextId() {
        return DEFAULT_GENERATOR.nextId();
    }

    /**
     * 生成下一个雪花 ID（字符串）
     *
     * @return string 类型雪花 ID
     */
    public static String nextIdStr() {
        return String.valueOf(nextId());
    }

    /**
     * 使用指定数据中心和机器节点生成器
     *
     * @param dataCenterId 数据中心 ID（0~31）
     * @param workerId     机器节点 ID（0~31）
     * @return SnowFlakeGenerator
     */
    public static SnowFlakeGenerator createGenerator(long dataCenterId, long workerId) {
        return new SnowFlakeGenerator(DEFAULT_EPOCH, dataCenterId, workerId);
    }

    /**
     * 反解析雪花 ID
     *
     * @param id 雪花 ID
     * @return 解析结果
     */
    public static SnowFlakeIdMeta parseId(long id) {
        if (id <= 0) {
            throw new SxwlBusinessException(400, "雪花 ID 必须大于 0");
        }

        long sequence = id & SEQUENCE_MASK;
        long workerId = (id >>> WORKER_ID_SHIFT) & MAX_WORKER_ID;
        long dataCenterId = (id >>> DATA_CENTER_ID_SHIFT) & MAX_DATA_CENTER_ID;
        long timestampDiff = id >>> TIMESTAMP_SHIFT;
        long timestamp = timestampDiff + DEFAULT_EPOCH;

        return new SnowFlakeIdMeta(timestamp, dataCenterId, workerId, sequence);
    }

    /**
     * 默认数据中心 ID
     *
     * @return dataCenterId
     */
    public static long getDefaultDataCenterId() {
        return DEFAULT_DATA_CENTER_ID;
    }

    /**
     * 默认机器节点 ID
     *
     * @return workerId
     */
    public static long getDefaultWorkerId() {
        return DEFAULT_WORKER_ID;
    }

    /**
     * 默认雪花起始时间戳
     *
     * @return epoch 毫秒
     */
    public static long getDefaultEpoch() {
        return DEFAULT_EPOCH;
    }

    private static long resolveDataCenterId() {
        Long dataCenterIdByProperty = readLongProperty("sxwl.snowflake.datacenter-id");
        if (dataCenterIdByProperty != null) {
            return normalizeDataCenterId(dataCenterIdByProperty);
        }

        try {
            long macHash = 0L;
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces != null && networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                byte[] hardwareAddress = networkInterface.getHardwareAddress();
                if (hardwareAddress == null || hardwareAddress.length == 0) {
                    continue;
                }
                for (byte b : hardwareAddress) {
                    macHash = (macHash << 5) - macHash + (b & 0xFF);
                }
            }
            return Math.floorMod(macHash, MAX_DATA_CENTER_ID + 1);
        } catch (Exception exception) {
            return 0L;
        }
    }

    private static long resolveWorkerId(long dataCenterId) {
        Long workerIdByProperty = readLongProperty("sxwl.snowflake.worker-id");
        if (workerIdByProperty != null) {
            return normalizeWorkerId(workerIdByProperty);
        }

        try {
            String processName = ManagementFactory.getRuntimeMXBean().getName();
            long processId = Long.parseLong(processName.split("@")[0]);
            long workerSeed = processId + (dataCenterId << 5);
            return Math.floorMod(workerSeed, MAX_WORKER_ID + 1);
        } catch (Exception exception) {
            return 0L;
        }
    }

    private static Long readLongProperty(String key) {
        String rawValue = System.getProperty(key);
        if (rawValue == null || rawValue.trim().isEmpty()) {
            return null;
        }
        try {
            return Long.parseLong(rawValue.trim());
        } catch (NumberFormatException exception) {
            throw new SxwlBusinessException(400, "系统属性 " + key + " 不是有效数字: " + rawValue, exception);
        }
    }

    private static long normalizeDataCenterId(long dataCenterId) {
        if (dataCenterId < 0 || dataCenterId > MAX_DATA_CENTER_ID) {
            throw new SxwlBusinessException(400, "dataCenterId 超出范围(0~" + MAX_DATA_CENTER_ID + "): " + dataCenterId);
        }
        return dataCenterId;
    }

    private static long normalizeWorkerId(long workerId) {
        if (workerId < 0 || workerId > MAX_WORKER_ID) {
            throw new SxwlBusinessException(400, "workerId 超出范围(0~" + MAX_WORKER_ID + "): " + workerId);
        }
        return workerId;
    }

    /**
     * 雪花 ID 生成器
     */
    public static final class SnowFlakeGenerator {

        private final long epoch;
        private final long dataCenterId;
        private final long workerId;

        /**
         * 同毫秒内序列号（0~4095）
         */
        private long sequence = 0L;

        /**
         * 上一次生成 ID 的时间戳
         */
        private long lastTimestamp = -1L;

        public SnowFlakeGenerator(long epoch, long dataCenterId, long workerId) {
            if (epoch < 0) {
                throw new SxwlBusinessException(400, "epoch 不能小于 0");
            }
            this.epoch = epoch;
            this.dataCenterId = normalizeDataCenterId(dataCenterId);
            this.workerId = normalizeWorkerId(workerId);
        }

        /**
         * 生成下一个雪花 ID（线程安全）
         *
         * @return long 类型雪花 ID
         */
        public synchronized long nextId() {
            long currentTimestamp = currentTimeMillis();

            if (currentTimestamp < lastTimestamp) {
                long backwardMillis = lastTimestamp - currentTimestamp;
                if (backwardMillis > MAX_BACKWARD_MS) {
                    throw new SxwlBusinessException(
                            500,
                            "时钟回拨超过可容忍范围，拒绝生成ID。backwardMs=" + backwardMillis
                    );
                }

                currentTimestamp = waitUntil(lastTimestamp);
            }

            if (currentTimestamp == lastTimestamp) {
                sequence = (sequence + 1) & SEQUENCE_MASK;
                if (sequence == 0) {
                    currentTimestamp = waitUntil(lastTimestamp + 1);
                }
            } else {
                sequence = 0L;
            }

            lastTimestamp = currentTimestamp;

            long timestampDiff = currentTimestamp - epoch;
            if (timestampDiff < 0) {
                throw new SxwlBusinessException(500, "当前时间小于雪花起始时间，无法生成ID。timestamp=" + currentTimestamp);
            }

            return (timestampDiff << TIMESTAMP_SHIFT)
                    | (dataCenterId << DATA_CENTER_ID_SHIFT)
                    | (workerId << WORKER_ID_SHIFT)
                    | sequence;
        }

        public String nextIdStr() {
            return String.valueOf(nextId());
        }

        public long getEpoch() {
            return epoch;
        }

        public long getDataCenterId() {
            return dataCenterId;
        }

        public long getWorkerId() {
            return workerId;
        }

        private long waitUntil(long targetTimestamp) {
            long timestamp = currentTimeMillis();
            while (timestamp < targetTimestamp) {
                LockSupport.parkNanos(1_000_000L);
                timestamp = currentTimeMillis();
            }
            return timestamp;
        }

        private long currentTimeMillis() {
            return System.currentTimeMillis();
        }
    }

    /**
     * 雪花 ID 解析元数据
     */
    public static final class SnowFlakeIdMeta {
        private final long timestamp;
        private final long dataCenterId;
        private final long workerId;
        private final long sequence;

        private SnowFlakeIdMeta(long timestamp, long dataCenterId, long workerId, long sequence) {
            this.timestamp = timestamp;
            this.dataCenterId = dataCenterId;
            this.workerId = workerId;
            this.sequence = sequence;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public Instant getInstant() {
            return Instant.ofEpochMilli(timestamp);
        }

        public long getDataCenterId() {
            return dataCenterId;
        }

        public long getWorkerId() {
            return workerId;
        }

        public long getSequence() {
            return sequence;
        }

        @Override
        public String toString() {
            return "SnowFlakeIdMeta{" +
                    "timestamp=" + timestamp +
                    ", dataCenterId=" + dataCenterId +
                    ", workerId=" + workerId +
                    ", sequence=" + sequence +
                    '}';
        }

        @Override
        public int hashCode() {
            return Objects.hash(timestamp, dataCenterId, workerId, sequence);
        }

        @Override
        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            if (!(object instanceof SnowFlakeIdMeta that)) {
                return false;
            }
            return timestamp == that.timestamp
                    && dataCenterId == that.dataCenterId
                    && workerId == that.workerId
                    && sequence == that.sequence;
        }
    }
}
