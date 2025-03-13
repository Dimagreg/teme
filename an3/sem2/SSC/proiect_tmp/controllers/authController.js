const bcrypt = require('bcryptjs');
const User = require('../models/User');
const tokenService = require('../services/tokenService');

// Mock database for demo purposes
const users = [];

exports.register = async (req, res) => {
  try {
    const { name, email, password } = req.body;
    
    // Check if user already exists
    const userExists = users.find(user => user.email === email);
    if (userExists) {
      return res.status(400).json({ message: 'User already exists' });
    }
    
    // Create new user
    const salt = await bcrypt.genSalt(10);
    const hashedPassword = await bcrypt.hash(password, salt);
    
    const newUser = new User({
      id: users.length + 1,
      name,
      email,
      password: hashedPassword,
      role: 'user'
    });
    
    users.push(newUser);
    
    // Generate JWT token
    const token = tokenService.generateJWT(newUser);
    
    res.status(201).json({ token });
  } catch (err) {
    console.error(err);
    res.status(500).json({ message: 'Server error' });
  }
};

exports.login = async (req, res) => {
  try {
    const { email, password } = req.body;
    
    // Check if user exists
    const user = users.find(user => user.email === email);
    if (!user) {
      return res.status(400).json({ message: 'Invalid credentials' });
    }
    
    // Check password
    const isMatch = await bcrypt.compare(password, user.password);
    if (!isMatch) {
      return res.status(400).json({ message: 'Invalid credentials' });
    }
    
    // Generate JWT token
    const token = tokenService.generateJWT(user);
    
    res.json({ token });
  } catch (err) {
    console.error(err);
    res.status(500).json({ message: 'Server error' });
  }
};

exports.getCurrentUser = async (req, res) => {
  try {
    const user = users.find(user => user.id === req.user.id);
    if (!user) {
      return res.status(404).json({ message: 'User not found' });
    }
    
    // Don't send password
    const { password, ...userData } = user;
    
    res.json(userData);
  } catch (err) {
    console.error(err);
    res.status(500).json({ message: 'Server error' });
  }
};

exports.authorizeOAuth = (req, res) => {
  // OAuth 2.0 authorization endpoint implementation
  const { client_id, redirect_uri, response_type, scope, state } = req.query;
  
  // For demo purposes, return a simple authorization code
  const authCode = Math.random().toString(36).substring(2, 15);
  
  res.redirect(`${redirect_uri}?code=${authCode}&state=${state}`);
};

exports.generateOAuthToken = (req, res) => {
  // OAuth 2.0 token endpoint implementation
  const { grant_type, code, redirect_uri, client_id, client_secret } = req.body;
  
  // For demo purposes, return a simple access token
  const accessToken = tokenService.generateOAuthToken(client_id);
  
  res.json({
    access_token: accessToken,
    token_type: 'Bearer',
    expires_in: 3600,
    refresh_token: Math.random().toString(36).substring(2, 15)
  });
};