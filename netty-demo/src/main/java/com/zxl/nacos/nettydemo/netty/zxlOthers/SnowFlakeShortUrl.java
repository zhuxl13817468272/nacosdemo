package com.zxl.nacos.nettydemo.netty.zxlOthers;

/**
*   double 8个字节64位   最大数为19位数
*   1bit不用 - 41bit时间戳(最大数为13位数，约(2^41-1)2199亿个毫秒值，换算成69年，所以截止时间=起始时间+69年，超过69年之后，会开始出现负值) - 10bit工作机器id（1024） - 12bit序列化（4096）
*
*/
public class SnowFlakeShortUrl {
    /**
     * 起始的时间戳
     */
    private final static long START_TIMESTAMP = 1480166465631L;

    /**
     * 每一部分占用的位数
     */
    private final static long SEQUENCE_BIT = 12;   //序列号占用的位数
    private final static long MACHINE_BIT = 5;     //机器标识占用的位数
    private final static long DATA_CENTER_BIT = 5; //数据中心占用的位数

    /**
     * 每一部分的最大值
     */
    private final static long MAX_SEQUENCE = -1L ^ (-1L << SEQUENCE_BIT);
    private final static long MAX_MACHINE_NUM = -1L ^ (-1L << MACHINE_BIT);
    private final static long MAX_DATA_CENTER_NUM = -1L ^ (-1L << DATA_CENTER_BIT);

    /**
     * 每一部分向左的位移
     */
    private final static long MACHINE_LEFT = SEQUENCE_BIT;
    private final static long DATA_CENTER_LEFT = SEQUENCE_BIT + MACHINE_BIT;
    private final static long TIMESTAMP_LEFT = DATA_CENTER_LEFT + DATA_CENTER_BIT;

    private long dataCenterId;  //数据中心
    private long machineId;     //机器标识
    private long sequence = 0L; //序列号
    private long lastTimeStamp = -1L;  //上一次时间戳

    private long getNextMill() {
        long mill = getNewTimeStamp();
        while (mill <= lastTimeStamp) {
            mill = getNewTimeStamp();
        }
        return mill;
    }

    private long getNewTimeStamp() {
        return System.currentTimeMillis();
    }

    /**
     * 根据指定的数据中心ID和机器标志ID生成指定的序列号
     *
     * @param dataCenterId 数据中心ID
     * @param machineId    机器标志ID
     */
    public SnowFlakeShortUrl(long dataCenterId, long machineId) {
        if (dataCenterId > MAX_DATA_CENTER_NUM || dataCenterId < 0) {
            throw new IllegalArgumentException("DtaCenterId can't be greater than MAX_DATA_CENTER_NUM or less than 0！");
        }
        if (machineId > MAX_MACHINE_NUM || machineId < 0) {
            throw new IllegalArgumentException("MachineId can't be greater than MAX_MACHINE_NUM or less than 0！");
        }
        this.dataCenterId = dataCenterId;
        this.machineId = machineId;
    }

    /**
     * 产生下一个ID
     *
     * @return
     */
    public synchronized long nextId() {
        long currTimeStamp = getNewTimeStamp();
        if (currTimeStamp < lastTimeStamp) {
            throw new RuntimeException("Clock moved backwards.  Refusing to generate id");
        }

        if (currTimeStamp == lastTimeStamp) {
            //相同毫秒内，序列号自增  与 seq = seq + 1 区别在于：一旦超过了4095，seq会从新变成0
            sequence = (sequence + 1) & MAX_SEQUENCE;
            //同一毫秒的序列数已经达到最大
            if (sequence == 0L) {
                currTimeStamp = getNextMill();
            }
        } else {
            //不同毫秒内，序列号置为0
            sequence = 0L;
        }

        lastTimeStamp = currTimeStamp;

        return (currTimeStamp - START_TIMESTAMP) << TIMESTAMP_LEFT //时间戳部分
                | dataCenterId << DATA_CENTER_LEFT       //数据中心部分
                | machineId << MACHINE_LEFT             //机器标识部分
                | sequence;                             //序列号部分
    }

    public static void main(String[] args) {
        //                                                         1-32         1-32
        SnowFlakeShortUrl snowFlake1_1 = new SnowFlakeShortUrl(1, 1);

        for (int i = 0; i < (1 << 4); i++) {
            //10进制
            System.out.println(snowFlake1_1.nextId());
        }

        System.out.println("===============================================");

        SnowFlakeShortUrl snowFlake2_1 = new SnowFlakeShortUrl(2, 1);

        for (int i = 0; i < (1 << 4); i++) {
            //10进制
            System.out.println(snowFlake2_1.nextId());
        }

        System.out.println("===============================================");

        SnowFlakeShortUrl snowFlake3_1 = new SnowFlakeShortUrl(3, 1);

        for (int i = 0; i < (1 << 4); i++) {
            //10进制
            System.out.println(snowFlake3_1.nextId());
        }

        /**
         *   double 8个字节64位   最大数为19位数
         *   1bit不用 - 41bit时间戳(最大数为13位数，约(2^41-1)2199亿个毫秒值，换算成69年，所以截止时间=起始时间+69年，超过69年之后，会开始出现负值) - 10bit工作机器id（1024） - 12bit序列化（4096）
         *
         */
        System.out.println(" 1bit不用 - 41bit时间戳 - 10bit工作机器id - 12bit序列化（4096）");
        System.out.println(System.currentTimeMillis());
        System.out.println(1L << 41);


        System.out.println("======== -1L ^ (-1L << DATA_CENTER_BIT) ================");
        System.out.println(-1L << 5); //-32
        System.out.println(-1L ^ (-1L << 5)); //31


        /**
         *  与 "&" 的规则是两者都为1时才得1，否则就得0
         *      7 & 6=？
         *          7的2进制是：1 1 1
         *          6的2进制是：1 1 0
         *               结果： 1 1 0
         *           所以：7 & 6= 6
         *
         */
        System.out.println("======== sequence = (sequence + 1) & MAX_SEQUENCE ================");

        System.out.println( 0 & 4095); // 0
        System.out.println( 1 & 4095); // 1

        System.out.println( 4094 & 4095); //4094
        System.out.println( 4095 & 4095); //4095

        System.out.println( 4096 & 4095); //0
        System.out.println( 4097 & 4095); //1


        /**
         *  或 "|" 的规则是两者之一有一个1就得1，否则就得0
         *       7 | 6 =？
         *          7的2进制是：1 1 1
         *          6的2进制是：1 1 0
         *               结果： 1 1 1
         *             所以7 | 6 = 7
         */
        System.out.println("======== -1L ^ (-1L << DATA_CENTER_BIT) ================");
        /**
         *  (currTimeStamp - START_TIMESTAMP) << TIMESTAMP_LEFT //时间戳部分
         *                 | dataCenterId << DATA_CENTER_LEFT       //数据中心部分
         *                 | machineId << MACHINE_LEFT             //机器标识部分
         *                 | sequence;                             //序列号部分
         */
        System.out.println(7 | 6); // 7
        System.out.println(22 | 17 | 12 | 0); //31
        System.out.println(130894474363L<<22 | 2<<17 | 3<< 12 | 0);


    }
}
