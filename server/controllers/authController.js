const bcrypt = require('bcryptjs');
const jwt = require('jsonwebtoken');
const JWT_SECRET = process.env.JWT_SECRET || 'QwasErdfTyghXcvsEdgtFs'; // 使用环境变量
// 登录
const login = async (req, res) => {
  const { user_phoneNumber, user_password } = req.body;
  // 验证请求参数是否存在
  if (!user_phoneNumber || !user_password) {return res.status(400).json({ status: 'error', message: '请输入手机号和密码' });}
  const sql = 'SELECT user_id, user_name, user_phoneNumber, user_hashPassword, user_role, user_accountStatus FROM User WHERE user_phoneNumber = ?';
  try {// 查询数据库中的用户信息
    const [results] = await req.db.query(sql, [user_phoneNumber]);
    if (results.length === 0) {return res.status(401).json({ status: 'error', message: '账号或密码错误' });}
    const user = results[0];
    // 验证密码
    const isMatch = await bcrypt.compare(user_password, user.user_hashPassword);
    if (!isMatch) {return res.status(402).json({ status: 'error', message: '账号或密码错误' });}
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
  const { user_name, user_phoneNumber, user_password } = req.body;
  // 验证请求参数合法性
  if (!user_name || !user_phoneNumber || !user_password) {return res.status(400).send('请输入所有必填字段');}
  if (user_name.length > 25) {return res.status(400).send('用户名不能超过25个字符');}
  const phoneRegex = /^1[3-9]\d{9}$/;
  if (!phoneRegex.test(user_phoneNumber)) {return res.status(400).send('请输入有效的中国手机号');}
  if (user_password.length < 8 || user_password.length > 25) {return res.status(400).send('密码长度应在8到25个字符之间');}
  // 检查手机号是否已经被注册
  try {
    const [existingUser] = await req.db.query('SELECT * FROM User WHERE user_phoneNumber = ?', [user_phoneNumber]);
    if (existingUser.length > 0) {return res.status(400).send('手机号已被注册');}
  } catch (err) {return res.status(500).send('系统错误');}
  const user_role = 3; // 默认角色
  const hashedPassword = await bcrypt.hash(user_password, 10);
  const insertSql = `
    INSERT INTO User (user_name, user_phoneNumber, user_hashPassword, user_role, user_accountStatus, user_createDate, user_lastUpdated)
    VALUES (?, ?, ?, ?, ?, NOW(), NOW())
  `;
  try {
    const [result] = await req.db.query(insertSql, [user_name, user_phoneNumber, hashedPassword, user_role, 1]);
    const user_id = result.insertId;

    const payload = {
      user_id: user_id,
      user_name: user_name,
      user_phoneNumber: user_phoneNumber,
      user_role: user_role,
      user_accountStatus: 1,
    };
    // 使用 JWT 签发 token，设置过期时间为 1 小时
    const token = jwt.sign(payload, JWT_SECRET, { expiresIn: '1h' });
    // 返回 token 和用户信息
    const responseData = {
      status: 'success',
      message: '注册成功',
      token: token, // 返回 token
      user: {
        user_id: user_id,
        user_name: user_name,
        user_phoneNumber: user_phoneNumber,
        user_role: user_role,
        user_accountStatus: 1,
      }
    };
    return res.json(responseData);
  } catch (err) {
    return res.status(500).send('系统错误');
  }
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
