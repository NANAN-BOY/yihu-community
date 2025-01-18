const path = require('path');
const fs = require('fs');
const {NEWDATE} = require("mysql/lib/protocol/constants/types");
const db = require('../db/pool');

const createProjectTemplate = async (req, res) => {
    try {
        const { template_name, template_description, template_create_user, TemplateFields } = req.body;
        // 保存模板到数据库
        const [templateResult] = await db.execute(
            'INSERT INTO Template (template_name, template_description, template_enable, template_type, template_create_user, template_create_at, templateArchive_enable) VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP, ?)',
            [template_name, template_description, 0, 1, template_create_user, 0]
        );
        // 获取插入后的模板 ID
        const template_Id = templateResult.insertId;
        // 插入表单字段
        for (let TemplateField of TemplateFields) {
            const { templateFields_name, templateFields_type, templateFields_isRequired, options } = TemplateField;
            const fieldOptions = options ? JSON.stringify(options) : null;  // 如果选项为空，则传递 null

            // 插入字段数据到数据库
            await db.execute(
                'INSERT INTO TemplateFields (template_id, templateFields_name, templateFields_type, templateFields_isRequired, templateFields_options) VALUES (?, ?, ?, ?, ?)',
                [template_Id, templateFields_name, templateFields_type, templateFields_isRequired, fieldOptions]
            );
        }
        // 返回成功的响应，包含模板 ID
        res.status(201).json({ message: 'Form uploaded successfully', templateId: template_Id });
    } catch (error) {
        console.error('Error uploading form:', error);
        res.status(500).json({ message: 'Error uploading form', error: error.message });
    }
};
const getProjectTemplateList = async (req, res) => {
    try {
        // 查询模板列表，不包含字段数据
        const [templates] = await db.execute(
            'SELECT template_id, template_name, template_description, template_enable, template_type, template_create_user, template_create_at, templateArchive_enable FROM Template'
        );

        // 返回模板列表
        res.status(200).json({ templates });
    } catch (error) {
        console.error('Error fetching templates:', error);
        res.status(500).json({ message: 'Error fetching templates', error: error.message });
    }
};
const enableProjectTemplate = async (req, res) => {
    try {
        const { template_id } = req.body;
        // 检查模板是否存在
        const [templateExists] = await db.execute('SELECT template_id FROM Template WHERE template_id = ?', [template_id]);
        if (templateExists.length === 0) {return res.status(404).json({ message: 'Template not found' });}
        // 查询当前启用的模板，并且是模板类型为 1 的模板
        const [enabledTemplates] = await db.execute('SELECT template_id FROM Template WHERE template_enable = 1 AND template_type = 1');
        if (enabledTemplates.length > 0) {
            // 如果当前已经有启用的模板，先禁用它
            await db.execute('UPDATE Template SET template_enable = 0 WHERE template_enable = 1 AND template_type = 1');
        }
        // 启用指定的模板
        const [result] = await db.execute(
            'UPDATE Template SET template_enable = 1 WHERE template_id = ?',
            [template_id]
        );
        // 返回成功的响应
        res.status(200).json({ message: 'Template enabled successfully' });
    } catch (error) {
        console.error('Error enabling template:', error);
        res.status(500).json({ message: 'Error enabling template', error: error.message });
    }
};
module.exports = { createProjectTemplate,
    getProjectTemplateList
,enableProjectTemplate};
