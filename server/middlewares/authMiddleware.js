const jwt = require('jsonwebtoken');
const JWT_SECRET = 'QwasErdfTyghXcvsEdgtFs';

function authenticateToken(req, res, next) {
  const authHeader = req.headers['authorization'];
  const token = authHeader && authHeader.split(' ')[1];
  if (!token) {
    return res.status(401).json({ error: '身份验证不完全' });
  }
  jwt.verify(token, JWT_SECRET, (err, user) => {
    if (err) {
      return res.status(403).json({ error: '身份已过期' });
    }
    req.user = user;
    next();
  });
}

module.exports = authenticateToken;
