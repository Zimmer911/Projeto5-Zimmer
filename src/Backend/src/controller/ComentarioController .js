const Comentario = require("../models/Comentario");

// Função para descriptografar a cifra de César
function decifrarCesar(textoCifrado, deslocamento) {
    return textoCifrado
        .split('')
        .map(char => {
            if (char.match(/[a-z]/i)) {
                const code = char.charCodeAt(0);
                const base = char.toLowerCase() === char ? 97 : 65;
                return String.fromCharCode(((code - base - deslocamento + 26) % 26) + base);
            }
            return char;
        })
        .join('');
}

const ComentarioController = {
  create: async (req, res) => {
    try {
      const { nome, descricao, nota } = req.body;

      // Descriptografar nome e descrição
      const nomeDecifrado = decifrarCesar(nome, 3);
      const descricaoDecifrada = decifrarCesar(descricao, 3);

      const comentarioCriado = await Comentario.create({ 
        nome: nomeDecifrado, 
        descricao: descricaoDecifrada, 
        nota 
      });

      return res.status(200).json({
        msg: "Comentario criado com sucesso!",
        user: comentarioCriado,
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

      // Descriptografar nome e descrição
      const nomeDecifrado = decifrarCesar(nome, 3);
      const descricaoDecifrada = decifrarCesar(descricao, 3);

      const comentarioUpdate = await Comentario.findByPk(id);

      if (comentarioUpdate == null) {
        return res.status(404).json({
          msg: "Comentario nao encontrado",
        });
      }

      const updated = await comentarioUpdate.update({
        nome: nomeDecifrado, 
        descricao: descricaoDecifrada, 
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
};

module.exports = ComentarioController;