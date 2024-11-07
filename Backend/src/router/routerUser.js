const { Router } = require("express");
const UserController = require("../controller/UserController");
const { validateUser , validateUser Id } = require("../middlewares/ValidateUser ");
const bcrypt = require('bcrypt');
const User = require("../models/User");

const router = Router();

// Função para descriptografar a cifra de César
function decifrarCesar(textoCifrado, deslocamento) {
    return textoCifrado
        .split('')
        .map(char => {
            if (/[A-Za-z]/.test(char)) {
                const code = char.charCodeAt(0);
                const base = code >= 97 ? 97 : 65; // base para minúsculas ou maiúsculas
                return String.fromCharCode(
                    ((code - base - deslocamento + 26) % 26) + base
                );
            }
            return char;
        })
        .join('');
}

router.post('/', validateUser , (req, res) => {
    UserController.create(req, res);
});

router.put('/:id', validateUser , validateUser Id, (req, res) => {
    UserController.update(req, res);
});

router.get('/', (req, res) => {
    UserController.getAll(req, res);
});

router.get('/:id', validateUser Id, (req, res) => {
    UserController.getOne(req, res);
});

router.delete('/:id', validateUser Id, (req, res) => {
    UserController.delete(req, res);
});

// Nova rota de login com descriptografia
router.get('/login', async (req, res) => {
    const { email, senha } = req.query;

    // Descriptografar email e senha
    const emailDecifrado = decifrarCesar(email, 3);
    const senhaDecifrada = decifrarCesar(senha, 3);

    try {
        const user = await User.findOne({ where: { email: emailDecifrado } });

        if (!user) {
            return res.status(401).json({ msg: "Usuário não encontrado" });
        }

        const senhaCorreta = await bcrypt.compare(senhaDecifrada, user.senha);

        if (!senhaCorreta) {
            return res.status(401).json({ msg: "Senha incorreta" });
        }

        return res.status(200).json({ msg: "Login realizado com sucesso!" });
    } catch (error) {
        console.error("Erro durante o login:", error);
        return res.status(500).json({ msg: "Erro interno do servidor" });
    }
});

module.exports = router;