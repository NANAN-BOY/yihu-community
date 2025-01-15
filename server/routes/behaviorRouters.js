const express = require('express');
const router = express.Router();
const multer = require('multer');
const path = require('path');
const BehaviorController = require('../controllers/behaviorController'); 

// 设置存储位置和文件命名
const storage = multer.diskStorage({
  destination: function (req, file, cb) {
    cb(null, 'uploads/behavior'); // 存储文件的文件夹
  },
  filename: function (req, file, cb) {
    const ext = path.extname(file.originalname); // 获取文件扩展名
    cb(null, Date.now() + ext); // 使用时间戳作为文件名
  },
});

// 创建 multer 实例
const upload = multer({ storage: storage });

// 定义路由
router.get('/behavior-categories', BehaviorController.getBehaviorCategories);
router.post('/behavior', upload.array('files'), BehaviorController.createBehaviorRecord);
// 获取行为文件的路由
router.get('/getbehaviorfile/:filename', BehaviorController.getBehaviorFile);
router.get('/getMybehaviorList',BehaviorController.getMyBehaviorList);
// 添加删除行为记录的路由
router.delete('/deleteBehavior/:record_id',  BehaviorController.deleteBehaviorRecord);



module.exports = router;

