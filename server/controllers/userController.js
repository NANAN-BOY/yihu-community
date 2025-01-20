const path = require('path'); 
const fs = require('fs');
const db = require('../db/pool');
const getUserListByRoleAndStatus = async (req, res) => {
  const { user_role, user_accountStatus } = req.query;
  // 检查参数是否存在
  if (user_role === undefined || user_accountStatus === undefined) {return res.status(400).json({ message: '缺少必需的参数' });}
  try {
    // 查询符合条件的用户
    const [users] = await db.query('SELECT user_id, user_name FROM User WHERE user_role = ? AND user_accountStatus = ?', [user_role, user_accountStatus]);
    // 返回用户列表
    return res.status(200).json({ message: '用户列表获取成功', users });
  } catch (error) {
    console.error(error);
    return res.status(500).json({ message: '服务器错误，请稍后重试' });
  }
};
const getUserById = async (req, res) => {
  const { id } = req.params;

  // 检查参数是否存在
  if (!id) {
    return res.status(400).json({ message: '缺少用户ID' });
  }

  try {
    // 查询用户信息
    const [users] = await db.query(
        'SELECT user_id, user_name, user_phoneNumber, user_role, user_accountStatus, user_createDate, user_lastUpdated FROM User WHERE user_id = ?',
        [id]
    );

    // 检查用户是否存在
    if (users.length === 0) {
      return res.status(404).json({ message: '用户不存在' });
    }

    // 返回用户信息
    return res.status(200).json({ message: '用户信息获取成功', user: users[0] });
  } catch (error) {
    console.error(error);
    return res.status(500).json({ message: '服务器错误，请稍后重试' });
  }
};



module.exports = { getUserListByRoleAndStatus,getUserById};
