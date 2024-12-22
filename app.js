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
    const { user_name, password } = req.body;
    // 检查是否提供了必要的参数
    if (!user_name || !password) { return res.status(400).send('请输入所有必填字段'); }
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
    } catch (err) { console.error('数据库插入错误:', err); return res.status(500).send('系统错误'); }
});

// 登录页面
app.get('/login', (req, res) => {
    res.render('login');
});
// 登录路由
app.post('/login', async (req, res) => {
    const { user_id, password } = req.body;
    // 验证请求参数是否存在
    if (!user_id || !password) { return res.status(400).send('请输入用户ID和密码'); }
    const sql = 'SELECT user_id, user_name, hash_password, user_role FROM User WHERE user_id = ?';
    try {
        // 使用 async/await 方式查询数据库
        const [results] = await db.query(sql, [user_id]);
        if (results.length === 0) { return res.status(401).send('账号或密码错误'); }
        const user = results[0];
        // 比较密码
        const isMatch = await bcrypt.compare(password, user.hash_password);
        if (!isMatch) { return res.status(402).send('账号或密码错误'); }
        // 登录成功，存储用户信息到 session
        req.session.user = { user_id: user.user_id, user_name: user.user_name, user_role: user.user_role };
        // 判断用户角色，跳转到不同的页面
        if (user.user_role === 3) { return res.redirect('/userdashboard'); }
        if (user.user_role === 2) { return res.redirect('/admindashboard'); }
        // 默认跳转到首页
        res.render('index', { user_name: user.user_name });
    } catch (err) { res.status(500).send('系统错误'); }
});
// 登出路由
app.get('/logout', (req, res) => {
    req.session.destroy((err) => { if (err) { return res.status(500).send('登出失败'); } res.redirect('/login'); });// 登出后跳转到登录页面
});
//基层组织面板路由
app.get('/userdashboard', (req, res) => {
    if (req.session.user?.user_role === undefined || req.session.user.user_role !== 3) { return res.status(401).redirect('/login'); }
    res.render('user/userdashboard', { user_name: req.session.user.user_name });
});

//基层组织管理项目路由
app.get('/user/manage-project', async (req, res) => {
    if (!req.session.user || req.session.user.user_role !== 3) {
        return res.status(401).redirect('/login'); // 验证登录和权限
    }

    try {
        const [entries] = await db.query(
            `SELECT fe.entry_id, pt.template_name, fe.submitted_at
             FROM form_entries fe
             JOIN ProjectTemplate pt ON fe.template_id = pt.template_id
             WHERE fe.user_id = ?
             ORDER BY fe.submitted_at DESC`,
            [req.session.user.user_id]
        );

        // 确保传递 entries 给模板
        res.render('user/manage-project', {
            user_name: req.session.user.user_name,
            entries: entries || [], // 如果为空，则传递空数组
        });
    } catch (error) {
        console.error('加载管理页面失败:', error);
        res.status(500).send('系统错误');
    }
});

//获取所有申报记录路由
app.get('/project-declaration/list', async (req, res) => {
    if (!req.session.user || req.session.user.user_role !== 3) {
        return res.status(401).redirect('/login'); // 验证登录和权限
    }

    try {
        const user_id = req.session.user.user_id;

        // 查询用户提交的申报记录
        const [entries] = await db.query(
            `SELECT fe.entry_id, pt.template_name, fe.submitted_at
             FROM form_entries fe
             JOIN ProjectTemplate pt ON fe.template_id = pt.template_id
             WHERE fe.user_id = ?
             ORDER BY fe.submitted_at DESC`,
            [user_id]
        );

        res.render('user/project/project-declaration-list', {
            user_name: req.session.user.user_name,
            entries // 将查询结果传递给前端模板
        });
    } catch (error) {
        console.error('获取申报列表失败:', error);
        res.status(500).send('系统错误');
    }
});

