// src/router/routerUser.js
const { Router } = require("express");
const UserController = require("../controller/UserController");
const { validateUser, validateUserId } = require("../middlewares/ValidateUser");
const authenticateToken = require("../middlewares/authenticateToken");

const router = Router();

// Rotas públicas
router.post('/register', validateUser, (req, res) => {
    UserController.create(req, res);
});

router.post('/login', (req, res) => {
    UserController.login(req, res);
});

// Rotas protegidas
router.use(authenticateToken); // Middleware de autenticação para as rotas abaixo

router.put('/:id', validateUser, validateUserId, (req, res) => {
    UserController.update(req, res);
});

router.put('/:id/password', validateUserId, (req, res) => {
    UserController.updatePassword(req, res);
});

router.get('/profile', (req, res) => {
    UserController.getProfile(req, res);
});

router.delete('/:id', validateUserId, (req, res) => {
    UserController.delete(req, res);
});

module.exports = router;