// src/controller/UserController.js
const User = require("../models/User");
const jwt = require('jsonwebtoken');
const crypto = require('crypto');

const UserController = {
    login: async (req, res) => {
        try {
            const { email, senha } = req.body;

            const user = await User.findOne({ where: { email } });
            if (!user) {
                return res.status(401).json({ 
                    msg: "Email ou senha incorretos" 
                });
            }

            // Decodifica a senha armazenada de Base64
            const storedPassword = Buffer.from(user.senha, 'base64');
            
            // Extrai o salt (primeiros 16 bytes) e o hash
            const salt = storedPassword.slice(0, 16);
            const storedHash = storedPassword.slice(16);

            // Gera o hash da senha fornecida usando o mesmo salt
            const hash = crypto.pbkdf2Sync(senha, salt, 1000, 64, 'sha256');

            // Compara os hashes de forma segura
            if (!crypto.timingSafeEqual(storedHash, hash.slice(0, storedHash.length))) {
                return res.status(401).json({ 
                    msg: "Email ou senha incorretos" 
                });
            }

            // Gera o token JWT
            const token = jwt.sign(
                { 
                    id: user.id,
                    email: user.email,
                    nome: user.nome 
                }, 
                process.env.JWT_SECRET,
                { expiresIn: '24h' }
            );

            return res.status(200).json({
                msg: "Login realizado com sucesso",
                token,
                user: {
                    id: user.id,
                    nome: user.nome,
                    email: user.email
                }
            });
        } catch (error) {
            console.error('Erro no login:', error);
            return res.status(500).json({ 
                msg: "Erro interno do servidor" 
            });
        }
    },

    create: async (req, res) => {
        try {
            const { nome, email, senha } = req.body;

            // Verifica se o email já existe
            const existingUser = await User.findOne({ where: { email } });
            if (existingUser) {
                return res.status(400).json({ 
                    msg: "Email já cadastrado" 
                });
            }

            // A senha já vem criptografada do cliente
            const user = await User.create({
                nome,
                email,
                senha // Já está em formato Base64 com salt
            });

            return res.status(201).json({
                msg: "Usuário criado com sucesso",
                user: {
                    id: user.id,
                    nome: user.nome,
                    email: user.email
                }
            });
        } catch (error) {
            console.error('Erro ao criar usuário:', error);
            if (error.name === 'SequelizeValidationError') {
                return res.status(400).json({ 
                    msg: "Dados inválidos", 
                    errors: error.errors.map(e => e.message) 
                });
            }
            return res.status(500).json({ 
                msg: "Erro interno do servidor" 
            });
        }
    },

    update: async (req, res) => {
        try {
            const { id } = req.params;
            const { nome, email } = req.body;

            const user = await User.findByPk(id);
            if (!user) {
                return res.status(404).json({ 
                    msg: "Usuário não encontrado" 
                });
            }

            // Não permite atualização de senha por esta rota
            await user.update({ nome, email });

            return res.status(200).json({
                msg: "Usuário atualizado com sucesso",
                user: {
                    id: user.id,
                    nome: user.nome,
                    email: user.email
                }
            });
        } catch (error) {
            console.error('Erro ao atualizar usuário:', error);
            return res.status(500).json({ 
                msg: "Erro interno do servidor" 
            });
        }
    },

    // Rota específica para alteração de senha
    updatePassword: async (req, res) => {
        try {
            const { id } = req.params;
            const { senhaAtual, novaSenha } = req.body;

            const user = await User.findByPk(id);
            if (!user) {
                return res.status(404).json({ 
                    msg: "Usuário não encontrado" 
                });
            }

            // Verificação da senha atual
            const storedPassword = Buffer.from(user.senha, 'base64');
            const salt = storedPassword.slice(0, 16);
            const storedHash = storedPassword.slice(16);
            const hash = crypto.pbkdf2Sync(senhaAtual, salt, 1000, 64, 'sha256');

            if (!crypto.timingSafeEqual(storedHash, hash.slice(0, storedHash.length))) {
                return res.status(401).json({ 
                    msg: "Senha atual incorreta" 
                });
            }

            // A nova senha já deve vir criptografada do cliente
            await user.update({ senha: novaSenha });

            return res.status(200).json({
                msg: "Senha atualizada com sucesso"
            });
        } catch (error) {
            console.error('Erro ao atualizar senha:', error);
            return res.status(500).json({ 
                msg: "Erro interno do servidor" 
            });
        }
    },

    delete: async (req, res) => {
        try {
            const { id } = req.params;

            const user = await User.findByPk(id);
            if (!user) {
                return res.status(404).json({ 
                    msg: "Usuário não encontrado" 
                });
            }

            await user.destroy();

            return res.status(200).json({
                msg: "Usuário deletado com sucesso"
            });
        } catch (error) {
            console.error('Erro ao deletar usuário:', error);
            return res.status(500).json({ 
                msg: "Erro interno do servidor" 
            });
        }
    }
};

module.exports = UserController;