// 基层组织项目申报路由
app.get('/project-declaration', async (req, res) => {
    if (!req.session.user || req.session.user.user_role !== 3) {
        return res.status(401).redirect('/login'); // 验证登录和权限
    }
    try {
        // 查询当前启用的模板
        const [enabledTemplate] = await db.query('SELECT * FROM ProjectTemplate WHERE template_enable = 1');
        if (enabledTemplate.length === 0) {
            return res.status(404).send('当前没有启用的模板');
        }

        const template = enabledTemplate[0];
        // 查询模板的字段
        const [fields] = await db.query(
            'SELECT * FROM template_fields WHERE template_id = ?',
            [template.template_id]
        );

        // 解析 JSON 字符串为数组
        fields.forEach(field => {
            if (typeof field.template_fields_options === 'string' && field.template_fields_options.trim() !== '') {
                try {
                    field.template_fields_options = JSON.parse(field.template_fields_options);
                } catch (e) {
                    console.error('Failed to parse template_fields_options:', e);
                    field.template_fields_options = []; // 如果解析失败，设置为空数组
                }
            } else {
                field.template_fields_options = []; // 确保始终是一个数组
            }
        });

        res.render('user/project/project-declaration', {
            user_name: req.session.user.user_name,
            template,
            fields, // 模板的字段列表
        });
    } catch (error) {
        console.error('加载申报页面失败:', error);
        res.status(500).send('系统错误');
    }
});

// 提交项目申报表单
app.post('/project-declaration', async (req, res) => {
    console.log('Received req.body:', req.body);

    const { template_id } = req.body;

    // 解析 fields 数据
    const fields = Object.keys(req.body)
        .filter(key => key.startsWith('fields[')) // 筛选出字段数据
        .map(key => {
            const matches = key.match(/fields\[(\d+)\]\[field_value\]/); // 匹配字段 ID
            if (matches) {
                return {
                    template_fields_id: matches[1], // 提取字段 ID
                    field_value: req.body[key]     // 对应的值
                };
            }
            return null;
        })
        .filter(field => field !== null); // 过滤掉无效字段

    console.log('Parsed fields:', fields);

    if (!template_id || fields.length === 0) {
        return res.status(400).send('缺少必要的参数');
    }

    let connection;
    try {
        connection = await db.getConnection();
        await connection.beginTransaction();

        // 创建新的 form_entries 记录
        const [entryResult] = await connection.execute(
            'INSERT INTO form_entries (template_id, user_id, submitted_at) VALUES (?, ?, CURRENT_TIMESTAMP)',
            [template_id, req.session.user.user_id]
        );

        const entry_id = entryResult.insertId;

        // 插入每个字段的值到 field_values 表
        for (const field of fields) {
            const { template_fields_id, field_value } = field;

            await connection.execute(
                'INSERT INTO field_values (entry_id, template_fields_id, field_value) VALUES (?, ?, ?)',
                [entry_id, template_fields_id, field_value]
            );
        }

        await connection.commit();
        res.status(201).send('申报提交成功');
    } catch (error) {
        if (connection) {
            await connection.rollback();
        }
        console.error('提交申报失败:', error);
        res.status(500).send('系统错误');
    } finally {
        if (connection) {
            connection.release();
        }
    }
});

//查看单个申报路由
app.get('/view-project/:entry_id', async (req, res) => {
    if (!req.session.user || req.session.user.user_role !== 3) {
        return res.status(401).redirect('/login'); // 验证登录和权限
    }

    const entry_id = req.params.entry_id;

    try {
        // 查询申报记录的基本信息
        const [entryDetails] = await db.query(
            `SELECT fe.entry_id, pt.template_name, fe.submitted_at
             FROM form_entries fe
             JOIN ProjectTemplate pt ON fe.template_id = pt.template_id
             WHERE fe.entry_id = ? AND fe.user_id = ?`,
            [entry_id, req.session.user.user_id]
        );

        if (entryDetails.length === 0) {
            return res.status(404).send('未找到该申报记录');
        }

        const entry = entryDetails[0];

        // 查询申报的字段值
        const [fieldValues] = await db.query(
            `SELECT tf.template_fields_name, fv.field_value
             FROM field_values fv
             JOIN template_fields tf ON fv.template_fields_id = tf.template_fields_id
             WHERE fv.entry_id = ?`,
            [entry_id]
        );

        res.render('user/project/view-project', {
            user_name: req.session.user.user_name,
            entry,
            fieldValues
        });
    } catch (error) {
        console.error('获取申报详情失败:', error);
        res.status(500).send('系统错误');
    }
});


