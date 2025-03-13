const express = require('express');
const router = express.Router();
const authController = require('../controllers/authController');
const auth = require('../middleware/auth');

// @route   POST api/auth/register
// @desc    Register a new user
// @access  Public
router.post('/register', authController.register);

// @route   POST api/auth/login
// @desc    Authenticate user & get JWT token
// @access  Public
router.post('/login', authController.login);

// @route   GET api/auth/me
// @desc    Get current user's profile
// @access  Private
router.get('/me', auth, authController.getCurrentUser);

// OAuth 2.0 endpoints
// @route   GET api/auth/oauth/authorize
// @desc    OAuth 2.0 authorization endpoint
// @access  Public
router.get('/oauth/authorize', authController.authorizeOAuth);

// @route   POST api/auth/oauth/token
// @desc    OAuth 2.0 token endpoint
// @access  Public
router.post('/oauth/token', authController.generateOAuthToken);

module.exports = router;