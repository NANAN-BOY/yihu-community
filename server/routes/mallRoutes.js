const express = require('express');
const router = express.Router();
const mallController = require('../controllers/mallController'); 
const authenticateToken = require('../middlewares/authMiddleware');

router.get('/mall/getitem/:id', mallController.getItemDetails);
router.get('/mall/recommended-items', mallController.getRecommendedItems);
router.get('/mall/itemphoto/:itemphotoname', mallController.getItemsphoto);
router.get('/mall/getCategories', mallController.getCategories);
router.get('/mall/items', mallController.getItemsByCategory);

module.exports = router;
