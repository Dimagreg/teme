const jwt = require('jsonwebtoken');
const config = require('../config/default');

exports.generateJWT = (user) => {
  const payload = {
    user: {
      id: user.id,
      role: user.role
    }
  };
  
  return jwt.sign(payload, config.jwtSecret, { expiresIn: '1h' });
};

exports.generateOAuthToken = (clientId) => {
  const payload = {
    client_id: clientId,
    scope: 'read write',
    exp: Math.floor(Date.now() / 1000) + 3600
  };
  
  return jwt.sign(payload, config.oauthSecret);
};

exports.verifyJWT = (token) => {
  return jwt.verify(token, config.jwtSecret);
};