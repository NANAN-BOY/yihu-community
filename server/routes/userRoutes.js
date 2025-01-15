const express = require('express');
const router = express.Router();
const userController = require('../controllers/userController');
const authenticateToken = require('../middlewares/authMiddleware');

router.get('/user/:id', authenticateToken, userController.getUser);
// 设置头像下载路由
router.get('/avatar/:hashedUserId', userController.getAvatar);

module.exports = router;
