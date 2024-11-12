const { DataTypes } = require("sequelize");
const sequelize = require("../config/config");

const Publicacao = sequelize.define('publicacao', {
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
    imagem: {
        type: DataTypes.STRING,
        allowNull: true
    },

    userId: {
        type: Sequelize.INTEGER,
        allowNull: false,
        references: {
            model: 'Users', // ou o nome da tabela de usuários
            key: 'id'
        }
    }

    
});

module.exports = Publicacao;