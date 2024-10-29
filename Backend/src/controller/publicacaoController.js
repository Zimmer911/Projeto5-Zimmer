const Publicacao = require("../models/Publicacao");
const multer = require('multer');
const sharp = require('sharp');
const path = require('path');
const fs = require('fs');

const upload = multer({ dest: 'uploads/' });

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

const PublicacaoController = {
  create: async (req, res) => {
    try {
      const { nome, descricao, nota } = req.body;
      const image = req.file;

      if (!image) {
        return res.status(400).json({ msg: "Imagem é obrigatória" });
      }

      // Descriptografar nome e descrição
      const nomeDecifrado = decifrarCesar(nome, 3);
      const descricaoDecifrada = decifrarCesar(descricao, 3);

      const imageName = image.originalname;
      const imageData = image.buffer;

      await sharp(imageData).toFile(`uploads/${imageName}`);

      const publicacaoCriada = await Publicacao.create({ 
        nome: nomeDecifrado, 
        descricao: descricaoDecifrada, 
        nota, 
        imagem: imageName 
      });

      return res.status(200).json({
        msg: "Publicação criada com sucesso!",
        user: publicacaoCriada,
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

      const publicacaoUpdate = await Publicacao.findByPk(id);

      if (publicacaoUpdate == null) {
        return res.status(404).json({
          msg: "Publicação não encontrada",
        });
      }

      if (req.file) {
        const image = req.file;
        const imageName = image.originalname;
        const imageData = image.buffer;

        await sharp(imageData).toFile(`uploads/${imageName}`);

        publicacaoUpdate.imagem = imageName;
      }
      await publicacaoUpdate.update({
        nome: nomeDecifrado,
        descricao: descricaoDecifrada,
        nota
      });

      return res.status(200).json({
        msg: "Publicação atualizada com sucesso!",
      });
    } catch (error) {
      console.error(error);
      return res.status(500).json({ msg: "Acione o Suporte" });
    }
  },

  getAll: async (req, res) => {
    try {
      const publicacoes = await Publicacao.findAll();
      return res.status(200).json({
        msg: "Publicações encontradas!",
        publicacoes,
      });
    } catch (error) {
      console.error(error);
      return res.status(500).json({ msg: "Acione o Suporte" });
    }
  },

  getOne: async (req, res) => {
    try {
      const { id } = req.params;

      const publicacaoEncontrada = await Publicacao.findByPk(id);

      if (publicacaoEncontrada == null) {
        return res.status(404).json({
          msg: "Publicação não encontrada!",
        });
      }

      return res.status(200).json({
        msg: "Publicação encontrada",
        publicacao: publicacaoEncontrada,
      });
    } catch (error) {
      console.error(error);
      return res.status(500).json({ msg: "Acione o Suporte" });
    }
  },

  delete: async (req, res) => {
    try {
      const { id } = req.params;

      const publicacaoFinded = await Publicacao.findByPk(id);

      if (publicacaoFinded == null) {
        return res.status(404).json({
          msg: "Publicação não encontrada",
        });
      }

      // Remover a imagem do servidor se ela existir
      if (publicacaoFinded.imagem) {
        const imagePath = path.join(__dirname, '..', '..', 'uploads', publicacaoFinded.imagem);
        if (fs.existsSync(imagePath)) {
          fs.unlinkSync(imagePath);
        }
      }

      await publicacaoFinded.destroy();

      return res.status(200).json({
        msg: "Publicação deletada com sucesso",
      });
    } catch (error) {
      console.error(error);
      return res.status(500).json({ msg: "Acione o Suporte" });
    }
  },

  listImages: async (req, res) => {
    fs.readdir('uploads/', (err, files) => {
      if (err) {
        return res.status(500).json({
          msg: "Erro ao listar imagens"
        });
      }

      const images = files.filter(
        (file) =>
          file.endsWith(".jpg") ||
          file.endsWith(".png") ||
          file.endsWith(".jpeg")
      );
      res.send(images);
    });
  },

  getImage: (req, res) => {
    const imageName = req.params.imageName;
    const imagePath = path.join(__dirname, '..', '..', 'uploads', imageName);
    
    if (fs.existsSync(imagePath)) {
      return res.sendFile(imagePath);
    } else {
      return res.status(404).json({
        msg: "Imagem não encontrada"
      });
    }
  }
};

module.exports = PublicacaoController;