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

    /**
     * Obtiene las plantas favoritas de un usuario.
     * @name api/users/favorites
     */
    router.get('/favorites', tokenVerify, async (req, res) => {
        try {
            const user = User.parse({...req.user, ...req.body});
            if (user) {
                const favorites = await userModel.getFavorites(user.uid)
                return res.status(200).json({
                    message: 'Plantas favoritas obtenidas correctamente.',
                    favorites: favorites
                })
            }
        } catch (error) {
            return res.status(500).json({
                message: error.message
            })
        }
    })

    /**
     * Añade una planata a la lista de plantas favoritas de un usuario.
     * @name api/users/favorites/:id
     */
    router.post('/favorites/:id', tokenVerify, async (req, res) => {
        try {
            const user = User.parse({...req.user, ...req.body});
            if (user) {
                await userModel.addFavorite(user.uid, req.params.id)
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
     * Elimina una planta de la lista de plantas favoritas de un usuario.
     * @name api/users/favorites/:id
     */
    router.delete('/favorites/:id', tokenVerify, async (req, res) => {
        try {
            const user = User.parse({...req.user, ...req.body});
            if (user) {
                await userModel.removeFavorite(user.uid, req.params.id)
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

    /**
     * Obtiene las tareas de un usuario.
     * @name api/users/tasks
     */
    router.get('/tasks', tokenVerify, async (req, res) => {
        try {
            const user = User.parse({...req.user, ...req.body});
            if (user) {
                const tasks = await userModel.getTasks(user.uid)
                return res.status(200).json({
                    message: 'Tareas obtenidas correctamente.',
                    tasks: tasks
                })
            }
        } catch (error) {
            return res.status(500).json({
                message: error.message
            })
        }
    })

    /**
     * Añade una tarea a un usuario.
     * @name api/users/tasks
     */
    router.post('/tasks', tokenVerify, async (req, res) => {
        try {
            const user = User.parse({...req.user});
            if (user) {
                const task = await userModel.addTask(user.uid, req.body)
                return res.status(200).json({
                    message: 'Tarea añadida correctamente.',
                    task: task
                })
            }
        } catch (error) {
            return res.status(500).json({
                message: error.message
            })
        }
    })

    /**
     * Elimina una tarea de un usuario.
     * @name api/users/tasks/:id
     */
    router.delete('/tasks/:id', tokenVerify, async (req, res) => {
        try {
            const user = User.parse({...req.user, ...req.body});
            if (user) {
                await userModel.removeTask(user.uid, req.params.id)
                return res.status(200).json({
                    message: 'Tarea eliminada correctamente.',
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