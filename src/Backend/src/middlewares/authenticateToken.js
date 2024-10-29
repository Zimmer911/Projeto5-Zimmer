const jwt = require("jsonwebtoken");

function authenticateToken(req, res, next) {
  const token = req.headers["authorization"]?.split(" ")[1];
  if (!token) {
    return res.status(401).json({
      msg: "Você precisa se autenticar para acessar essa página",
    });
  }

  jwt.verify(token, process.env.SECRET, (err, user) => {
    if (err) {
      return res.status(403).json({
        msg: "Sua sessão expirou, por favor faça login novamente"
      });
    }

    //Armazenar usuario na requisição
    req.user = user;
    next();
  });
}

module.exports = authenticateToken;