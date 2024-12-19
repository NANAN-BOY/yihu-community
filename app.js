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
        // 使用 async/await 方式查询数据库
        const [results] = await db.query(sql, [user_id]);
        if (results.length === 0) {return res.status(401).send('账号或密码错误');}
        const user = results[0];
        // 比较密码
        const isMatch = await bcrypt.compare(password, user.hash_password);
        if (!isMatch) {return res.status(402).send('账号或密码错误');}
        // 登录成功，存储用户信息到 session
        req.session.user = { user_id: user.user_id, user_name: user.user_name, user_role: user.user_role };
        // 判断用户角色，跳转到不同的页面
        if (user.user_role === 3) {return res.redirect('/userdashboard');}
        if (user.user_role === 2) {return res.redirect('/admindashboard');}
        // 默认跳转到首页
        res.render('index', { user_name: user.user_name });
    } catch (err) {res.status(500).send('系统错误');}
});
// 登出路由
app.get('/logout', (req, res) => {
    req.session.destroy((err) => {if (err) {return res.status(500).send('登出失败');}res.redirect('/login'); });// 登出后跳转到登录页面
});
//基层组织面板路由
app.get('/userdashboard', (req, res) => {if (req.session.user?.user_role===undefined||req.session.user.user_role!==3) {return res.status(401).redirect('/login');} 
    res.render('user/userdashboard', { user_name: req.session.user.user_name });
});
// 基层组织项目申报路由
app.get('/project-declaration', (req, res) => {if (req.session.user?.user_role===undefined||req.session.user.user_role!==3) {return res.status(401).redirect('/login');} 
    res.render('user/project/project-declaration'); // 渲染项目申报页面
});


//区管理员面板路由
app.get('/admindashboard', (req, res) => {if (req.session.user?.user_role===undefined||req.session.user.user_role!==2) {return res.status(401).redirect('/login');} 
    res.render('admin/AdminDashboard', { user_name: req.session.user.user_name });
});
//管理项目模板路由
app.get('/admin/manage-templates',  async (req, res) => {if (req.session.user?.user_role === undefined || req.session.user.user_role !== 2) {return res.status(401).redirect('/login');} // 如果用户未授权或不是区管理员
    // 查询所有模板
    try {
        // 使用 async/await 执行查询所有模板
        const [results] = await db.query('SELECT * FROM ProjectTemplate');
        // 渲染模板管理页面并传递查询到的模板数据
        res.render('admin/project/manage-templates', { 
            user_name: req.session.user.user_name,
            templates: results // 模板数据
        });
    } catch (err) {
        console.error('数据库查询失败:', err);
        return res.status(500).send('数据库查询失败');
    }
});
//创建项目模板Get
app.get('/admin/create-template', (req, res) => {if (req.session.user?.user_role===undefined||req.session.user.user_role!==2) {return res.status(401).redirect('/login');} 
    res.render('admin/project/create-template', { user_name: req.session.user.user_name });
}); 
// 创建项目模板Post
app.post('/admin/create-template', async (req, res) => {if (req.session.user?.user_role===undefined||req.session.user.user_role!==2) {return res.status(401).redirect('/login');} 
    try {
      const { form_name, form_description, fields } = req.body;
      // 保存到数据库
      const [formResult] = await db.execute('INSERT INTO ProjectTemplate (template_name, template_description, template_enable) VALUES (?, ?, ?)',[form_name, form_description,0]);
      const formId = formResult.insertId;
      // 插入表单字段，确保所有字段都被验证过
      for (let field of fields) {
      const { fieldName, fieldType, isRequired, options } = field;
      const fieldOptions = options ? JSON.stringify(options) : null;  // 如果选项为空，则传递 null
      await db.execute(
        'INSERT INTO template_fields (template_id, template_fields_name, template_fields_type, fields_isRequired, template_fields_options) VALUES (?, ?, ?, ?, ?)',
        [formId, fieldName, fieldType, isRequired, fieldOptions]
    );}
    // 返回上传成功信息
    res.status(201).json({message: 'Form uploaded successfully',formId: formId});
    } catch (error){res.status(500).json({ message: 'Error uploading form' });}
});
// 更换模板方法
app.post('/admin/replace-templates', async (req, res) => {if (req.session.user?.user_role===undefined||req.session.user.user_role!==2) {return res.status(401).redirect('/login');} 
    const { newTemplateId } = req.body; // 获取传入的新模板ID
    if (!newTemplateId) {return res.status(400).json({ message: '缺少模板ID' });}
    const connection = await db.getConnection();
    try {
        await connection.beginTransaction();
        const [currentEnabledTemplates] = await connection.execute('SELECT template_id FROM ProjectTemplate WHERE template_enable = 1 LIMIT 1');
        if (currentEnabledTemplates.length > 0) {
            const oldTemplateId = currentEnabledTemplates[0].template_id;
            await connection.execute('UPDATE ProjectTemplate SET template_enable = 0 WHERE template_id = ?',[oldTemplateId]);
        }
        await connection.execute('UPDATE ProjectTemplate SET template_enable = 1 WHERE template_id = ?',[newTemplateId]);
        await connection.commit();res.status(200).json({ message: '模板已成功更换' });
    } catch (error) {await connection.rollback();res.status(500).json({ message: '更换模板失败', error: error.message });
    } finally {connection.release();}
});
//删除模板方法
app.post('/admin/delete-templates', async (req, res) => {if (req.session.user?.user_role===undefined||req.session.user.user_role!==2) {return res.status(401).redirect('/login');} 
    const { deleteTemplateId } = req.body; // 获取传入的模板ID
    if (!deleteTemplateId) {return res.status(400).json({ message: '缺少模板ID' });}
    const connection = await db.getConnection();
    try {
        await connection.beginTransaction(); 
        const [template] = await connection.execute('SELECT template_enable FROM ProjectTemplate WHERE template_id = ?', [deleteTemplateId]);
        if (template.length > 0 && template[0].template_enable === 1) {return res.status(400).json({ message: '启用的模板不能删除' });}
        await connection.execute('DELETE FROM template_fields WHERE template_id = ?', [deleteTemplateId]);
        await connection.execute('DELETE FROM ProjectTemplate WHERE template_id = ?', [deleteTemplateId]);
        await connection.commit(); 
        res.status(200).json({ message: '模板已成功删除' });
    } catch (error) {await connection.rollback(); res.status(500).json({ message: '删除模板失败', error: error.message });
    } finally {connection.release();}
});


// 启动服务器
app.listen(3000, () => {
    console.log('Server started on http://localhost:3000');
});