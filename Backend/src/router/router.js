const { Router } = require("express");
const userRoutes = require("./routerUser"); // Removido espaço extra
const user2Routes = require("./routerUser2"); // Verifique se não há espaço extra
const comentarioRoutes = require("./routerComentario");
const UserController = require("../controller/UserController");
const authenticateToken = require("../middlewares/authenticateToken");

const uploadRoutes = require('./routerUpload');
const publicacaoRoutes = require('./routerPublicacao');

const router = Router();

// Rota para obter comentários por postId
router.get('/comentario/post/:postId', (req, res) => {
    ComentarioController.getAllByPostId(req, res);
});

router.use('/comentario', comentarioRoutes);
router.use('/image', uploadRoutes);
router.use('/user', userRoutes);
router.use('/user2', user2Routes);
router.use('/publicacao', publicacaoRoutes);

module.exports = router;