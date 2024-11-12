const Publicacao = require("../models/Publicacao");
const fs = require('fs');
const path = require('path');

const PublicacaoController = {
  create: async (req, res) => {
    try {
      const { nome, descricao, nota } = req.body;
      let imagemPath = null;

      if (req.file) {
        imagemPath = req.file.filename; // Nome do arquivo salvo
      }

      const publicacaoCriada = await Publicacao.create({ 
        nome, 
        descricao, 
        nota,
        imagem: imagemPath 
      });

      return res.status(200).json({
        msg: "Publicação criada com sucesso!",
        publicacao: publicacaoCriada,
      });
    } catch (error) {
      console.error(error);
      return res.status(500).json({ msg: "Erro ao criar publicação" });
    }
  },

  update: async (req, res) => {
    try {
      const { id } = req.params;
      const { nome, descricao, nota } = req.body;

      const publicacaoUpdate = await Publicacao.findByPk(id);

      if (!publicacaoUpdate) {
        return res.status(404).json({
          msg: "Publicação não encontrada",
        });
      }

      let imagemPath = publicacaoUpdate.imagem;

      if (req.file) {
        // Se há uma nova imagem, deletamos a antiga (se existir)
        if (publicacaoUpdate.imagem) {
          const oldImagePath = path.join(__dirname, '..', '..', 'uploads', publicacaoUpdate.imagem);
          fs.unlink(oldImagePath, (err) => {
            if (err) console.error("Erro ao deletar imagem antiga:", err);
          });
        }
        imagemPath = req.file.filename;
      }

      await publicacaoUpdate.update({
        nome,
        descricao,
        nota,
        imagem: imagemPath
      });

      return res.status(200).json({
        msg: "Publicação atualizada com sucesso!",
        publicacao: publicacaoUpdate,
      });
    } catch (error) {
      console.error(error);
      return res.status(500).json({ msg: "Erro ao atualizar publicação" });
    }
  },

  getAll: async (req, res) => {
    try {
      const publicacoes = await Publicacao.findAll();
      
      const publicacoesComImagens = publicacoes.map(pub => ({
        ...pub.toJSON(),
        imagemUrl: pub.imagem ? `${req.protocol}://${req.get('host')}/uploads/${pub.imagem}` : null
      }));

      return res.status(200).json({
        msg: "Publicações encontradas!",
        publicacoes: publicacoesComImagens,
      });
    } catch (error) {
      console.error(error);
      return res.status(500).json({ msg: "Erro ao buscar publicações" });
    }
  },

  getOne: async (req, res) => {
    try {
      const { id } = req.params;

      const publicacaoEncontrada = await Publicacao.findByPk(id);

      if (!publicacaoEncontrada) {
        return res.status(404).json({
          msg: "Publicação não encontrada!",
        });
      }

      const publicacaoComImagem = {
        ...publicacaoEncontrada.toJSON(),
        imagemUrl: publicacaoEncontrada.imagem 
          ? `${req.protocol}://${req.get('host')}/uploads/${publicacaoEncontrada.imagem}` 
          : null
      };

      return res.status(200).json({
        msg: "Publicação encontrada",
        publicacao: publicacaoComImagem,
      });
    } catch (error) {
      console.error(error);
      return res.status(500).json({ msg: "Erro ao buscar publicação" });
    }
  },

  delete: async (req, res) => {
    try {
      const { id } = req.params;

      const publicacaoFinded = await Publicacao.findByPk(id);

      if (!publicacaoFinded) {
        return res.status(404).json({
          msg: "Publicação não encontrada",
        });
      }

      // Se a publicação tem uma imagem, deletamos o arquivo
      if (publicacaoFinded.imagem) {
        const imagePath = path.join(__dirname, '..', '..', 'uploads', publicacaoFinded.imagem);
        fs.unlink(imagePath, (err) => {
          if (err) console.error("Erro ao deletar imagem:", err);
        });
      }

      await publicacaoFinded.destroy();

      return res.status(200).json({
        msg: "Publicação deletada com sucesso",
      });
    } catch (error) {
      console.error(error);
      return res.status(500).json({ msg: "Erro ao deletar publicação" });
    }
  },
};

module.exports = PublicacaoController;