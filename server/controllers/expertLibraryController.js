const db = require('../db/pool');
const moment = require('moment');
const bcrypt = require('bcryptjs');
// 邀请专家的控制器方法
const inviteExpert = async (req, res) => {
    const { invite_user_id, invite_deadline } = req.body;

    // 检查参数是否存在
    if (!invite_user_id || !invite_deadline) {
        return res.status(400).json({ message: '缺少必要的参数' });
    }

    // 获取当前时间作为创建时间
    const invite_create_at = new Date();

    const sql = `
        INSERT INTO InviteSpecialisRrecord (
            invite_user_id, invite_create_at, invite_deadline, invite_isAgree, Specialis_user_id, invite_refuseReason
        ) VALUES (?, ?, ?, NULL, NULL, NULL)
    `;

    try {
        // 执行插入操作
        const [results] = await db.query(sql, [invite_user_id, invite_create_at, invite_deadline]);

        // 返回成功并包含新插入的记录 ID
        return res.status(200).json({
            message: '邀请专家成功',
            inviteSpecialisRrecord_id: results.insertId // 返回新插入的记录 ID
        });
    } catch (err) {
        console.error('数据库错误:', err);
        return res.status(500).json({ message: '服务器错误' });
    }
};

// 查询邀请信息的控制器方法
const getInviteInfo = async (req, res) => {
    const { inviteId } = req.params;  // 获取 URL 中的邀请 ID

    // 检查 inviteId 是否存在
    if (!inviteId) {
        return res.status(400).json({ message: '缺少邀请 ID' });
    }

    // SQL 查询：查找对应的邀请记录并关联用户表获取邀请人用户名
    const sql = `
        SELECT u.user_name, ir.invite_deadline
        FROM InviteSpecialisRrecord ir
        JOIN User u ON ir.invite_user_id = u.user_id
        WHERE ir.inviteSpecialisRrecord_id = ?
    `;

    try {
        // 执行查询
        const [results] = await db.query(sql, [inviteId]);

        // 检查是否存在对应的邀请记录
        if (results.length === 0) {
            return res.status(404).json({ message: '未找到该邀请记录' });
        }

        // 返回邀请人用户名和截止时间
        const inviteInfo = results[0];  // 结果通常只有一条记录
        return res.status(200).json({
            message: '查询成功',
            invite_user_name: inviteInfo.user_name,
            invite_deadline: inviteInfo.invite_deadline,
        });
    } catch (err) {
        console.error('数据库错误:', err);
        return res.status(500).json({ message: '服务器错误' });
    }
};
//专家注册
const jwt = require('jsonwebtoken');
const JWT_SECRET = 'your_jwt_secret_key'; // 替换为你的 JWT 密钥

