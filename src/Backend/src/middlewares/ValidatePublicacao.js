const validatePublication = (req, res, next) => {
  const { nome, descricao, nota } = req.body;

  if (!nome || !descricao || !nota) {
    return res.status(400).json({
      msg: "Campos inválidos",
    });
  }

  return next();
};

const validatePublicationId = (req, res, next) => {
  const { id } = req.params;

  if (!id) {
    return res.status(400).json({
      msg: "Parâmetro faltando",
    });
  }

  return next();
};

module.exports = { validatePublication, validatePublicationId };
  
