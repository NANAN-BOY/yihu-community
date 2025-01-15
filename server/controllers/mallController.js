const path = require('path');
const fs = require('fs');

// 获取单个商品详情
const getItemDetails = async (req, res) => {
  const itemId = req.params.id;
  const sql = `SELECT item_id, item_title, item_description, item_photos, points_required, stock_quantity, item_creation_date, commodity_price, discounted_price FROM StoreItems WHERE item_id = ?`;

  try {
    const [results] = await req.db.query(sql, [itemId]);
    if (results.length === 0) {
      return res.status(404).json({ error: '商品未找到' });
    }
    res.status(200).json(results[0]);
  } catch (err) {
    console.error('Database query error:', err);
    res.status(500).json({ error: '数据库错误' });
  }
};

// 获取推荐商品列表
const getRecommendedItems = async (req, res) => {
  const sql = `SELECT item_id, item_title, item_photos, points_required FROM StoreItems LIMIT 4`;

  try {
    const [results] = await req.db.query(sql);
    res.status(200).json(results); // 返回前四个商品
  } catch (err) {
    console.error('Database query error:', err);
    res.status(500).json({ error: '数据库错误' });
  }
};

// 获取商品图片
const getItemsphoto = async (req, res) => {
  const ItemsphotoPath = path.join(__dirname, '../uploads/mall', `${req.params.itemphotoname}.png`);

  fs.access(ItemsphotoPath, fs.constants.F_OK, (err) => {
    if (err) {
      return res.status(404).json({ error: '商品图片未找到' });
    }
    res.sendFile(ItemsphotoPath, (err) => {
      if (err) {
        return res.status(500).json({ error: '商品图片下载失败' });
      }
    });
  });
};

// 获取商品分类列表
const getCategories = async (req, res) => {
  const sql = `SELECT storeitems_categories_id AS id, storeitems_categories_name AS name FROM storeitems_categories`;

  try {
    const [results] = await req.db.query(sql);
    if (results.length === 0) {
      return res.status(404).json({ error: '未找到任何商品分类' });
    }
    res.status(200).json(results);
  } catch (err) {
    console.error('Database query error:', err);
    res.status(500).json({ error: '数据库错误' });
  }
};

// 获取商品列表，支持按分类筛选
const getItemsByCategory = async (req, res) => {
  const categoryId = req.query.categoryId || null;  // 获取请求中的分类ID（如果没有传入，默认为null）
  console.log(categoryId);
  let sql;
  const params = [];

  if (categoryId) {
    // 如果分类ID存在，查询特定分类的商品
    sql = `SELECT * FROM StoreItems WHERE item_categories  = ?`;
    params.push(categoryId);
  } else {
    // 如果没有传入分类ID，查询所有商品
    sql = `SELECT item_id, item_title, item_description, item_photos, points_required, stock_quantity, item_creation_date, commodity_price, discounted_price 
           FROM StoreItems`;
  }

  try {
    const [results] = await req.db.query(sql, params);
    if (results.length === 0) {
      return res.status(404).json({ error: '没有找到商品' });
    }
    res.status(200).json(results);
  } catch (err) {
    console.error('Database query error:', err);
    res.status(500).json({ error: '数据库错误' });
  }
};

module.exports = { getItemDetails,
                   getRecommendedItems,
                   getItemsphoto ,
                   getCategories,
                   getItemsByCategory };
