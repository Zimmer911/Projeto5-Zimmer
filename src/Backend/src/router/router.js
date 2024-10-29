const { Router } = require("express");
const userRoutes = require("./routerUser");
const comentarioRoutes = require("./routerComentario");
const UserController = require("../controller/UserController");
const authenticateToken = require("../middlewares/authenticateToken");

const uploadRoutes = require('./routerUpload');
const publicacaoRoutes = require('./routerPublicacao');

const router = Router();

router.use('/comentario', comentarioRoutes);

router.use('/image', uploadRoutes);

router.use('/user', userRoutes);

router.use('/publicacao', publicacaoRoutes);

module.exports = router;