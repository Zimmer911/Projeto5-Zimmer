const { Sequelize } = require("sequelize");

const sequelize = new Sequelize('ludis','root','root',{
    host: 'localhost',
    dialect: 'mysql'
});

module.exports = sequelize;