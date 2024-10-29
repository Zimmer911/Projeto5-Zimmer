//Nome , email e senha

const validateUser = (req, res, next) => {
    const { nome, email, senha } = req.body;
    console.log('Valor do campo "nome":', nome);

    if (!nome) {
      return res.status(400).json({
        msg: "O campo 'nome' é obrigatório",
      });
    }
  
    if (!email) {
      return res.status(400).json({
        msg: "O campo 'email' é obrigatório",
      });
    }
  
    if (!senha) {
      return res.status(400).json({
        msg: "O campo 'senha' é obrigatório",
      });
    }
  
    return next();
  };

const validateUserId = (req,res,next) => {
    const {id} = req.params;

    if(!id) {
        return res.status(400).json({
            msg:"Parametro faltando",
        });
    }

    return next();
};

module.exports = {validateUser,validateUserId};