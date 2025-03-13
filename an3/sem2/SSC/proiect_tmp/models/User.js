class User {
  constructor({ id, name, email, password, role }) {
    this.id = id;
    this.name = name;
    this.email = email;
    this.password = password;
    this.role = role || 'user';
    this.createdAt = new Date();
  }
}

module.exports = User;