const express = require('express');
const router = express.Router();
const auth = require('../middleware/auth');
const resourceController = require('../controllers/resourceController');

// @route   GET api/resources
// @desc    Get all resources (protected endpoint)
// @access  Private
router.get('/', auth, resourceController.getAllResources);

// @route   GET api/resources/:id
// @desc    Get resource by ID (protected endpoint)
// @access  Private
router.get('/:id', auth, resourceController.getResourceById);

module.exports = router;