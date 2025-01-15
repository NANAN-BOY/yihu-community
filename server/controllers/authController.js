const bcrypt = require('bcryptjs');
const jwt = require('jsonwebtoken');
const JWT_SECRET = process.env.JWT_SECRET || 'QwasErdfTyghXcvsEdgtFs'; // 使用环境变量
// 登录
const login = async (req, res) => {
  const { user_phoneNumber, user_password } = req.body;

  // 验证请求参数是否存在
  if (!user_phoneNumber || !user_password) {
    return res.status(400).json({ status: 'error', message: '请输入手机号和密码' });
  }

  const sql = 'SELECT user_id, user_name, user_phoneNumber, user_hashPassword, user_role, user_accountStatus FROM User WHERE user_phoneNumber = ?';

  try {
    // 查询数据库中的用户信息
    const [results] = await req.db.query(sql, [user_phoneNumber]);

    if (results.length === 0) {
      return res.status(401).json({ status: 'error', message: '账号或密码错误' });
    }

    const user = results[0];

    // 验证密码
    const isMatch = await bcrypt.compare(user_password, user.user_hashPassword);
    if (!isMatch) {
      return res.status(402).json({ status: 'error', message: '账号或密码错误' });
    }

    // 生成 JWT token
    const payload = {
      user_id: user.user_id,
      user_name: user.user_name,
      user_phoneNumber: user.user_phoneNumber,
      user_role: user.user_role,
      user_accountStatus: user.user_accountStatus,
    };

    // 使用 JWT 签发 token，设置过期时间为 1 小时
    const token = jwt.sign(payload, JWT_SECRET, { expiresIn: '1h' });

    // 返回 token 和用户信息
    const responseData = {
      status: 'success',
      message: '登录成功',
      token: token, // 返回 token
      user: {
        user_id: user.user_id,
        user_name: user.user_name,
        user_phoneNumber: user.user_phoneNumber,
        user_role: user.user_role,
        user_accountStatus: user.user_accountStatus,
      }
    };

    return res.json(responseData);

  } catch (err) {
    console.error(err); // 记录系统错误
    return res.status(500).json({ status: 'error', message: '系统错误' });
  }
};

// 生成5位随机数字
const generateRandomNumberString = (length) => {
  let result = '';
  for (let i = 0; i < length; i++) {
    const randomDigit = Math.floor(Math.random() * 10);
    result += randomDigit;
  }
  return result;
};

// 注册
const register = async (req, res) => {

};

// 恢复登录状态
const restoreLogin = async (req, res) => {

};

// 导出控制器
module.exports = {
  login,
  restoreLogin,
  register
};
