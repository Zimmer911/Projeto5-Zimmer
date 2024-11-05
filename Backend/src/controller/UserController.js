// Backend/src/controller/UserController.js

const User = require("../models/User");
const jwt = require('jsonwebtoken');

const UserController = {
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

            const userCriado = await User.create({ 
                nome: nomeDecifrado, 
                email: emailDecifrado, 
                senha 
            });

            return res.status(200).json({
                msg: "Usuario criado com sucesso!",
                user: userCriado,
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
            const user = await User.findOne({ where: { email } });

            if (!user) {
                console.log("Usuário não encontrado");
                return res.status(401).json({ msg: "Usuário não encontrado. Por favor, verifique seu email e senha." });
            }

            // Comparar senha
            if (user.senha !== senha) {
                console.log("Senha incorreta");
                return res.status(401).json({ msg: "Senha incorreta. Por favor, verifique sua senha." });
            }

            // Gerar token
            const token = jwt.sign({ email: user.email, nome: user.nome }, process.env.SECRET, { expiresIn: "1h" });

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

            const userUpdate = await User.findByPk(id);

            if (userUpdate == null) {
                return res.status(404).json({
                    msg: "usuario nao encontrado",
                });
            }

            const updated = await userUpdate.update({
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
            const usuarios = await User.findAll();
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

            const usuarioEncontrado = await User.findByPk(id);

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

            const userFinded = await User.findByPk(id);

            if (userFinded == null) {
                return res.status(404).json({
                    msg: "Usuario nao encontrado",
                });
            }
            await userFinded.destroy();

            return res.status(200).json({
                msg: "Usuario deletado com sucesso",
            });
        } catch (error) {
            console.error(error);
            return res.status(500).json({ msg: "Acione o Suporte" });
        }
    },
};

module.exports = UserController;