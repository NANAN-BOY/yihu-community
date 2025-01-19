const express = require('express');
const router = express.Router();
const inviteController = require('../controllers/ExpertLibraryController');

// 路由：邀请专家
router.post('/ExpertLibrary/invite-expert', inviteController.inviteExpert);

module.exports = router;