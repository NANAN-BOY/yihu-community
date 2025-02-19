// db/pool.js
const mysql = require('mysql2');

// 创建并导出连接池
const pool = mysql.createPool({
    host: '123.249.11.153',
    user: 'project',
    password: 'GGGYCRpwWCSbNPMy',  // 根据实际配置修改密码
    database: 'project',
    waitForConnections: true,
    connectionLimit: 10,
    queueLimit: 0
});
const promisePool = pool.promise();
// 检查连接是否正常
promisePool.getConnection()
    .then(() => {
        console.log('Database connected successfully!');
    })
    .catch((err) => {
        console.error('Database connection failed:', err.message);
    });

module.exports = promisePool;
