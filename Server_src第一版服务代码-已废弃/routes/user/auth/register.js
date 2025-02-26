// routes/register.js
const express = require('express');
const bcrypt = require('bcrypt');
const db = require("../../../db/db"); // 引入UserDao
const router = express.Router();

// 注册页面
router.get('/register', (req, res) => {
    res.render('register');
});
// 注册路由
router.post('/register', async (req, res) => {
    const { user_name, user_phoneNumber, user_password } = req.body;
    //验证请求参数合法性
    {
    if (!user_name || !user_phoneNumber || !user_password) {return res.status(400).send('请输入所有必填字段');}
    if (user_name.length > 25) {return res.status(400).send('用户名不能超过25个字符');}
    const phoneRegex = /^1[3-9]\d{9}$/;
    if (!phoneRegex.test(user_phoneNumber)) {return res.status(400).send('请输入有效的中国手机号');}
    if (user_password.length < 8 || user_password.length > 25) {return res.status(400).send('密码长度应在8到25个字符之间');}
    }

    const user_role = 3;
    const hashedPassword = await bcrypt.hash(user_password, 10);
    const insertSql = `
        INSERT INTO User (user_name, user_phoneNumber, user_hashPassword, user_role, user_accountStatus, user_createDate, user_lastUpdated) 
        VALUES (?, ?, ?, ?, ?, NOW(), NOW())
    `;
    try {
        const [result] = await db.query(insertSql, [user_name, user_phoneNumber, hashedPassword, user_role, 1]);
        const user_id = result.insertId;

        req.session.user = { user_id, user_name, user_phoneNumber, user_role };
        res.redirect('/user/manage-project');
    } catch (err) {
        console.error('数据库插入错误/register:', err);
        return res.status(500).send('系统错误');
    }
});

module.exports = router;
