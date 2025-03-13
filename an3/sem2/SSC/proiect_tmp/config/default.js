module.exports = {
  jwtSecret: process.env.JWT_SECRET || 'your_default_jwt_secret',
  oauthSecret: process.env.OAUTH_SECRET || 'your_default_oauth_secret',
  port: process.env.PORT || 3000,
  oauth: {
    clientId: 'example-client-id',
    clientSecret: 'example-client-secret',
    redirectUris: ['http://localhost:3000/callback']
  }
};