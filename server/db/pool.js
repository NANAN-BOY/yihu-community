// db/pool.js
const mysql = require('mysql2');

// 创建并导出连接池
const pool = mysql.createPool({
    host: 'localhost',
    user: 'root',
    password: '123456',  // 根据实际配置修改密码
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
