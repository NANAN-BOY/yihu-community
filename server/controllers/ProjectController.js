const db = require('../db/pool'); // 假设你有一个数据库连接模块

// 处理提交项目申报的业务逻辑
const submitProjectDeclare = async (req, res) => {
  const {
    template_id,
    projectDeclare_user,
    projectDeclare_draftEnable,
    fields, // 项目申报字段数据
  } = req.body;

  const connection = await db.getConnection();
  try {
    // 开始事务
    await connection.beginTransaction();

    // 1. 创建 ProjectDeclare 记录
    const [projectDeclareResult] = await connection.query(
      'INSERT INTO ProjectDeclare (template_id, projectDeclare_user, projectDeclare_create_at, projectDeclare_draftEnable) VALUES (?, ?, NOW(), ?)',
      [template_id, projectDeclare_user, projectDeclare_draftEnable]
    );
    const projectDeclare_id = projectDeclareResult.insertId; // 获取插入后的 projectDeclare_id

    // 2. 创建 ProjectDeclareField 记录
    const fieldValues = fields.map(field => [
      field.templateFields_id,
      field.value, // 仅插入 templateFields_id 和 projectDeclareField_value
    ]);
    await connection.query(
      'INSERT INTO ProjectDeclareField (templateFields_id, projectDeclareField_value) VALUES ?',
      [fieldValues]
    );

    // 3. 创建 ProjectDeclareFieldValueAssociation 记录
    // 获取所有插入的 ProjectDeclareField_id，并构造关联关系
    const [insertedFields] = await connection.query(
      'SELECT projectDeclareField_id, templateFields_id FROM ProjectDeclareField WHERE templateFields_id IN (?)',
      [fields.map(field => field.templateFields_id)]
    );

    const associations = insertedFields.map(field => ({
      projectDeclare_id,
      projectDeclareField_id: field.projectDeclareField_id,
      optimize_enable: false,
      optimize_frequency: 0,
      projectOptimizeRrecord_id: null,
    }));

    const associationValues = associations.map(association => [
      association.projectDeclare_id,
      association.projectDeclareField_id,
      association.optimize_enable,
      association.optimize_frequency,
      association.projectOptimizeRrecord_id,
    ]);

    await connection.query(
      'INSERT INTO ProjectDeclareFieldValueAssociation (projectDeclare_id, projectDeclareField_id, optimize_enable, optimize_frequency, projectOptimizeRrecord_id) VALUES ?',
      [associationValues]
    );

    // 提交事务
    await connection.commit();

    // 返回成功响应
    res.status(200).json({ success: true });
  } catch (error) {
    // 如果发生错误，则回滚事务
    await connection.rollback();
    console.error(error);
    res.status(500).json({ success: false, message: '提交失败，请稍后再试！' });
  } finally {
    connection.release(); // 释放数据库连接
  }
};




module.exports = {
  submitProjectDeclare,
};
