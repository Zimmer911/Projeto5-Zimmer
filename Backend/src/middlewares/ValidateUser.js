// src/middlewares/ValidateUser.js
const validateUser = (req, res, next) => {
  const { nome, email, senha } = req.body;

  // Validação do nome
  if (!nome || nome.trim().length < 2) {
      return res.status(400).json({
          msg: "Nome inválido. Deve ter pelo menos 2 caracteres"
      });
  }

  // Validação do email
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  if (!email || !emailRegex.test(email)) {
      return res.status(400).json({
          msg: "Email inválido"
      });
  }

  // Validação da senha (apenas verifica se existe, pois já deve vir criptografada)
  if (!senha) {
      return res.status(400).json({
          msg: "Senha é obrigatória"
      });
  }

  return next();
};

const validateUserId = (req, res, next) => {
  const { id } = req.params;

  if (!id || isNaN(id)) {
      return res.status(400).json({
          msg: "ID inválido"
      });
  }

  return next();
};

module.exports = { validateUser, validateUserId };