const expertRegister = async (req, res) => {
    const { inviteId, userName, userPhoneNumber, userPassword } = req.body;

    // 1. 校验参数
    if (!inviteId || !userName || !userPhoneNumber || !userPassword) {
        return res.status(400).json({ message: '缺少必需的字段' });
    }

    try {
        // 2. 检查手机号是否已经被注册
        const [existingUser] = await db.query('SELECT * FROM User WHERE user_phoneNumber = ?', [userPhoneNumber]);
        if (existingUser.length > 0) {
            return res.status(400).json({ message: '该手机号已被注册' });
        }

        // 3. 查找邀请记录
        const [inviteRecords] = await db.query('SELECT * FROM InviteSpecialisRrecord WHERE inviteSpecialisRrecord_id = ?', [inviteId]);
        if (inviteRecords.length === 0) {
            return res.status(404).json({ message: '无效的邀请ID' });
        }
        const inviteRecord = inviteRecords[0];

        // 4. 检查邀请是否已经被接受
        if (inviteRecord.invite_isAgree !== null) {
            return res.status(400).json({ message: '邀请已失效' });
        }

        // 5. 验证邀请是否过期
        const currentDate = moment();
        const inviteDeadline = moment(inviteRecord.invite_deadline);
        if (currentDate.isAfter(inviteDeadline)) {
            return res.status(400).json({ message: '邀请已经过期' });
        }

        // 6. 创建新用户并进行密码加密
        const salt = await bcrypt.genSalt(10);
        const hashedPassword = await bcrypt.hash(userPassword, salt);

        const [newUserResult] = await db.query(
            'INSERT INTO User (user_name, user_phoneNumber, user_hashPassword, user_role, user_accountStatus, user_createDate, user_lastUpdated) VALUES (?, ?, ?, ?, ?, ?, ?)',
            [userName, userPhoneNumber, hashedPassword, 4, 1, new Date(), new Date()]
        );
        const newUserId = newUserResult.insertId; // 获取新插入的用户ID

        // 7. 更新邀请记录，标记专家已注册且同意邀请
        await db.query('UPDATE InviteSpecialisRrecord SET Specialis_user_id = ?, invite_isAgree = ? WHERE inviteSpecialisRrecord_id = ?', [newUserId, true, inviteId]);

        // 8. 自动登录：生成 JWT token 并返回用户信息
        const payload = {
            user_id: newUserId,
            user_name: userName,
            user_phoneNumber: userPhoneNumber,
            user_role: 4, // 专家角色
            user_accountStatus: 1, // 正常状态
        };
        const token = jwt.sign(payload, JWT_SECRET, { expiresIn: '1h' });

        // 9. 返回成功消息和登录信息
        return res.status(201).json({
            message: '专家注册成功并已自动登录',
            token: token,
            user: {
                user_id: newUserId,
                user_name: userName,
                user_phoneNumber: userPhoneNumber,
                user_role: 4,
                user_accountStatus: 1,
            },
        });

    } catch (error) {
        console.error(error);
        return res.status(500).json({ message: '服务器错误，请稍后再试' });
    }
};
// 根据专家的user_id查询邀请人信息（邀请人的user_id和user_name）
const getInviteUserInfo = async (req, res) => {
    const { user_id } = req.params;  // 获取 URL 中的专家 user_id

    // 检查 user_id 是否存在
    if (!user_id) {
        return res.status(400).json({ message: '缺少专家 user_id' });
    }

    // SQL 查询：查找邀请记录并关联用户表获取邀请人用户ID和用户名
    const sql = `
        SELECT u.user_id AS invite_user_id, u.user_name AS invite_user_name
        FROM InviteSpecialisRrecord ir
        JOIN User u ON ir.invite_user_id = u.user_id
        WHERE ir.Specialis_user_id = ?
    `;

    try {
        // 执行查询
        const [results] = await db.query(sql, [user_id]);

        // 检查是否存在对应的邀请记录
        if (results.length === 0) {
            return res.status(404).json({ message: '未找到该专家的邀请信息' });
        }

        // 返回邀请人信息
        const inviteInfo = results[0];  // 结果通常只有一条记录
        return res.status(200).json({
            message: '查询成功',
            invite_user_id: inviteInfo.invite_user_id,
            invite_user_name: inviteInfo.invite_user_name,
        });
    } catch (err) {
        console.error('数据库错误:', err);
        return res.status(500).json({ message: '服务器错误' });
    }
};

const expertRefuseInvitation = async (req, res) => {
    const { inviteSpecialisRrecord_id, expertRefuseInvitationReason } = req.body;

    try {
        // 检查请求参数是否完整
        if (!inviteSpecialisRrecord_id ||!expertRefuseInvitationReason) {
            return res.status(400).json({ error: 'inviteSpecialisRrecord_id and expertRefuseInvitationReason are required' });
        }

        // 执行更新操作，将邀请记录标记为已拒绝，并记录拒绝原因
        const [result] = await db.execute(
            'UPDATE InviteSpecialisRrecord SET invite_isAgree = false, invite_refuseReason =? WHERE inviteSpecialisRrecord_id =?',
            [expertRefuseInvitationReason, inviteSpecialisRrecord_id]
        );

        // 检查是否有记录被更新
        if (result.affectedRows === 0) {
            return res.status(404).json({ error: 'Invitation record not found' });
        }

        // 返回成功响应
        res.status(200).json({ message: 'Invitation refused successfully' });
    } catch (error) {
        console.error('Error refusing invitation:', error);
        res.status(500).json({ error: 'Internal server error' });
    }
};



module.exports = {
    inviteExpert,
    getInviteInfo,
    expertRegister,// 导出查询邀请信息的方法
    getInviteUserInfo,
    expertRefuseInvitation
};