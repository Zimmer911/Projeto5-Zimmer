// src/models/User.js
const { DataTypes } = require("sequelize");
const sequelize = require("../config/config");

const User = sequelize.define('user', {
    nome: {
        type: DataTypes.STRING,
        allowNull: false,
        validate: {
            notEmpty: true
        }
    },
    email: {
        type: DataTypes.STRING,
        allowNull: false,
        unique: true,
        validate: {
            isEmail: true
        }
    },
    senha: {
        type: DataTypes.STRING(88), // Aumentado para acomodar o hash+salt em Base64
        allowNull: false,
        validate: {
            notEmpty: true
        }
    }
}, {
    timestamps: true // Adiciona created_at e updated_at
});

module.exports = User;