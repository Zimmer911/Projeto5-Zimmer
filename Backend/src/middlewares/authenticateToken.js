// src/middlewares/authenticateToken.js
const jwt = require('jsonwebtoken');

const authenticateToken = (req, res, next) => {
    const authHeader = req.headers['authorization'];
    const token = authHeader && authHeader.split(' ')[1];

    if (!token) {
        return res.status(401).json({ 
            msg: "Token de autenticação não fornecido" 
        });
    }

    try {
        const decoded = jwt.verify(token, process.env.JWT_SECRET);
        req.user = decoded;
        next();
    } catch (error) {
        if (error.name === 'TokenExpiredError') {
            return res.status(401).json({ 
                msg: "Token expirado" 
            });
        }
        return res.status(403).json({ 
            msg: "Token inválido" 
        });
    }
};

module.exports = authenticateToken;