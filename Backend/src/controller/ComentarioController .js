// Backend/src/controller/ComentarioController.js

const Comentario = require("../models/Comentario");

const ComentarioController = {
  create: async (req, res) => {
    try {
      const { nome, descricao, nota, publicacaoId } = req.body;

      if (!publicacaoId) {
        return res.status(400).json({
          msg: "ID da publicação é obrigatório",
        });
      }

      const comentarioCriado = await Comentario.create({ 
        nome, 
        descricao, 
        nota, 
        publicacaoId 
      });

      return res.status(200).json({
        msg: "Comentario criado com sucesso!",
        comentario: comentarioCriado,
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
        msg: "Erro ao atualizar comentario"
      });
    } catch (error) {
      console.error(error);
      return res.status(500).json({ msg: "Acione o Suporte" });
    }
  },

  getAll: async (req, res) => {
    try {
      const { publicacaoId } = req.query;

      if (!publicacaoId) {
        return res.status(400).json({
          msg: "ID da publicação é obrigatório",
        });
      }

      const comentarios = await Comentario.findAll({
        where: { publicacaoId: publicacaoId }
      });

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
        comentario: comentarioEncontrado,
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

  // Novo método para contar comentários por publicação
  countByPublicacao: async (req, res) => {
    try {
      const { publicacaoId } = req.params;

      const count = await Comentario.count({
        where: { publicacaoId: publicacaoId }
      });

      return res.status(200).json({
        msg: "Contagem de comentários realizada com sucesso",
        count: count,
      });
    } catch (error) {
      console.error(error);
      return res.status(500).json({ msg: "Acione o Suporte" });
    }
  }
};

module.exports = ComentarioController;