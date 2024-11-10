const Comentario = require("../models/Comentario");

const ComentarioController = {
  create: async (req, res) => {
    try {
      const { nome, descricao, nota, postId } = req.body; // Incluindo postId

      const comentarioCriado = await Comentario.create({ nome, descricao, nota, postId }); // Adicionando postId na criação

      return res.status(200).json({
        msg: "Comentario criado com sucesso!",
        comentario: comentarioCriado, // Corrigido para 'comentario'
      });
    } catch (error) {
      console.error(error);
      return res.status(500).json({ msg: "Acione o Suporte" });
    }
  },
  update: async (req, res) => {
    try {
      const { id } = req.params;
      const { nome, descricao, nota } = req.body;

      console.log({ id });
      console.log({ nome, descricao, nota });

      const comentarioUpdate = await Comentario.findByPk(id);

      if (comentarioUpdate == null) {
        return res.status(404).json({
          msg: "Comentario nao encontrado",
        });
      }

      const updated = await comentarioUpdate.update({
        nome, 
        descricao, 
        nota
      });
      if (updated) {
        return res.status(200).json({
          msg: "Comentario atualizado com sucesso!",
        });
      }
      return res.status(500).json({
        msg:"Erro ao atualizar comentario"
      });
    } catch (error) {
      console.error(error);
      return res.status(500).json({ msg: "Acione o Suporte" });
    }
  },
  getAll: async (req, res) => {
    try {
      const comentarios = await Comentario.findAll();
      return res.status(200).json({
        msg: "Comentarios Encontrados!",
        comentarios,
      });
    } catch (error) {
      console.error(error);
      return res.status(500).json({ msg: "Acione o Suporte" });
    }
  },
  getOne: async (req, res) => {
    try {
      const { id } = req.params;

      const comentarioEncontrado = await Comentario.findByPk(id);

      if (comentarioEncontrado == null) {
        return res.status(404).json({
          msg: "Comentario nao encontrado!",
        });
      }
      return res.status(200).json({
        msg: "Comentario Encontrado",
        usuario: comentarioEncontrado,
      });
    } catch (error) {
      console.error(error);
      return res.status(500).json({ msg: "Acione o Suporte" });
    }
  },
  delete: async (req, res) => {
    try {
      const { id } = req.params;

      const comentarioFinded = await Comentario.findByPk(id);

      if (comentarioFinded == null) {
        return res.status(404).json({
          msg: "Comentario nao encontrado",
        });
      }
      await comentarioFinded.destroy();

      return res.status(200).json({
        msg: "Comentario deletado com sucesso",
      });
    } catch (error) {
      console.error(error);
      return res.status(500).json({ msg: "Acione o Suporte" });
    }
  },
  getByPostId: async (req, res) => { // Nova função para buscar comentários por postId
    try {
      const { postId } = req.params; // Captura o postId da URL
      const comentarios = await Comentario.findAll({ where: { postId } });

      return res.status(200).json(comentarios);
    } catch (error) {
      console.error(error);
      return res.status(500).json({ msg: "Erro ao buscar comentários" });
    }
  }
};

module.exports = ComentarioController;