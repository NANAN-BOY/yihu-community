var express = require('express');
var path = require('path');
var bodyParser = require('body-parser');
var formRouter = require('./routes/formRouter');  // 引入表单路由
var indexRouter = require('./routes/indexRouter.js');
const db = require('./db/db');
var app = express();
const session = require('express-session');
const bcrypt = require('bcryptjs');



// 设置视图引擎为 ejs
app.set('view engine', 'ejs');
app.set('views', path.join(__dirname, 'views'));

// 设置静态文件目录
app.use(express.static(path.join(__dirname, 'public')));

// 配置 session
app.use(session({
    secret: 'your-secret-key', // 用于加密 session ID 的密钥
    resave: false,
    saveUninitialized: true,
    cookie: { secure: false } // 如果使用 HTTPS，则将 secure 设置为 true
  }));

// 中间件
app.use(bodyParser.urlencoded({ extended: false }));
app.use(bodyParser.json());

// 使用路由
app.use('/', formRouter);
app.use('/', indexRouter);

// 注册页面
app.get('/register', (req, res) => {
    res.render('register');
});
// 注册路由
app.post('/register', async (req, res) => {
    const {user_name, password } = req.body;
    // 检查是否提供了必要的参数
    if (!user_name || !password) {return res.status(400).send('请输入所有必填字段');}
    // 初始角色为 3 （普通组织）
    const user_role = 3;
    // 对密码进行加密
    const hashedPassword = await bcrypt.hash(password, 10);
    // 插入新用户到数据库
    const insertSql = 'INSERT INTO User (user_name, hash_password, user_role) VALUES (?, ?, ?)';
    try {
        // 使用 promisePool 执行插入操作
        const [result] = await db.query(insertSql, [user_name, hashedPassword, user_role]);
        const user_id = result.insertId;  // 获取新用户的 user_id
        // 注册成功，存储用户信息到 session
        req.session.user = { user_id, user_name, user_role };
        // 注册成功后跳转到主页
        res.redirect('/userdashboard');
    } catch (err) {console.error('数据库插入错误:', err);return res.status(500).send('系统错误');}
});

// 登录页面
app.get('/login', (req, res) => {
    res.render('login');
  });
// 登录路由
app.post('/login', async (req, res) => {
    const { user_id, password } = req.body;
    // 验证请求参数是否存在
    if (!user_id || !password) {return res.status(400).send('请输入用户ID和密码');}
    const sql = 'SELECT user_id, user_name, hash_password, user_role FROM User WHERE user_id = ?';
    try {
      // 执行查询数据库操作，查找用户信息
      db.query(sql, [user_id], async (err, results) => {
        if (err) {console.error('数据库查询错误:', err);return res.status(500).send('系统错误');}
        if (results.length === 0) {return res.status(401).send('账号或密码错误');}
        const user = results[0];
        // 比较密码
        const isMatch = await bcrypt.compare(password, user.hash_password);
        if (!isMatch) {return res.status(402).send('账号或密码错误');}
        // 登录成功，存储用户信息到 session
        req.session.user = {user_id: user.user_id,user_name: user.user_name,user_role: user.user_role};
        // 判断用户角色，跳转到不同的页面
        if (user.user_role === 2) {
          // 如果角色是 2，跳转到用户仪表板页面
          return res.render('userdashboard', { user_name: user.user_name });
        }
        // 默认跳转到首页
        res.render('index', { user_name: user.user_name });
      });
    } catch (err) {console.error('系统错误:', err);res.status(500).send('系统错误');}
});
  
// 登出路由
app.get('/logout', (req, res) => {
    req.session.destroy((err) => {
        if (err) {
            return res.status(500).send('登出失败');
        }
        res.redirect('/login'); // 登出后跳转到登录页面
    });
});

  
//dashboard路由
app.get('/userdashboard', (req, res) => {
    sessionCheck(req, res);
    res.render('userdashboard', { user_name: req.session.user.user_name });
  });

// 启动服务器
app.listen(3000, () => {
    console.log('Server started on http://localhost:3000');
});

// 身份验证函数
function sessionCheck(req, res) {
    // 检查 session 中是否存在用户信息
    if (!req.session.user) {
        // 如果 session 中没有用户信息，返回 401 未授权错误
        return res.status(401).redirect('/login'); // 重定向到登录页面
    }
    // 如果 session 中存在用户信息，继续执行请求
    return true;
}
