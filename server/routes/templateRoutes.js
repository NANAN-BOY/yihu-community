const express = require('express');
const router = express.Router();
const templateController = require('../controllers/templateController');
const authenticateToken = require('../middlewares/authMiddleware');

router.post('/template/createProjectTemplate', templateController.createProjectTemplate);
router.get('/template/getProjectTemplateList', templateController.getProjectTemplateList);
//获取项目模板列表
router.post('/template/enableProjectTemplatet', templateController.enableProjectTemplate);
//获取当前启用的项目填报模板
router.get('/template/getEnableProjectTemplate', templateController.getEnableProjectTemplate);

module.exports = router;
