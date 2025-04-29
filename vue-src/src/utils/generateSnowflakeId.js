export const generateSnowflakeId = (() => {
    const epoch = new Date('2024-01-01').getTime();  // 自定义起始时间
    let machineId = Math.floor(Math.random() * 1024); // 随机10位机器ID
    let lastTimestamp = 0;
    let sequence = 0;

    return () => {
        let timestamp = Date.now() - epoch;

        // 处理时钟回拨（简单等待策略）
        while (timestamp < lastTimestamp) {
            timestamp = Date.now() - epoch;
        }

        // 同一毫秒内自增序列号
        if (timestamp === lastTimestamp) {
            sequence = (sequence + 1) & 0xFFF; // 12位序列号（0~4095）
            if (sequence === 0) timestamp++;   // 序列号耗尽时强制跳到下一毫秒
        } else {
            sequence = 0;
        }

        lastTimestamp = timestamp;

        // 组合为BigInt并转为字符串（兼容旧环境）
        return (
            (BigInt(timestamp) << BigInt(22)) |
            (BigInt(machineId) << BigInt(12)) |
            BigInt(sequence)
        ).toString();
    };
})();
