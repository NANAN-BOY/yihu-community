const bcrypt = require('bcryptjs');
const jwt = require('jsonwebtoken');
const JWT_SECRET = process.env.JWT_SECRET || 'QwasErdfTyghXcvsEdgtFs'; // 使用环境变量

const login = async (req, res) => {
  const { user_id, password } = req.body;

  if (!user_id || !password) {
    return res.status(400).send('请输入用户ID和密码');
  }

  const sql = 'SELECT user_id, user_name, hash_password, user_role FROM User WHERE user_id = ?';
  
  try {
    const [results] = await db.query(sql, [user_id]);

    if (results.length === 0) {
      return res.status(401).send('账号或密码错误');
    }

    const user = results[0];
    
    // 比较密码
    const isMatch = await bcrypt.compare(password, user.hash_password);
    if (!isMatch) {
      return res.status(402).send('账号或密码错误');
    }

    // 将用户信息存储在 session 中
    req.session.user = {
      user_id: user.user_id,
      user_name: user.user_name,
      user_role: user.user_role
    };

    // 根据角色跳转
    if (user.user_role === 2) {
      return res.render('userdashboard', { user_name: user.user_name });
    }

    // 对于其他角色，跳转到默认首页（可以根据需求调整）
    res.render('index', { user_name: user.user_name });

  } catch (err) {
    console.error('数据库查询错误:', err);
    res.status(500).send('系统错误');
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
  const { phone_number, password } = req.body;
  if (!phone_number || !password) {
    return res.status(400).json({ error: '请输入手机号和密码' });
  }

  const checkUserSql = 'SELECT * FROM Users WHERE phone_number = ?';
  try {
    const [results] = await req.db.query(checkUserSql, [phone_number]);
    if (results.length > 0) {
      return res.status(409).json({ error: '手机号已存在' });
    }

    const randomDigits = generateRandomNumberString(5);
    const username = `用户${randomDigits}`; // 创建用户名
    const hashedPassword = await bcrypt.hash(password, 10);

    const now = new Date();
    const insertUserSql = 'INSERT INTO Users (username, password_hash, email, phone_number, avatar_url, personal_profile, preferences, point_num, registration_date, login_last, forbid_settings, qq_login_id, WeChat_login_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)';
    await req.db.query(insertUserSql, [username, hashedPassword, null, phone_number, '/api/avatar/MA==' , '无', null, 0, now, now, 0, null, null]);
    res.status(201).json({ message: '注册成功' });
  } catch (err) {
    console.error('Database query error:', err);
    res.status(500).json({ error: '服务器错误' });
  }
};

// 恢复登录状态
const restoreLogin = async (req, res) => {
  const token = req.headers.authorization?.split(' ')[1];
  if (!token) {
    return res.status(401).json({ error: '未提供 token' });
  }
  
  try {
    const decoded = jwt.verify(token, JWT_SECRET);
    const userId = decoded.id;
    const sql = 'SELECT user_id, username, email, phone_number, avatar_url, preferences, point_num, registration_date, login_last FROM Users WHERE user_id = ?';
    const [results] = await req.db.query(sql, [userId]);

    if (results.length === 0) {
      return res.status(404).json({ error: '用户未找到' });
    }
    
    const user = results[0];
    res.json({
      message: '恢复成功',
      user: {
        user_id: user.user_id,
        username: user.username,
        email: user.email,
        phone_number: user.phone_number,
        avatar_url: user.avatar_url,
        preferences: user.preferences,
        point_num: user.point_num,
        registration_date: user.registration_date,
        login_last: user.login_last
      }
    });
  } catch (err) {
    console.error('Token verification or database error:', err);
    res.status(500).json({ error: '服务器错误' });
  }
};

// 导出控制器
module.exports = {
  login,
  restoreLogin,
  register
};
