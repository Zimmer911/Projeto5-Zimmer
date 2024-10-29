const { Router } = require("express");
const UserController = require("../controller/UserController");
const { validateUser, validateUserId } = require("../middlewares/ValidateUser");

const router = Router();

router.post('/register', validateUser, (req, res) => {
    UserController.create(req, res);
});

router.post('/login', (req, res) => {
    UserController.login(req, res);
});

router.put('/:id', validateUser, validateUserId, (req, res) => {
    UserController.update(req, res);
});

router.delete('/:id', validateUserId, (req, res) => {
    UserController.delete(req, res);
});

module.exports = router;