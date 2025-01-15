const mysql = require('mysql2');

// 创建连接池
const pool = mysql.createPool({
    host: 'localhost',
    user: 'root',
    password: 'Qwertyuiop123',  // 根据实际配置修改密码
    database: 'project',
    waitForConnections: true,
    connectionLimit: 10,  // 最大连接数
    queueLimit: 0  // 队列长度限制（0表示无限制）
});

// 获取连接池的Promise封装，方便使用async/await
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
