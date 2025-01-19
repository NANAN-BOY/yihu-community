const express = require('express');
const router = express.Router();
const inviteController = require('../controllers/ExpertLibraryController');

// 路由：邀请专家
router.post('/ExpertLibrary/invite-expert', inviteController.inviteExpert);
router.get('/ExpertLibrary/invite/:inviteId', inviteController.getInviteInfo);
module.exports = router;