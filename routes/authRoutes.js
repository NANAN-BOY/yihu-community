const express = require('express');
const router = express.Router();
const authController = require('../controllers/authController');

router.post('/login', authController.login);
router.post('/login', authController.login);
router.get('/restore-login', authController.restoreLogin);
router.post('/register',authController.register);

module.exports = router;
