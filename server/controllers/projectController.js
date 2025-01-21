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
const getMyProjects = async (req, res) => {
  const connection = await db.getConnection();
  try {
    const { userId } = req.query;  // 从请求的查询参数中获取 userId

    if (!userId) {
      return res.status(400).json({ success: false, message: '用户未授权' });
    }

    // 查询该用户的所有项目
    const [projects] = await connection.query(
        `SELECT pd.projectDeclare_id, pd.template_id, pd.projectDeclare_user, pd.projectDeclare_create_at, 
              pd.projectDeclare_draftEnable, pd.project_name
       FROM ProjectDeclare pd
       WHERE pd.projectDeclare_user = ?`, [userId]
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

    // 返回带有优化状态的项目列表
    res.status(200).json({ success: true, projects: projectsWithOptimizationStatus });
  } catch (error) {
    console.error('获取我的项目列表失败:', error);
    res.status(500).json({ success: false, message: '获取我的项目列表失败，请稍后再试！' });
  } finally {
    connection.release(); // 释放数据库连接
  }
};

// 获取项目详细信息
const getProjectDetails = async (req, res) => {
  const projectId = req.params.id; // 从请求参数中获取项目 ID

  if (!projectId) {
    return res.status(400).json({ success: false, message: "项目 ID 是必需的" });
  }

  const query = `
        SELECT 
            p.projectDeclare_id AS project_id,
            p.project_name,
            p.projectDeclare_create_at AS project_create_at,
            u.user_name AS project_creator_name,
            pdfva.projectDeclareField_id AS field_id,
            pdf.projectDeclareField_value AS field_value,
            tf.templateFields_name AS field_name,
            tf.templateFields_type AS field_type,
            pdfva.optimize_frequency,
            por.specialist_user_id AS last_specialist_id,
            last_specialist.user_name AS last_specialist_name,
            por.projectOptimize_create_at AS last_optimize_time
        FROM 
            ProjectDeclare p
        LEFT JOIN 
            User u ON p.projectDeclare_user = u.user_id
        LEFT JOIN 
            ProjectDeclareFieldValueAssociation pdfva ON p.projectDeclare_id = pdfva.projectDeclare_id
        LEFT JOIN 
            ProjectDeclareField pdf ON pdfva.projectDeclareField_id = pdf.projectDeclareField_id
        LEFT JOIN 
            TemplateFields tf ON pdf.templateFields_id = tf.templateFields_id
        LEFT JOIN 
            ProjectOptimizeRrecord por ON pdfva.projectOptimizeRrecord_id = por.projectOptimizeRrecord_id
        LEFT JOIN 
            User last_specialist ON por.specialist_user_id = last_specialist.user_id
        WHERE 
            p.projectDeclare_id = ?
            AND pdfva.optimize_frequency = (
                SELECT MAX(optimize_frequency) 
                FROM ProjectDeclareFieldValueAssociation 
                WHERE projectDeclare_id = p.projectDeclare_id
            );
    `;

  let connection;
  try {
    connection = await db.getConnection();
    const [rows] = await connection.query(query, [projectId]);

    if (rows.length === 0) {
      return res.status(404).json({ success: false, message: "项目未找到" });
    }

    // 提取公共项目信息
    const projectDetails = {
      project_id: rows[0].project_id,
      project_name: rows[0].project_name,
      project_create_at: rows[0].project_create_at,
      project_creator_name: rows[0].project_creator_name,
    };

    // 提取字段信息
    const fields = rows.map((row) => ({
      field_id: row.field_id,
      field_value: row.field_value,
      field_name: row.field_name,
      field_type: row.field_type,
      optimize_frequency: row.optimize_frequency,
      last_specialist_id: row.last_specialist_id,
      last_specialist_name: row.last_specialist_name,
      last_optimize_time: row.last_optimize_time,
    }));

    return res.status(200).json({
      success: true,
      data: {
        project: projectDetails,
        fields: fields,
      },
    });
  } catch (error) {
    console.error("获取项目详细信息出错:", error);
    return res.status(500).json({ success: false, message: "服务器内部错误" });
  } finally {
    connection.release();
  }
};

module.exports = {
  submitProjectDeclare,
  getAllProjects,
  getMyProjects,
  getProjectDetails
};
