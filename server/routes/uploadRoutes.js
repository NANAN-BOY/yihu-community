const express = require('express');
const router = express.Router();
const uploadController = require('../controllers/uploadController');
const authenticateToken = require('../middlewares/authMiddleware');

router.post('/upload', authenticateToken, uploadController.uploadFile);

module.exports = router;
