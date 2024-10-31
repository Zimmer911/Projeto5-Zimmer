const { Router } = require("express");
const PublicacaoController = require("../controller/publicacaoController");
const { validatePublication, validatePublicationId } = require("../middlewares/ValidatePublicacao");
const multer = require('multer');
const path = require('path');

const router = Router();

// Configuração do Multer
const storage = multer.diskStorage({
  destination: function (req, file, cb) {
    cb(null, 'uploads/') // Certifique-se de que este diretório existe
  },
  filename: function (req, file, cb) {
    cb(null, Date.now() + path.extname(file.originalname)) // Gera um nome único para o arquivo
  }
});

const upload = multer({ storage: storage });

// Rota para criar uma nova publicação
router.post('/', upload.single('image'), validatePublication, (req, res) => {
  PublicacaoController.create(req, res)
});

// Rota para atualizar uma publicação existente
router.put('/:id', upload.single('image'), validatePublication, validatePublicationId, (req, res) => {
  PublicacaoController.update(req, res)
});

// Rota para obter todas as publicações
router.get('/', (req, res) => {
  PublicacaoController.getAll(req, res)
});

// Rota para obter uma publicação específica
router.get('/:id', validatePublicationId, (req, res) => {
  PublicacaoController.getOne(req, res)
});

// Rota para deletar uma publicação
router.delete('/:id', validatePublicationId, (req, res) => {
  PublicacaoController.delete(req, res)
});

module.exports = router;