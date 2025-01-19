const db = require('../db/pool'); // 假设你有一个数据库连接模块

const submitProjectDeclare = async (req, res) => {
  const {
    template_id,
    projectDeclare_user,
    projectDeclare_draftEnable,
    fields, // 项目申报字段数据
    project_name, // 新增的项目名称字段
  } = req.body;

  const connection = await db.getConnection();
  try {
    // 开始事务
    await connection.beginTransaction();

    // 1. 创建 ProjectDeclare 记录
    const [projectDeclareResult] = await connection.query(
      'INSERT INTO ProjectDeclare (template_id, projectDeclare_user, projectDeclare_create_at, projectDeclare_draftEnable, project_name) VALUES (?, ?, NOW(), ?, ?)',
      [template_id, projectDeclare_user, projectDeclare_draftEnable, project_name]  // 插入项目名称
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

const getAllProjects = async (req, res) => {
  const connection = await db.getConnection();
  try {
    // 查询所有项目的基本信息（包括项目名称和用户ID）
    const [projects] = await connection.query(
        `SELECT pd.projectDeclare_id, pd.template_id, pd.projectDeclare_user, pd.projectDeclare_create_at, 
              pd.projectDeclare_draftEnable, pd.project_name
       FROM ProjectDeclare pd`
    );

    // 查询每个项目是否已被优化
    const projectIds = projects.map(project => project.projectDeclare_id);
    const [optimizationStatus] = await connection.query(
        `SELECT projectDeclare_id
       FROM ProjectDeclareFieldValueAssociation
       WHERE projectDeclare_id IN (?) AND optimize_frequency > 0`,
        [projectIds]
    );

    // 将结果整合到项目数据中
    const optimizedProjectIds = optimizationStatus.map(record => record.projectDeclare_id);
    const projectsWithOptimizationStatus = projects.map(project => ({
      ...project,
      isOptimized: optimizedProjectIds.includes(project.projectDeclare_id)
    }));

    // 获取用户信息
    const userIds = projectsWithOptimizationStatus.map(project => project.projectDeclare_user);
    const [users] = await connection.query(
        `SELECT user_id, user_name FROM User WHERE user_id IN (?)`,
        [userIds]
    );

    // 将用户名添加到项目数据中
    const projectsWithUserNames = projectsWithOptimizationStatus.map(project => {
      const user = users.find(u => u.user_id === project.projectDeclare_user);
      return {
        ...project,
        projectDeclare_user_name: user ? user.user_name : '未知用户'  // 添加用户的用户名
      };
    });

    // 返回带有优化状态和用户名的项目列表
    res.status(200).json({ success: true, projects: projectsWithUserNames });
  } catch (error) {
    console.error('获取项目列表失败:', error);
    res.status(500).json({ success: false, message: '获取项目列表失败，请稍后再试！' });
  } finally {
    connection.release(); // 释放数据库连接
  }
};


module.exports = {
  submitProjectDeclare,
  getAllProjects,
};
