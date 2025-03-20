/**
 * Rutas para la gestión de usuarios.
 * @module routes/users
 */

const express = require('express');
const router = express.Router();
const User = require('../schemes/User.scheme');
const tokenVerify = require('../middlewares/tokenVerify');

module.exports = (UserModel) => {

    /**
     * Obtiene las plantas de un usuario.
     * @name api/users/plants
     */

    const userModel = new UserModel();

    router.get('/plantlist', tokenVerify, async (req, res) => {
        try {
            const user = User.parse({...req.user, ...req.body});
            if (user) {
                const plants = await userModel.getPlants(user.uid)
                return res.status(200).json({
                    message: 'Plantas obtenidas correctamente.',
                    plants: plants
                })
            }
        } catch (error) {
            console.error('Error al obtener las plantas de un usuario.')
            return res.status(500).json({
                message: 'Error al obtener las plantas de un usuario.'
            })
        }
    })

    router.post('/plantlist/:id', tokenVerify, async (req, res) => {
        try {
            const user = User.parse({...req.user, ...req.body});
            if (user) {
                const plants = await userModel.addPlant(user.uid, req.params.id)
                return res.status(200).json({
                    message: 'Planta añadida correctamente.',
                    plants: plants
                })
            }
        } catch (error) {
            console.error('Error al añadir una planta a la lista de un usuario.')
            return res.status(500).json({
                message: 'Error al añadir una planta a la lista de un usuario.'
            })
        }
    })

    return router;
}