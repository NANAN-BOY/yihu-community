const express = require('express');
const router = express.Router();
const userController = require('../controllers/userController');

// 定义路由
router.get('/users/list', userController.getUserListByRoleAndStatus);
router.get('/users/getUserInfo/:id', userController.getUserById);

module.exports = router;