//区管理员面板路由
app.get('/admindashboard', (req, res) => {
    if (req.session.user?.user_role === undefined || req.session.user.user_role !== 2) { return res.status(401).redirect('/login'); }
    res.render('admin/AdminDashboard', { user_name: req.session.user.user_name });
});
//管理项目模板路由
app.get('/admin/manage-templates', async (req, res) => {
    if (req.session.user?.user_role === undefined || req.session.user.user_role !== 2) { return res.status(401).redirect('/login'); } // 如果用户未授权或不是区管理员
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
// 查看项目模板路由
app.get('/admin/view-template/:templateId', async (req, res) => {
    const templateId = req.params.templateId;
    try {
        const [templateResult] = await db.query('SELECT * FROM ProjectTemplate WHERE template_id = ?', [templateId]);
        if (templateResult.length === 0) {
            return res.status(404).send('模板未找到');
        }
        const template = templateResult[0];
        const [fields] = await db.query('SELECT * FROM template_fields WHERE template_id = ?', [templateId]);

        // 解析 JSON 字符串为数组
        fields.forEach(field => {
            if (typeof field.template_fields_options === 'string' && field.template_fields_options.trim() !== '') {
                try {
                    field.template_fields_options = JSON.parse(field.template_fields_options);
                } catch (e) {
                    console.error('Failed to parse template_fields_options:', e);
                    field.template_fields_options = [];
                }
            }
            // 确保始终是一个数组
            if (!Array.isArray(field.template_fields_options)) {
                field.template_fields_options = [];
            }
        });

        // 合并 fields 到 template 对象中
        template.fields = fields;
        res.render('admin/project/view-template', {
            user_name: req.session.user.user_name,
            template: template // 包含 fields 的模板数据
        });
    }
    catch (error) {
        console.error('Error fetching template:', error);
        res.status(500).send('获取模板失败');
    }
});

// 编辑项目模板路由
app.get('/admin/edit-template/:templateId', async (req, res) => {
    const templateId = req.params.templateId;
    try {
        const [templateResult] = await db.query('SELECT * FROM ProjectTemplate WHERE template_id = ?', [templateId]);
        if (templateResult.length === 0) {
            return res.status(404).send('模板未找到');
        }
        const template = templateResult[0];
        const [fields] = await db.query('SELECT * FROM template_fields WHERE template_id = ?', [templateId]);

        // 解析 JSON 字符串为数组
        fields.forEach(field => {
            if (typeof field.template_fields_options === 'string' && field.template_fields_options.trim() !== '') {
                try {
                    field.template_fields_options = JSON.parse(field.template_fields_options);
                } catch (e) {
                    console.error('Failed to parse template_fields_options:', e);
                    field.template_fields_options = [];
                }
            }
            // 确保始终是一个数组
            if (!Array.isArray(field.template_fields_options)) {
                field.template_fields_options = [];
            }
        });

        // 合并 fields 到 template 对象中
        template.fields = fields;
        res.render('admin/project/edit-template', {
            user_name: req.session.user.user_name,
            template: template // 包含 fields 的模板数据
        });
    } catch (error) {
        console.error('Error fetching template:', error);
        res.status(500).send('获取模板失败');
    }
});
// 编辑项目模板Post
app.post("/admin/edit-template/:templateId", async (req, res) => {
    const { templateId } = req.params;
    const { form_name, form_description, fields } = req.body;

    let connection;
    try {
        connection = await db.getConnection();
        await connection.beginTransaction();

        // 更新模板基本信息
        const sanitizedFormName = form_name !== undefined ? form_name : null;
        const sanitizedFormDescription = form_description !== undefined ? form_description : null;

        await connection.execute(
            "UPDATE ProjectTemplate SET template_name = ?, template_description = ? WHERE template_id = ?",
            [sanitizedFormName, sanitizedFormDescription, templateId]
        );

        // 删除旧的字段
        const [deleteResult] = await connection.execute(
            "DELETE FROM template_fields WHERE template_id = ?",
            [templateId]
        );

        // 插入新的字段
        if (Array.isArray(fields) && fields.length > 0) {
            for (let field of fields) {
                const fieldName = field.template_fields_name || null;
                const fieldType = field.template_fields_type || null;
                const isRequired = field.fields_isRequired !== undefined ? field.fields_isRequired : false;
                const fieldOptions = Array.isArray(field.template_fields_options)
                    ? JSON.stringify(field.template_fields_options)
                    : null;

                if (!fieldName || !fieldType) {
                    console.error("字段值无效，跳过插入：", { fieldName, fieldType });
                    continue; // 跳过无效字段
                }

                await connection.execute(
                    "INSERT INTO template_fields (template_id, template_fields_name, template_fields_type, fields_isRequired, template_fields_options) VALUES (?, ?, ?, ?, ?)",
                    [templateId, fieldName, fieldType, isRequired, fieldOptions]
                );
            }
        } else {
            console.warn("字段数据为空，跳过字段插入");
        }

        await connection.commit();
        res.status(200).json({ message: "模板已成功更新" });
    } catch (error) {
        if (connection) {
            await connection.rollback();
        }
        console.error("更新模板失败：", error.stack);
        res.status(500).json({ message: "更新模板失败", error: error.message });
    } finally {
        if (connection) {
            connection.release();
        }
    }
});
//创建项目模板Get
app.get('/admin/create-template', (req, res) => {
    if (req.session.user?.user_role === undefined || req.session.user.user_role !== 2) { return res.status(401).redirect('/login'); }
    res.render('admin/project/create-template', { user_name: req.session.user.user_name });
});
// 创建项目模板Post
app.post('/admin/create-template', async (req, res) => {
    if (req.session.user?.user_role === undefined || req.session.user.user_role !== 2) { return res.status(401).redirect('/login'); }
    try {
        const { form_name, form_description, fields } = req.body;
        // 保存到数据库
        const [formResult] = await db.execute('INSERT INTO ProjectTemplate (template_name, template_description, template_enable) VALUES (?, ?, ?)', [form_name, form_description, 0]);
        const formId = formResult.insertId;
        // 插入表单字段，确保所有字段都被验证过
        for (let field of fields) {
            const { fieldName, fieldType, isRequired, options } = field;
            const fieldOptions = options ? JSON.stringify(options) : null;  // 如果选项为空，则传递 null
            await db.execute(
                'INSERT INTO template_fields (template_id, template_fields_name, template_fields_type, fields_isRequired, template_fields_options) VALUES (?, ?, ?, ?, ?)',
                [formId, fieldName, fieldType, isRequired, fieldOptions]
            );
        }
        // 返回上传成功信息
        res.status(201).json({ message: 'Form uploaded successfully', formId: formId });
    } catch (error) { res.status(500).json({ message: 'Error uploading form' }); }
});

// 区管理员查看单个申报路由
app.get('/admin/view-project/:entry_id', async (req, res) => {
    if (!req.session.user || req.session.user.user_role !== 2) {
        return res.status(401).redirect('/login'); // 验证管理员权限
    }

    const entry_id = req.params.entry_id;

    try {
        // 查询申报记录的基本信息，包括用户信息
        const [entryDetails] = await db.query(
            `SELECT fe.entry_id, pt.template_name, fe.submitted_at, u.user_name
             FROM form_entries fe
             JOIN ProjectTemplate pt ON fe.template_id = pt.template_id
             JOIN User u ON fe.user_id = u.user_id
             WHERE fe.entry_id = ?`,
            [entry_id]
        );

        if (entryDetails.length === 0) {
            return res.status(404).send('未找到该申报记录');
        }

        const entry = entryDetails[0];

        // 查询申报的字段值
        const [fieldValues] = await db.query(
            `SELECT tf.template_fields_name, fv.field_value
             FROM field_values fv
             JOIN template_fields tf ON fv.template_fields_id = tf.template_fields_id
             WHERE fv.entry_id = ?`,
            [entry_id]
        );

        res.render('admin/view-project', {
            user_name: req.session.user.user_name,
            entry,
            fieldValues
        });
    } catch (error) {
        console.error('获取申报详情失败:', error);
        res.status(500).send('系统错误');
    }
});


// 更换选定模板方法
app.post('/admin/replace-templates', async (req, res) => {
    if (req.session.user?.user_role === undefined || req.session.user.user_role !== 2) { return res.status(401).redirect('/login'); }
    const { newTemplateId } = req.body; // 获取传入的新模板ID
    if (!newTemplateId) { return res.status(400).json({ message: '缺少模板ID' }); }
    const connection = await db.getConnection();
    try {
        await connection.beginTransaction();
        const [currentEnabledTemplates] = await connection.execute('SELECT template_id FROM ProjectTemplate WHERE template_enable = 1 LIMIT 1');
        if (currentEnabledTemplates.length > 0) {
            const oldTemplateId = currentEnabledTemplates[0].template_id;
            await connection.execute('UPDATE ProjectTemplate SET template_enable = 0 WHERE template_id = ?', [oldTemplateId]);
        }
        await connection.execute('UPDATE ProjectTemplate SET template_enable = 1 WHERE template_id = ?', [newTemplateId]);
        await connection.commit(); res.status(200).json({ message: '模板已成功更换' });
    } catch (error) {
        await connection.rollback(); res.status(500).json({ message: '更换模板失败', error: error.message });
    } finally { connection.release(); }
});

//删除模板方法
app.post('/admin/delete-templates', async (req, res) => {
    if (req.session.user?.user_role === undefined || req.session.user.user_role !== 2) { return res.status(401).redirect('/login'); }
    const { deleteTemplateId } = req.body; // 获取传入的模板ID
    if (!deleteTemplateId) { return res.status(400).json({ message: '缺少模板ID' }); }
    const connection = await db.getConnection();
    try {
        await connection.beginTransaction();
        const [template] = await connection.execute('SELECT template_enable FROM ProjectTemplate WHERE template_id = ?', [deleteTemplateId]);
        if (template.length > 0 && template[0].template_enable === 1) { return res.status(400).json({ message: '启用的模板不能删除' }); }
        await connection.execute('DELETE FROM template_fields WHERE template_id = ?', [deleteTemplateId]);
        await connection.execute('DELETE FROM ProjectTemplate WHERE template_id = ?', [deleteTemplateId]);
        await connection.commit();
        res.status(200).json({ message: '模板已成功删除' });
    } catch (error) {
        await connection.rollback(); res.status(500).json({ message: '删除模板失败', error: error.message });
    } finally { connection.release(); }
});

// 查看所有用户提交的申报
app.get('/admin/view-all-submissions', async (req, res) => {
    if (!req.session.user || req.session.user.user_role !== 2) {
        return res.status(401).redirect('/login'); // 验证管理员权限
    }

    try {
        // 查询所有用户提交的申报数据
        const [submissions] = await db.query(`
            SELECT 
                fe.entry_id, 
                fe.submitted_at, 
                fe.user_id, 
                pt.template_name, 
                u.user_name 
            FROM form_entries fe
            JOIN ProjectTemplate pt ON fe.template_id = pt.template_id
            JOIN User u ON fe.user_id = u.user_id
            ORDER BY fe.submitted_at DESC
        `);

        // 检查查询结果
        console.log('查询结果:', submissions);

        // 渲染页面
        res.render('admin/project/view-all-submissions', {
            user_name: req.session.user.user_name,
            submissions: submissions
        });
    } catch (error) {
        console.error('获取用户申报数据失败:', error);
        res.status(500).send('系统错误');
    }
});



// 启动服务器
app.listen(3000, () => {
    console.log('Server started on http://localhost:3000');
});