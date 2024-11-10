const {Router} = require("express");
const ComentarioController = require("../controller/ComentarioController ");
const { validateComentario, validateComentarioId } = require("../middlewares/ValidateComentario");

const router = Router();

router.post('/',validateComentario,(req,res) => {
    ComentarioController.create(req,res)
});
router.put('/:id',validateComentario,validateComentarioId,(req,res) => {
    ComentarioController.update(req,res)
});
router.get('/',(req,res) => {
    ComentarioController.getAll(req,res)
});
router.get('/:id',validateComentarioId,(req,res) => {
    ComentarioController.getOne(req,res)
});
router.delete('/:id',validateComentarioId,(req,res) => {
    ComentarioController.delete(req,res)
});

module.exports = router;