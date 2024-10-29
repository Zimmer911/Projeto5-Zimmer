require('dotenv').config();
const express = require("express");
const router = require("./router/router");
const sequelize = require("./config/config");
const path = require('path');

const Comentario = require("./models/Comentario");
const User = require("./models/User");
const Publicacao = require("./models/Publicacao");

const cors = require('cors');

const app = express();

app.use(cors());

app.use((req, res, next) => {
  res.header("Access-Control-Allow-Origin", "*");
  res.header("Access-Control-Allow-Methods", 'GET,PUT,POST,DELETE');
  next();
});

// Modelo da API JSON
app.use(express.json());
app.use(express.urlencoded({ extended: true }));

// Servir arquivos estáticos da pasta uploads
app.use('/uploads', express.static(path.join(__dirname, '..', 'uploads')));

app.use("/api", router);

app.get("/healthcheck", (req, res) => {
  // 200 significa que está ok o servidor
  return res.status(200).json({
    msg: "Estamos vivos",
    alive: true,
  });
});

sequelize
  .authenticate()
  .then(async () => {
    console.log("Conexão estabelecida com sucesso");
    await sequelize.sync(); // Sincroniza o código com a tabela
  })
  .then(() => {
    const port = process.env.PORT || 8080;
    app.listen(port, () => {
      console.log(`Servidor rodando na porta ${port}`);
    });
  })
  .catch((error) => { 
    console.error("Erro ao se conectar com o banco", error);
  });

// Tratamento de erros global
app.use((err, req, res, next) => {
  console.error(err.stack);
  res.status(500).send('Algo deu errado!');
});

// Rota para lidar com rotas não encontradas
app.use((req, res, next) => {
  res.status(404).send("Desculpe, não conseguimos encontrar essa página!");
});

module.exports = app;