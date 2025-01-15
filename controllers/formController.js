// formController.js
const pool = require('../src/db/db'); // 引入数据库连接池
const db = require('../src/db/db');  // 从 db/db.js 导入连接池模块


// 上传表单并保存数据到数据库
const uploadForm = async ({ form_name, form_description }, fields) => {
  try {
      // 插入表单
      const [formResult] = await db.execute(
          'INSERT INTO forms (form_name, form_description) VALUES (?, ?)',
          [form_name, form_description]
      );

      const formId = formResult.insertId;

      // 插入表单字段，确保所有字段都被验证过
      for (let field of fields) {
          const { fieldName, fieldType, isRequired, options } = field;

          // 验证字段数据，确保不包含 undefined
          console.log('fieldName:', fieldName);

          console.log('fieldType:', fieldType);
          console.log('fieldIsRequired:', isRequired);
          const fieldOptions = options ? JSON.stringify(options) : null;  // 如果选项为空，则传递 null
            console.log('fieldOptions:', fieldOptions);
          await db.execute(
              'INSERT INTO form_fields (forms_id, form_id, form_fields_name, form_fields_type, isRequired, form_fields_options) VALUES (?, ?, ?, ?, ?, ?)',
              [formId, formId, fieldName, fieldType, isRequired, fieldOptions]
          );
      }

      return formId;
  } catch (error) {
      console.error('Error uploading form:', error);
      throw error;
  }
};

// 根据字段类型转换为整数
function getFieldTypeInt(fieldType) {
  switch (fieldType) {
      case 'text':
          return 1;  // 对应文本类型
      case 'textarea':
          return 2;  // 对应文本区域类型
      case 'select':
          return 3;  // 对应下拉选择类型
      case 'file':
          return 4;  // 对应文件上传类型
      default:
          return 1;  // 默认返回文本类型
  }
}

module.exports = { uploadForm };
