const express = require('express');
const cors = require('cors');
const bodyParser = require('body-parser');
const authRoutes = require('./routes/authRoutes');
const userRoutes = require('./routes/userRoutes');
const uploadRoutes = require('./routes/uploadRoutes');
const mallRoutes = require('./routes/templateRoutes');
const ProjectRouters = require('./routes/ProjectRouters');
const pool = require('./db/pool'); // 引入连接池

const app = express();
const port = 3001;

// 中间件
app.use(cors());
app.use(express.json());
app.use(bodyParser.json());

// 路由中间件
app.use((req, res, next) => {
  req.db = pool; // 将连接池注入到每个请求中
  next();
});

// 路由
app.use('/api', authRoutes);
app.use('/api', userRoutes);
app.use('/api', uploadRoutes);
app.use('/api', mallRoutes);
app.use('/api', ProjectRouters);

// 启动服务器
app.listen(port, () => {
  console.log(`服务器已经启动：${port}`);
});
