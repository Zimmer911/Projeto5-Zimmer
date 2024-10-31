require('dotenv').config();
const express = require("express");
const router = require("./router/router");
const sequelize = require("./config/config");
const path = require('path');

const Comentario = require("./models/Comentario");
const User = require("./models/User");
const Publicacao = require("./models/Publicacao");

var cors = require('cors');

const app = express();

app.use(cors());

app.use((req, res, next) => {
  res.header("Access-Control-Allow-Origin", "*");
  res.header("Access-Control-Allow-Methods", 'GET,PUT,POST,DELETE');
  res.header("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");
  next();
});

// Configuração para servir arquivos estáticos da pasta 'uploads'
app.use('/uploads', express.static(path.join(__dirname, 'uploads')));

// Modelo da API JSON
app.use(express.json());
app.use(express.urlencoded({ extended: true }));

app.use("/api", router);

app.get("/healthcheck", (req, res) => {
  // 200 significa que está ok o servidor
  return res.status(200).json({
    msg: "Estamos vivos",
    alive: true,
  });
});

// Rota para lidar com rotas não encontradas
app.use((req, res) => {
  res.status(404).json({ message: "Rota não encontrada" });
});

// Middleware de tratamento de erros
app.use((err, req, res, next) => {
  console.error(err.stack);
  res.status(500).json({ message: "Erro interno do servidor" });
});

const PORT = process.env.PORT || 8080;

sequelize
  .authenticate()
  .then(async () => {
    console.log("Conexão estabelecida com sucesso");
    await sequelize.sync(); // Sincroniza o código com a tabela
  })
  .then(() => {
    app.listen(PORT, () => {
      console.log(`Servidor rodando na porta ${PORT}`);
    });
  })
  .catch((error) => { 
    console.error("Erro ao se conectar com o banco", error);
  });

module.exports = app; // Exporta o app para possíveis testes