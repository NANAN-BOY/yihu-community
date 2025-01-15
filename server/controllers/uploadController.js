const multer = require('multer');
const path = require('path');
const authenticateToken = require('../middlewares/authMiddleware.js');

// 上传文件的存储路径和文件名
const storage = multer.diskStorage({
  destination: (req, file, cb) => {
    cb(null, 'uploads/');
  },
  filename: (req, file, cb) => {
    const userId = req.user.id;
    const timestamp = Date.now();
    const fileExt = path.extname(file.originalname);
    const fileName = `${timestamp}-${userId}${fileExt}`;
    cb(null, fileName);
  }
});

const upload = multer({
  storage: storage,
  fileFilter: (req, file, cb) => {
    const allowedTypes = /jpeg|jpg|png|gif|mp4|avi|mkv/;
    const mimeType = allowedTypes.test(file.mimetype);
    const extName = allowedTypes.test(path.extname(file.originalname).toLowerCase());
    if (mimeType && extName) return cb(null, true);
    cb(new Error('Invalid file type. Only JPEG, PNG, GIF images and MP4, AVI, MKV videos are allowed.'));
  },
  limits: { fileSize: 1024 * 1024 * 1024 }
});

// 上传文件路由
const uploadFile = (req, res) => {
  upload.single('file')(req, res, async (err) => {
    if (err) return res.status(400).json({ error: err.message });
    if (!req.file) return res.status(400).json({ error: 'No file uploaded.' });
    
    const album_id = req.body.album_id;
    const photo_Url = `/uploads/${req.file.filename}`;
    
    const sql = 'INSERT INTO photo_list (album_id, user_id, photo_url) VALUES (?, ?, ?)';
    try {
      await req.db.query(sql, [album_id, req.user.id, photo_Url]);
      res.json({ message: '文件上传成功', photo_url: photo_Url });
    } catch (err) {
      console.error('Database error:', err);
      res.status(500).json({ error: '数据库错误' });
    }
  });
};

module.exports = { uploadFile };
