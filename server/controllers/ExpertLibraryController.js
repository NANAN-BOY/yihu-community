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

module.exports = {
    inviteExpert
};
