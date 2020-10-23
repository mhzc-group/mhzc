package com.beauty.mhzc.db.util;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.sql.Timestamp;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;


/**
 * 高效生成唯一ID的方法(线程安全)
 */
public class IdHelper {
	
	private static final Sequence sequence = new Sequence();

    public static void main(String[] args){
        System.out.println(IdHelper.generateGUID());
    }

	private IdHelper() {}
	
	/**
	 * 生成一个分布式高效有序GUID的long值<br/>
     * 建议用来代替UUID
	 * @return long值
	 */
	public static long generateGUID() {
        return sequence.nextId();
    }

    /**
     * 生成一个UUID字符串(去掉了"-")
	 * @return 字符串(长度为32)
     */
    public static synchronized String generate32UUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }
    
    /**
     * 获取当前时间的毫秒值<br/>
     *  高并发场景下System.currentTimeMilles()的性能优化
     * @return 当前时间的毫秒值
     */
    public static long currentTimeMillis() {
    		return sequence.timeGen();
    }
    
    /**
     * 获取宿主机器的MAC地址
     * @return 正规MAC地址字符串值
     * @throws Exception 未成功获取到MAC地址时抛出此异常
     */
    public static String getMACAddress() throws Exception {
		InetAddress ip = InetAddress.getLocalHost();
		NetworkInterface network = NetworkInterface.getByInetAddress(ip);
		byte[] mac = network.getHardwareAddress();
		
		if(null == mac) {
			throw new Exception("未能获取到MAC地址");
		}
		// 把mac地址拼装成string
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < mac.length; i++) {
			if (i > 0) {
				sb.append("-");
			}
			//把byte转化为16进制的整数
			String s = Integer.toHexString(mac[i] & 0xFF);
			if(1 == s.length()) {
				sb.append(0);
			}
			sb.append(s);
		}
		// 把字符串所有小写字母改为大写。正规的mac地址并返回
		return sb.toString().toUpperCase();
	}

    /**
     * 基于Twitter的Snowflake算法实现分布式高效有序ID生产黑科技(sequence) <br>
     * 每秒最多可生产418万个有序的ID，即QPS=400w/s <br/>
     * 来源：https://gitee.com/yu120/sequence/tree/master
     *
     * <br>
     * SnowFlake的结构如下(每部分用-分开):<br>
     * <br>
     * 0 - 0000000000 0000000000 0000000000 0000000000 0 - 00000 - 00000 - 000000000000 <br>
     * <br>
     * 1位标识，由于long基本类型在Java中是带符号的，最高位是符号位，正数是0，负数是1，所以id一般是正数，最高位是0<br>
     * <br>
     * 41位时间截(毫秒级)，注意，41位时间截不是存储当前时间的时间截，而是存储时间截的差值（当前时间截 - 开始时间截)
     * 得到的值），这里的的开始时间截，一般是我们的id生成器开始使用的时间，由我们程序来指定的（如下下面程序IdWorker类的startTime属性）。41位的时间截，可以使用69年，年T = (1L << 41) / (1000L * 60 * 60 * 24 * 365) = 69<br>
     * <br>
     * 10位的数据机器位，可以部署在1024个节点，包括5位datacenterId和5位workerId<br>
     * <br>
     * 12位序列，毫秒内的计数，12位的计数顺序号支持每个节点每毫秒(同一机器，同一时间截)产生4096个ID序号<br>
     * <br>
     * <br>
     * 加起来刚好64位，为一个Long型。<br>
     * SnowFlake的优点是，整体上按照时间自增排序，并且整个分布式系统内不会产生ID碰撞(由数据中心ID和机器ID作区分)，并且效率较高，经测试，SnowFlake每秒能够产生26万ID左右。
     *
     * @author lry
     */
    public static class Sequence {

        /** 起始时间戳，用于用当前时间戳减去这个时间戳，算出偏移量 **/
        private final long startTime = 1522727085950L;

        /** workerId占用的位数5（表示只允许workId的范围为：0-1023）**/
        private final long workerIdBits = 5L;
        /** dataCenterId占用的位数：5 **/
        private final long dataCenterIdBits = 5L;
        /** 序列号占用的位数：12（表示只允许workId的范围为：0-4095）**/
        private final long sequenceBits = 12L;

        /** workerId可以使用的最大数值：31 **/
        private final long maxWorkerId = -1L ^ (-1L << workerIdBits);
        /** dataCenterId可以使用的最大数值：31 **/
        private final long maxDataCenterId = -1L ^ (-1L << dataCenterIdBits);

        private final long workerIdShift = sequenceBits;
        private final long dataCenterIdShift = sequenceBits + workerIdBits;
        private final long timestampLeftShift = sequenceBits + workerIdBits + dataCenterIdBits;

        /** 用mask防止溢出:位与运算保证计算的结果范围始终是 0-4095 **/
        private final long sequenceMask = -1L ^ (-1L << sequenceBits);

        private long workerId;
        private long dataCenterId;
        private long sequence = 0L;
        private long lastTimestamp = -1L;
        private boolean isClock = false;

        public Sequence() {
            this.dataCenterId = getDatacenterId(maxDataCenterId);
            this.workerId = getMaxWorkerId(dataCenterId, maxWorkerId);
        }

        /**
         * <p>
         * 数据标识id部分
         * </p>
         */
        static long getDatacenterId(long maxDatacenterId) {
            long id = 0L;
            try {
                InetAddress ip = InetAddress.getLocalHost();
                NetworkInterface network = NetworkInterface.getByInetAddress(ip);
                if (network == null) {
                    id = 1L;
                } else {
                    byte[] mac = network.getHardwareAddress();
                    if (null != mac) {
                        id = ((0x000000FF & (long) mac[mac.length - 1])
                                | (0x0000FF00 & (((long) mac[mac.length - 2]) << 8))) >> 6;
                        id = id % (maxDatacenterId + 1);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return id;
        }

        /**
         * <p>
         * 获取 maxWorkerId
         * </p>
         */
        protected static long getMaxWorkerId(long datacenterId, long maxWorkerId) {
            StringBuilder mpid = new StringBuilder();
            mpid.append(datacenterId);
            String name = ManagementFactory.getRuntimeMXBean().getName();
            if (Objects.nonNull(name) && !"".equals(name)) {
                /*
                 * GET jvmPid
                 */
                mpid.append(name.split("@")[0]);
            }
            /*
             * MAC + PID 的 hashcode 获取16个低位
             */
            return (mpid.toString().hashCode() & 0xffff) % (maxWorkerId + 1);
        }

        /**
         * 基于Snowflake创建分布式ID生成器
         * <p>
         * 注：sequence
         *
         * @param workerId     工作机器ID,数据范围为0~31
         * @param dataCenterId 数据中心ID,数据范围为0~31
         */
        public Sequence(long workerId, long dataCenterId) {
            if (workerId > maxWorkerId || workerId < 0) {
                throw new IllegalArgumentException(String.format("worker Id can't be greater than %d or less than 0", maxWorkerId));
            }
            if (dataCenterId > maxDataCenterId || dataCenterId < 0) {
                throw new IllegalArgumentException(String.format("dataCenter Id can't be greater than %d or less than 0", maxDataCenterId));
            }

            this.workerId = workerId;
            this.dataCenterId = dataCenterId;
        }

        public void setClock(boolean clock) {
            isClock = clock;
        }

        /**
         * 获取ID
         *
         * @return
         */
        public synchronized Long nextId() {
            long timestamp = this.timeGen();

            // 闰秒：如果当前时间小于上一次ID生成的时间戳，说明系统时钟回退过这个时候应当抛出异常
            if (timestamp < lastTimestamp) {
                long offset = lastTimestamp - timestamp;
                if (offset <= 5) {
                    try {
                        this.wait(offset << 1);
                        timestamp = this.timeGen();
                        if (timestamp < lastTimestamp) {
                            throw new RuntimeException(String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", offset));
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    throw new RuntimeException(String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", offset));
                }
            }

            // 解决跨毫秒生成ID序列号始终为偶数的缺陷:如果是同一时间生成的，则进行毫秒内序列
            if (lastTimestamp == timestamp) {
                // 通过位与运算保证计算的结果范围始终是 0-4095
                sequence = (sequence + 1) & sequenceMask;
                if (sequence == 0) {
                    timestamp = this.tilNextMillis(lastTimestamp);
                }
            } else {
                // 时间戳改变，毫秒内序列重置
                sequence = 0L;
            }

            lastTimestamp = timestamp;

            /*
             * 1.左移运算是为了将数值移动到对应的段(41、5、5，12那段因为本来就在最右，因此不用左移)
             * 2.然后对每个左移后的值(la、lb、lc、sequence)做位或运算，是为了把各个短的数据合并起来，合并成一个二进制数
             * 3.最后转换成10进制，就是最终生成的id
             */
            return ((timestamp - startTime) << timestampLeftShift) |
                    (dataCenterId << dataCenterIdShift) |
                    (workerId << workerIdShift) |
                    sequence;
        }

        /**
         * 保证返回的毫秒数在参数之后(阻塞到下一个毫秒，直到获得新的时间戳)
         *
         * @param lastTimestamp
         * @return
         */
        private long tilNextMillis(long lastTimestamp) {
            long timestamp = this.timeGen();
            while (timestamp <= lastTimestamp) {
                timestamp = this.timeGen();
            }

            return timestamp;
        }

        /**
         * 获得系统当前毫秒数
         *
         * @return timestamp
         */
        private long timeGen() {
            if (isClock) {
                // 解决高并发下获取时间戳的性能问题
                return SystemClock.now();
            } else {
                return System.currentTimeMillis();
            }
        }

    }

    /**
     * 高并发场景下System.currentTimeMillis()的性能问题的优化
     * <p><p>
     * System.currentTimeMillis()的调用比new一个普通对象要耗时的多（具体耗时高出多少我还没测试过，有人说是100倍左右）<p>
     * System.currentTimeMillis()之所以慢是因为去跟系统打了一次交道<p>
     * 后台定时更新时钟，JVM退出时，线程自动回收<p>
     * 10亿：43410,206,210.72815533980582%<p>
     * 1亿：4699,29,162.0344827586207%<p>
     * 1000万：480,12,40.0%<p>
     * 100万：50,10,5.0%<p>
     * @author lry
     */
    public static class SystemClock {

        private final long period;
        private final AtomicLong now;

        private SystemClock(long period) {
            this.period = period;
            this.now = new AtomicLong(System.currentTimeMillis());
            scheduleClockUpdating();
        }

        private static class InstanceHolder {
            public static final SystemClock INSTANCE = new SystemClock(1);
        }

        private static SystemClock instance() {
            return InstanceHolder.INSTANCE;
        }

        private void scheduleClockUpdating() {
            ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
                public Thread newThread(Runnable runnable) {
                    Thread thread = new Thread(runnable, "System Clock");
                    thread.setDaemon(true);
                    return thread;
                }
            });
            scheduler.scheduleAtFixedRate(new Runnable() {
                public void run() {
                    now.set(System.currentTimeMillis());
                }
            }, period, period, TimeUnit.MILLISECONDS);
        }

        private long currentTimeMillis() {
            return now.get();
        }

        public static long now() {
            return instance().currentTimeMillis();
        }

        public static String nowDate() {
            return new Timestamp(instance().currentTimeMillis()).toString();
        }

    }
}
