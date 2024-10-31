
const validateComentario = (req,res,next) => {
    const { nome,descricao,nota} =req.body;

    if(!nome || !descricao || !nota) {
        return res.status(400).json({
            msg:"Campos Invalidos",
        });
    }

    return next();
};

const validateComentarioId = (req,res,next) => {
    const {id} = req.params;

    if(!id) {
        return res.status(400).json({
            msg:"Parametro faltando",
        });
    }

    return next();
};

module.exports = {validateComentario,validateComentarioId};