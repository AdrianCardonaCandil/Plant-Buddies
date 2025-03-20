/**
 * Rutas para la autenticación de usuarios.
 * @module routes/auth
 */

const express = require('express');
const router = express.Router();
const User = require('../schemes/User.scheme');
const tokenVerify = require('../middlewares/tokenVerify');

module.exports = (UserModel) => {

    /**
     * Registra un nuevo usuario en la aplicación.
     * @name api/auth/register
     */

    const userModel = new UserModel();

    router.post('/register', async (req, res) => {
        try {
            const token = await userModel.createUser(req.body)
            if (token) {
                console.log('Usuario registrado correctamente.')
                return res.status(200).json({
                    message: 'Usuario registrado correctamente.',
                    token: token
                })
            }
        } catch (error) {
            console.error('Error al registrar un usuario.')
            return res.status(500).json({
                message: 'Error al registrar un usuario.'
            })
        }
    })

    /** 
     * Inicia sesión de un usuario en la aplicación.
     * @name api/auth/login
    */

    router.post('/login', async (req, res) => {
        try {
            const token = await userModel.loginUser(req.body)
            if (token) {
                console.log('Usuario autenticado correctamente.')
                return res.status(200).json({
                    message: 'Usuario autenticado correctamente.',
                    token: token
                })
            }
        } catch (error) {
            console.error('Error al autenticar un usuario.')
            return res.status(500).json({
                message: 'Error al autenticar un usuario.'
            })
        }
    })

    return router;
}