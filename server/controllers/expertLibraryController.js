const db = require('../db/pool');

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

module.exports = {
    inviteExpert,
    getInviteInfo  // 导出查询邀请信息的方法
};