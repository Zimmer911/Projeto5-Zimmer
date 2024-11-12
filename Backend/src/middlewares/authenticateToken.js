const jwt = require("jsonwebtoken");

function authenticateToken(req, res, next) {
  const token = req.headers["authorization"]?.split(" ")[1];
  if (!token) {
    return res.status(401).json({
      msg: "Você precisa se autenticar para acessar essa página",
    });
  }

  jwt.verify(token, process.env.SECRET, (err, decoded) => {
    if (err) {
      return res.status(403).json({
        msg: "Sua sessão expirou, por favor faça login novamente"
      });
    }

    // Armazenar userId  na requisição
    req.userId  = decoded.userId ; // Usa userId  do token decodificado
    next();
  });
}

module.exports = authenticateToken;