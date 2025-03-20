/**
 * Rutas de la API de plantas.
 */

const express = require('express');
const router = express.Router();

const {db} = require('../config/firebase');

module.exports = (PlantModel) => {
    
    /**
    * Obtiene plantas según filtros de la base de datos.
    * @name api/plants
    */
   const plantModel = new PlantModel();

   router.post('/', async (req, res) => {
       try {
           const plants = await plantModel.getPlants(req.body);
           return res.status(200).json({
               message: 'Plantas obtenidas correctamente.',
               plants: plants
           })
       } catch (error) {
           console.error('Error al obtener las plantas:', error);
           return res.status(500).json({
               message: 'Error al obtener las plantas.'
           })
       }
   })

   /**
    * Obtiene planta según el ID de la base de datos.
    * @name api/plants/:id
    */
   router.get('/:id', async (req, res) => {
       try {
           const plant = await plantModel.getPlantById(req.params.id);
           return res.status(200).json({
               message: 'Planta obtenida correctamente.',
               plant: plant
           })
       } catch (error) {
           console.error('Error al obtener la planta:', error);
           return res.status(500).json({
               message: 'Error al obtener la planta.'
           })
       }
   })

   return router;
}