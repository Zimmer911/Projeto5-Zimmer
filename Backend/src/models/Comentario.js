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
    },
    postId: { // Adicionando postId para associar o comentário ao post
        type: DataTypes.INTEGER,
        allowNull: false,
        references: {
            model: 'publicacao', // Nome da tabela de publicações
            key: 'id' // Chave primária da tabela de publicações
        }
    }
});

module.exports = Comentario;