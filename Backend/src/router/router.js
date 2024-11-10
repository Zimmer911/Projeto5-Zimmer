const { Router } = require("express");
const userRoutes = require("./routerUser");
const user2Routes = require("./routerUser2");
const comentarioRoutes = require("./routerComentario");
const ComentarioController = require('../controller/ComentarioController');
const UserController = require("../controller/UserController");
const authenticateToken = require("../middlewares/authenticateToken");

const uploadRoutes = require('./routerUpload');
const publicacaoRoutes = require('./routerPublicacao')

const router = Router();

router.use('/comentario', comentarioRoutes);

router.use('/image', uploadRoutes);

router.use('/user', userRoutes);

router.use('/user2', user2Routes);

router.use('/publicacao', publicacaoRoutes);

module.exports = router;