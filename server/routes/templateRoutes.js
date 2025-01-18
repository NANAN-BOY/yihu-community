const express = require('express');
const router = express.Router();
const templateController = require('../controllers/templateController');
const authenticateToken = require('../middlewares/authMiddleware');

router.post('/template/createProjectTemplate', templateController.createProjectTemplate);
router.get('/template/getProjectTemplateList', templateController.getProjectTemplateList);
router.post('/template/enableProjectTemplatet', templateController.enableProjectTemplate);

module.exports = router;
