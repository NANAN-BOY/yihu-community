const path = require('path');

// 获取行为分类列表
const getBehaviorCategories = async (req, res) => {
  const sql = 'SELECT category_id, category_title, category_details, category_creation_date FROM BehaviorCategories';

  try {
    const [results] = await req.db.query(sql);
    if (results.length === 0) {
      return res.status(404).json({ error: '没有找到行为分类' });
    }
    res.status(200).json(results);
  } catch (err) {
    console.error('Database query error:', err);
    res.status(500).json({ error: '数据库错误' });
  }
};

// 创建行为记录
const createBehaviorRecord = async (req, res) => {
  const {
    user_id,
    category_id,
    behavior_title,
    content_description,
    join_event_id,
    tags,
    approval_status = 0,
  } = req.body;

  // 验证请求数据
  if (!user_id || !category_id || !behavior_title || !content_description) {
    return res.status(400).json({ message: '缺少必要的字段' });
  }

  // 处理上传的文件
  const content_details = req.files.map(file => `/getbehaviorfile/${file.filename}`).join('%');
  

  const insertBehaviorSql = `
    INSERT INTO BehaviorRecords (
      user_id,
      category_id, 
      behavior_title, 
      content_description, 
      content_details, 
      join_event_id, 
      tags, 
      approval_status, 
      record_creation_date, 
      record_last_updated)
    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)`;

  const params = [
    user_id,
    category_id,
    behavior_title,
    content_description,
    content_details,
    join_event_id,
    tags,
    approval_status,
    new Date(),
    new Date(),
  ];

  try {
    const [result] = await req.db.query(insertBehaviorSql, params);

    // 更新用户积分
    const updateUserPointsSql = `
      UPDATE Users 
      SET point_num = point_num + 1, login_last = ? 
      WHERE user_id = ?`;

    await req.db.query(updateUserPointsSql, [new Date(), user_id]);

    // 获取最新积分数
    const [updatedUser] = await req.db.query(`SELECT point_num FROM Users WHERE user_id = ?`, [user_id]);

    // 记录积分变更
    const insertPointsSql = `
      INSERT INTO PointRecords (user_id, record_id, points_awarded, source_of_points, points_awarded_date, points_expiration_date)
      VALUES (?, ?, ?, ?, ?, ?)`;

    const pointsParams = [
      user_id,
      result.insertId,
      1, // 增加1积分
      '发布行为', // 积分来源
      new Date(),
      new Date(new Date().getTime() + 30 * 24 * 60 * 60 * 1000) // 设定积分过期时间为30天后
    ];

    await req.db.query(insertPointsSql, pointsParams);

    // 返回包含新积分数的响应
    res.status(201).json({ 
      record_id: result.insertId, 
      new_points: updatedUser[0].point_num, 
      ...req.body 
    });
  } catch (error) {
    console.error('创建行为记录失败:', error);
    res.status(500).json({ message: '服务器错误' });
  }
};

const getBehaviorFile = (req, res) => {
  const filename = req.params.filename;

  // 构建文件的绝对路径
  const filePath = path.join(__dirname, '../uploads/behavior', filename);

  // 发送文件
  res.sendFile(filePath, (err) => {
    if (err) {
      if (err.code === 'ENOENT') { // 文件未找到
        return res.status(404).json({ message: '文件不存在' }); // 返回 404 状态和消息
      }
      return res.status(err.status).end(); // 处理其他错误
    }
  });
};
// 获取我的所有行为记录
const getMyBehaviorList = async (req, res) => {
  // 使用 req.query 获取查询参数
  const { user_id } = req.query;

  // 验证请求数据
  if (!user_id) {
    return res.status(400).json({ message: '缺少必要的字段' });
  }

  const sql = `SELECT * FROM BehaviorRecords WHERE user_id = ?`;
  const params = [user_id];

  try {
    const [results] = await req.db.query(sql, params);
    if (results.length === 0) {
      return res.status(404).json({ error: '没有找到行为列表' });
    }
    res.status(200).json(results);
  } catch (err) {
    console.error('Database query error:', err);
    res.status(500).json({ error: '数据库错误' });
  }
};

// 删除行为记录
const deleteBehaviorRecord = async (req, res) => {
  const { record_id } = req.params;

  // 验证请求参数
  if (!record_id) {
    return res.status(400).json({ message: '缺少必要的参数：record_id' });
  }

  try {
    // 查询行为记录是否存在
    const [record] = await req.db.query('SELECT * FROM BehaviorRecords WHERE record_id = ?', [record_id]);
    if (record.length === 0) {
      return res.status(404).json({ message: '未找到对应的行为记录' });
    }

    // 删除行为记录
    const deleteSql = 'DELETE FROM BehaviorRecords WHERE record_id = ?';
    await req.db.query(deleteSql, [record_id]);

    // // 删除与该行为记录相关的积分记录
    // const deletePointsSql = 'DELETE FROM PointRecords WHERE record_id = ?';
    // await req.db.query(deletePointsSql, [record_id]);

    res.status(200).json({ message: '行为记录删除成功', record_id });
  } catch (error) {
    console.error('删除行为记录失败:', error);
    res.status(500).json({ message: '服务器错误' });
  }
};

// 导出控制器
module.exports = {
  getBehaviorCategories,
  createBehaviorRecord,
  getBehaviorFile,
  getMyBehaviorList,
  deleteBehaviorRecord,
};
