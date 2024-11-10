const Comentario = require("../models/Comentario");

const ComentarioController = {
  create: async (req, res) => {
    try {
      const { nome, descricao, nota, postId } = req.body; // Incluindo postId

      // Verificação de campos obrigatórios
      if (!nome || !descricao || !nota || !postId) {
        return res.status(400).json({ msg: "Todos os campos são obrigatórios." });
      }

      const comentarioCriado = await Comentario.create({ nome, descricao, nota, postId }); // Adicionando postId na criação

      return res.status(201).json({
        msg: "Comentário criado com sucesso!",
        comentario: comentarioCriado, // Corrigido para 'comentario'
      });
    } catch (error) {
      console.error("Erro ao criar comentário:", error);
      return res.status(500).json({ msg: "Acione o Suporte" });
    }
  },
  
  update: async (req, res) => {
    try {
      const { id } = req.params;
      const { nome, descricao, nota } = req.body;

      const comentarioUpdate = await Comentario.findByPk(id);

      if (!comentarioUpdate) {
        return res.status(404).json({
          msg: "Comentário não encontrado",
        });
      }

      const updated = await comentarioUpdate.update({
        nome, 
        descricao, 
        nota
      });
      
      return res.status(200).json({
        msg: "Comentário atualizado com sucesso!",
        comentario: updated, // Retorna o comentário atualizado
      });
    } catch (error) {
      console.error("Erro ao atualizar comentário:", error);
      return res.status(500).json({ msg: "Acione o Suporte" });
    }
  },
  
  getAll: async (req, res) => {
    try {
      const comentarios = await Comentario.findAll();
      return res.status(200).json({
        msg: "Comentários encontrados!",
        comentarios,
      });
    } catch (error) {
      console.error("Erro ao buscar comentários:", error);
      return res.status(500).json({ msg: "Acione o Suporte" });
    }
  },
  
  getOne: async (req, res) => {
    try {
      const { id } = req.params;

      const comentarioEncontrado = await Comentario.findByPk(id);

      if (!comentarioEncontrado) {
        return res.status(404).json({
          msg: "Comentário não encontrado!",
        });
      }
      return res.status(200).json({
        msg: "Comentário encontrado",
        comentario: comentarioEncontrado, // Corrigido para 'comentario'
      });
    } catch (error) {
      console.error("Erro ao buscar comentário:", error);
      return res.status(500).json({ msg: "Acione o Suporte" });
    }
  },
  
  delete: async (req, res) => {
    try {
      const { id } = req.params;

      const comentarioFinded = await Comentario.findByPk(id);

      if (!comentarioFinded) {
        return res.status(404).json({
          msg: "Comentário não encontrado",
        });
      }
      await comentarioFinded.destroy();

      return res.status(200).json({
        msg: "Comentário deletado com sucesso",
      });
    } catch (error) {
      console.error("Erro ao deletar comentário:", error);
      return res.status(500).json({ msg: "Acione o Suporte" });
    }
  },
  
  getByPostId: async (req, res) => { // Nova função para buscar comentários por postId
    try {
      const { postId } = req.params; // Captura o postId da URL

      // Verificação de postId
      if (!postId) {
        return res.status(400).json({ msg: "O parâmetro postId é obrigatório." });
      }

      const comentarios = await Comentario.findAll({ where: { postId } });

      return res.status(200).json({
        msg: "Comentários encontrados para o post!",
        comentarios,
      });
    } catch (error) {
      console.error("Erro ao buscar comentários por postId:", error);
      return res.status(500).json({ msg: "Erro ao buscar comentários" });
    }
  }
};

module.exports = ComentarioController;