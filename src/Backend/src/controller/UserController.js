const User = require("../models/User");
const jwt = require('jsonwebtoken');

const UserController = {
  /**
   * Função para login de usuário
   * @param {Object} req - Requisição do cliente
   * @param {Object} res - Resposta do servidor
   */
  login: async (req, res) => {
    try {
      const { email, senha } = req.body;

      console.log("Email:", email);
      console.log("Senha:", senha);

      // Buscar usuário
      const user = await User.findOne({ where: { email } });

      console.log("Usuário encontrado:", user);

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

  create: async (req, res) => {
    try {
      const { nome, senha, email } = req.body;

      console.log("Nome:", nome);
      console.log("Senha:", senha);
      console.log("Email:", email);

      const userCriado = await User.create({ nome, senha, email });

      console.log("Usuário criado:", userCriado);

      return res.status(200).json({
        msg: "Usuario criado com sucesso!",
        user: userCriado,
      });
    } catch (error) {
      console.error(error);
      return res.status(500).json({ msg: "Acione o Suporte" });
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
        msg: "Usuario Encontrados",
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
          msg: "user nao encontrado",
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
