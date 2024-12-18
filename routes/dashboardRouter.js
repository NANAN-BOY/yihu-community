var express = require('express');
var router = express.Router();

/* user 仪表盘页面. */
router.get('/userdashboard', function(req, res, next) {
  res.render('index', { title: '智能管理平台' });
});

module.exports = router;
