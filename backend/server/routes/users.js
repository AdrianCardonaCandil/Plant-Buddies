/**
 * Rutas para la gestión de usuarios.
 * @module routes/users
 */

const express = require('express');
const router = express.Router();
const User = require('../schemes/User.scheme');
const tokenVerify = require('../middlewares/tokenVerify');

module.exports = (UserModel, PlantModel) => {

    /**
     * Obtiene las plantas de un usuario.
     * @name api/users/plants
     */

    const userModel = new UserModel();
    const plantModel = new PlantModel();

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
            return res.status(500).json({
                message: error.message
            })
        }
    })

    /**
     * Añade una planta a la lista de un usuario.
     * @name api/users/plantlist/:id
     */
    router.post('/plantlist/:id', tokenVerify, async (req, res) => {
        try {
            const user = User.parse({...req.user, ...req.body});
            if (user) {
                await userModel.addPlant(user.uid, req.params.id)
                plant = await plantModel.getPlantById(req.params.id)
                return res.status(200).json({
                    message: 'Planta añadida correctamente.',
                    plant: plant
                })
            }
        } catch (error) {
            return res.status(500).json({
                message: error.message
            })
        }
    })

    /**
     * Elimina una planta de la lista de un usuario.
     * @name api/users/plantlist/:id
     */
    router.delete('/plantlist/:id', tokenVerify, async (req, res) => {
        try {
            const user = User.parse({...req.user, ...req.body});
            if (user) {
                await userModel.removePlant(user.uid, req.params.id)
                plant = await plantModel.getPlantById(req.params.id)
                return res.status(200).json({
                    message: 'Planta eliminada correctamente.',
                    plant: plant
                })
            }
        } catch (error) {
            return res.status(500).json({
                message: error.message
            })
        }
    })

    return router;
}