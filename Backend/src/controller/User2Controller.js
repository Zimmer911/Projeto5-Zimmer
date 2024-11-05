// Backend/src/controller/User2Controller.js

const User2 = require("../models/User2");
const jwt = require('jsonwebtoken');

// Função para descriptografar cifra de César
function decifrarCesar(textoCifrado, deslocamento) {
    return textoCifrado
        .split('')
        .map(char => {
            // Preserva caracteres especiais
            if (char === '@' || char === '.' || /\d/.test(char) || char === '_' || char === '-') {
                return char;
            }
            
            // Processa apenas letras
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

const User2Controller = {
    create: async (req, res) => {
        try {
            const { nome, email, senha } = req.body;

            // Descriptografa nome e email (usando deslocamento 3)
            const nomeDecifrado = decifrarCesar(nome, 3);
            const emailDecifrado = decifrarCesar(email, 3);

            // Log adicionado para verificar os dados recebidos e decifrados
            console.log("Dados recebidos:", {
                nomeOriginal: nome,
                emailOriginal: email,
                nomeDecifrado: nomeDecifrado,
                emailDecifrado: emailDecifrado
            });

            const user2Criado = await User2.create({ 
                nome: nomeDecifrado, 
                email: emailDecifrado, 
                senha 
            });

            return res.status(200).json({
                msg: "Usuario criado com sucesso!",
                user2: user2Criado,
            });
        } catch (error) {
            console.error("Erro ao criar usuário:", error);
            return res.status(500).json({ msg: "Acione o Suporte" });
        }
    },

    login: async (req, res) => {
        try {
            const { email, senha } = req.body;

            // Buscar usuário
            const user2 = await User2.findOne({ where: { email } });

            if (!user2) {
                console.log("Usuário não encontrado");
                return res.status(401).json({ msg: "Usuário não encontrado. Por favor, verifique seu email e senha." });
            }

            // Comparar senha
            if (user2.senha !== senha) {
                console.log("Senha incorreta");
                return res.status(401).json({ msg: "Senha incorreta. Por favor, verifique sua senha." });
            }

            // Gerar token
            const token = jwt.sign({ email: user2.email, nome: user2.nome }, process.env.SECRET, { expiresIn: "1h" });

            console.log("Token gerado:", token);

            return res.status(200).json({ msg: "Login realizado", token });
        } catch (error) {
            console.error("Erro ao fazer login:", error);
            return res.status(500).json({ msg: "Erro ao fazer login. Por favor, tente novamente." });
        }
    },

    update: async (req, res) => {
        try {
            const { id } = req.params;
            const { nome, senha, email } = req.body;

            console.log({ id });
            console.log({ nome, senha, email });

            const user2Update = await User2.findByPk(id);

            if (user2Update == null) {
                return res.status(404).json({
                    msg: "usuario nao encontrado",
                });
            }

            const updated = await user2Update.update({
                nome,
                senha,
                email,
            });
            if (updated) {
                return res.status(200).json({
                    msg: "Usuario atualizado com sucesso!",
                });
            }
            return res.status(500).json({
                msg: "Erro ao atualizar usuario",
            });
        } catch (error) {
            console.error(error);
            return res.status(500).json({ msg: "Acione o Suporte" });
        }
    },

    getAll: async (req, res) => {
        try {
            const usuarios = await User2.findAll();
            return res.status(200).json({
                msg: "Usuarios Encontrados!",
                usuarios,
            });
        } catch (error) {
            console.error(error);
            return res.status(500).json({ msg: "Acione o Suporte" });
        }
    },

    getOne: async (req, res) => {
        try {
            const { id } = req.params;

            const usuarioEncontrado = await User2.findByPk(id);

            if (usuarioEncontrado == null) {
                return res.status(404).json({
                    msg: "Usuario nao encontrado!",
                });
            }
            return res.status(200).json({
                msg: "Usuario Encontrado",
                usuario: usuarioEncontrado,
            });
        } catch (error) {
            console.error(error);
            return res.status(500).json({ msg: "Acione o Suporte" });
        }
    },

    delete: async (req, res) => {
        try {
            const { id } = req.params;

            const user2Finded = await User2.findByPk(id);

            if (user2Finded == null) {
                return res.status(404).json({
                    msg: "Usuario nao encontrado",
                });
            }
            await user2Finded.destroy();

            return res.status(200).json({
                msg: "Usuario deletado com sucesso",
            });
        } catch (error) {
            console.error(error);
            return res.status(500).json({ msg: "Acione o Suporte" });
        }
    },
};

module.exports = User2Controller;