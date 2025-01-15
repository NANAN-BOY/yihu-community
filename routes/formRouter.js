// routes/formRouter.js
var express = require('express');
var router = express.Router();
const db = require('../src/db/db');  // 从 db/db.js 导入 Promise 版本的连接池模块

const { uploadForm } = require('../controllers/formController');

// 渲染表单创建页面
router.get('/create-form', (req, res) => {
    res.render('create-form');
});

// 上传表单接口
router.post('/upload-form', async (req, res) => {
  try {
    console.log('Received form data:', req.body);
    const { form_name, form_description, fields } = req.body;

    // 上传表单并保存到数据库
    const formId = await uploadForm({ form_name, form_description }, fields);
    
    // 返回上传成功信息
    res.status(201).json({
      message: 'Form uploaded successfully',
      formId: formId
    });
  } catch (error) {
    console.error('Error uploading form:', error);
    res.status(500).json({ message: 'Error uploading form' });
  }
});

// 获取表单信息的路由
router.get('/get-form/:formId', async (req, res) => {
  const formId = req.params.formId;

  try {
    // 查询表单基本信息（如 form_name 和 form_description）
    const [formDetails] = await db.query('SELECT * FROM forms WHERE forms_id = ?', [formId]);

    if (formDetails.length === 0) {
        return res.status(404).json({ message: '表单未找到' });
    }

    // 查询字段信息，使用正确的外键 `forms_id`
    const [fields] = await db.query('SELECT * FROM form_fields WHERE forms_id = ?', [formId]);

    // 返回表单基本信息和字段信息
    res.status(200).json({
        form: formDetails[0],
        fields: fields
    });
  } catch (error) {
    console.error('Error fetching form:', error);
    res.status(500).json({ message: '获取表单失败' });
  }
});
// routes/formRouter.js
router.get('/fill-form/:formId', async (req, res) => {
  const formId = req.params.formId;

  try {
    // 查询表单基本信息
    const [formDetails] = await db.query('SELECT * FROM forms WHERE forms_id = ?', [formId]);

    if (formDetails.length === 0) {
      return res.status(404).json({ message: '表单未找到' });
    }

    // 查询字段信息
    const [fields] = await db.query('SELECT * FROM form_fields WHERE forms_id = ?', [formId]);
    console.log(fields);
    // 渲染填报页面，并将数据传递给模板
    res.render('fill-form', {
      form: formDetails[0],  // 表单基本信息
      fields: fields         // 表单字段
    });
  } catch (error) {
    console.error('Error fetching form:', error);
    res.status(500).json({ message: '获取表单失败' });
  }
});

module.exports = router;
