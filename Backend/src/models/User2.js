const { DataTypes } = require("sequelize");
const sequelize = require("../config/config");

const User2 = sequelize.define('user2', {
    nome: {
        type: DataTypes.STRING,
        allowNull: false
    },
    email: {
        type: DataTypes.STRING,
        allowNull: false
    },
    senha: {
        type: DataTypes.STRING,
        allowNull: false
    }
});

module.exports = User2;