const { DataTypes } = require("sequelize");
const sequelize = require("../config/config");

const Comentario = sequelize.define('comentario', {
    nome: {
        type: DataTypes.STRING,
        allowNull: false
    },
    descricao: {
        type: DataTypes.STRING,
        allowNull: false
    },
    nota: {
        type: DataTypes.INTEGER,
        allowNull: false
    }
});

module.exports = Comentario;