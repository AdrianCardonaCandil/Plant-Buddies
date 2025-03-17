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

    router.post('/register', tokenVerify, async (req, res) => {
        try {
            const user = new User({...req.user, ...req.body});
            userModel.createUser(user).then(() => {
                console.log('Usuario creado correctamente.');
                return res.status(201).json({
                    message: 'Usuario creado correctamente.',
                    user: user
                })
            })
        } catch (error) {
            console.error('Error al crear un usuario:', error);
            return res.status(500).json({
                message: 'Error al crear un usuario.'
            })
        }
    })

    /** 
     * Inicia sesión de un usuario en la aplicación. Unicamente verifica
     * la existencia del usuario y la validez del token de usuario.
     * @name api/auth/login
    */

    router.post('/login', tokenVerify, async (req, res) => {
        try {
            const user = new User({...req.user, ...req.body});
            userModel.loginUser(user).then(user => {
                if (user) {
                    console.log('Usuario autenticado correctamente.');
                    return res.status(200).json({
                        message: 'Usuario autenticado correctamente.',
                        user: user
                    })
                } else {
                    console.error('Usuario no encontrado.');
                    return res.status(404).json({
                        message: 'Usuario no encontrado.'
                    })
                }
            })
        } catch (error) {
            console.error('Error al autenticar un usuario:', error);
            return res.status(500).json({
                message: 'Error al autenticar un usuario.'
            })
        }
    })

    return router;
}