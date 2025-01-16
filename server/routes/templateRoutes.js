const express = require('express');
const router = express.Router();
const templateController = require('../controllers/templateController');
const authenticateToken = require('../middlewares/authMiddleware');

router.post('/template/createProjectTemplate', templateController.createProjectTemplate);


module.exports = router;
