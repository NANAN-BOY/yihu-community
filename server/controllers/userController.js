const path = require('path'); 
const fs = require('fs');

// 获取用户信息
const getUser = async (req, res) => {
  const userId = req.params.id;
  const sql = 'SELECT username, email, phone_number FROM Users WHERE user_id = ?';
  
  try {
    const [results] = await req.db.query(sql, [userId]);
    if (results.length === 0) {
      return res.status(404).json({ error: '用户未找到' });
    }
    res.status(200).json(results[0]);
  } catch (err) {
    console.error('Database query error:', err);
    res.status(500).json({ error: '数据库错误' });
  }
};

const decryptUserId = (hashedId) => {
  return Buffer.from(hashedId, 'base64').toString('utf-8'); // Base64 解码
};

const getAvatar = (req, res) => {
  const hashedUserId = req.params.hashedUserId; // 从请求参数中获取加密后的用户ID
  const userId = decryptUserId(hashedUserId); // 解密用户ID
  const avatarPath = path.join(__dirname, '../uploads/avatars', `${userId}.png`); 

  fs.access(avatarPath, fs.constants.F_OK, (err) => {
    if (err) {
      return res.status(404).json({ error: '头像未找到' });
    }
    res.sendFile(avatarPath, (err) => {
      if (err) {
        return res.status(500).json({ error: '头像下载失败' });
      }
    });
  });
};

module.exports = { getUser , getAvatar};
