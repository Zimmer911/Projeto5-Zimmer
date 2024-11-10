const Comentario = require("../models/Comentario");

const ComentarioController = {
    create: async (req, res) => {
        try {
            const { nome, descricao, postId } = req.body; // Incluindo postId

            const comentarioCriado = await Comentario.create({ nome, descricao, postId });

            return res.status(201).json({
                msg: "Comentário criado com sucesso!",
                comentario: comentarioCriado,
            });
        } catch (error) {
            console.error(error);
            return res.status(500).json({ msg: "Erro ao criar comentário" });
        }
    },

    // Nova função para obter comentários por postId
    getAllByPostId: async (req, res) => {
        try {
            const { postId } = req.params;
            const comentarios = await Comentario.findAll({ where: { postId } });
            return res.status(200).json(comentarios);
        } catch (error) {
            console.error(error);
            return res.status(500).json({ msg: "Erro ao buscar comentários" });
        }
    },

    update: async (req, res) => {
        try {
            const { id } = req.params;
            const { nome, descricao, nota } = req.body;

            const comentarioUpdate = await Comentario.findByPk(id);

            if (comentarioUpdate == null) {
                return res.status(404).json({
                    msg: "Comentário não encontrado",
                });
            }

            const updated = await comentarioUpdate.update({
                nome, 
                descricao, 
                nota
            });
            if (updated) {
                return res.status(200).json({
                    msg: "Comentário atualizado com sucesso!",
                });
            }
            return res.status(500).json({
                msg: "Erro ao atualizar comentário"
            });
        } catch (error) {
            console.error(error);
            return res.status(500).json({ msg: "Acione o Suporte" });
        }
    },

    getAll: async (req, res) => {
        try {
            const comentarios = await Comentario.findAll();
            return res.status(200).json({
                msg: "Comentários Encontrados!",
                comentarios,
            });
        } catch (error) {
            console.error(error);
            return res.status(500).json({ msg: "Acione o Suporte" });
        }
    },

    getOne: async (req, res) => {
        try {
            const { id } = req.params;

            const comentarioEncontrado = await Comentario.findByPk(id);

            if (comentarioEncontrado == null) {
                return res.status(404).json({
                    msg: "Comentário não encontrado!",
                });
            }
            return res.status(200).json({
                msg: "Comentário Encontrado",
                usuario: comentarioEncontrado,
            });
        } catch (error) {
            console.error(error);
            return res.status(500).json({ msg: "Acione o Suporte" });
        }
    },

    delete: async (req, res) => {
        try {
            const { id } = req.params;

            const comentarioFinded = await Comentario.findByPk(id);

            if (comentarioFinded == null) {
                return res.status(404).json({
                    msg: "Comentário não encontrado",
                });
            }
            await comentarioFinded.destroy();

            return res.status(200).json({
                msg: "Comentário deletado com sucesso",
            });
        } catch (error) {
            console.error(error);
            return res.status(500).json({ msg: "Acione o Suporte" });
        }
    },
};

module.exports = ComentarioController;