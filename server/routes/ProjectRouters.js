const express = require('express');
const ProjectController = require('../controllers/ProjectController.js'); // 引入控制器
const router = express.Router();

// 路由：提交项目申报
router.post('/project/submitProjectDeclare', ProjectController.submitProjectDeclare);
router.get('/project/getAllProjects', ProjectController.getAllProjects);

module.exports = router;
