const { Router } = require("express");
const PublicacaoController = require("../controller/publicacaoController");
const multer = require('multer');
const upload = multer({ storage: multer.memoryStorage() });
const { validatePublication, validatePublicationId } = require("../middlewares/ValidatePublicacao");

const router = Router();

router.post('/', upload.single('image'), validatePublication, (req, res) => {
  PublicacaoController.create(req, res)
});

router.put('/:id', upload.single('image'), validatePublication, validatePublicationId, (req, res) => {
  PublicacaoController.update(req, res)
});

router.get('/', (req, res) => {
  PublicacaoController.getAll(req, res)
});

router.get('/:id', validatePublicationId, (req, res) => {
  PublicacaoController.getOne(req, res)
});

router.delete('/:id', validatePublicationId, (req, res) => {
  PublicacaoController.delete(req, res)
});

module.exports = router;