// routes/login.js
const express = require('express');
const bcrypt = require('bcrypt');
const db = require("../../../db/db");
const router = express.Router();

// 登录页面
router.get('/login', (req, res) => {
    res.render('login');
});

// 登录路由
router.post('/login', async (req, res) => {
    const { user_phoneNumber, user_password } = req.body;
    // 验证请求参数是否存在
    if (!user_phoneNumber || !user_password) {return res.status(400).json({ status: 'error', message: '请输入手机号和密码' });}
    const sql = 'SELECT user_id, user_name,user_phoneNumber, user_hashPassword, user_role FROM User WHERE user_phoneNumber = ?';
    try {
        // 使用 async/await 方式查询数据库
        const [results] = await db.query(sql, [user_phoneNumber]);
        if (results.length === 0) {return res.status(401).json({ status: 'error', message: '账号或密码错误' });}
        const user = results[0];
        const isMatch = await bcrypt.compare(user_password, user.user_hashPassword);
        if (!isMatch) {return res.status(402).json({ status: 'error', message: '账号或密码错误' });}
        // 登录成功
        req.session.user = { user_id: user.user_id, user_name: user.user_name, user_role: user.user_role };
        // 跳转
        if (user.user_role === 3) {return res.json({ status: 'success', message: '登录成功', redirectUrl: '/user/manage-project' });}
        if (user.user_role === 2) {return res.json({ status: 'success', message: '登录成功', redirectUrl: '/admin/manage-templates' });}

    } catch (err) {
        console.error(err); // 记录系统错误
        return res.status(500).json({ status: 'error', message: '系统错误' });
    }
});

module.exports = router;
