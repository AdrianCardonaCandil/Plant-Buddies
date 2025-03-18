const { DocumentSnapshot } = require('firebase-admin/firestore');
const PlantService = require('../services/Plant.service');

/**
 * Modelo para gestionar operaciones realizadas sobre objetos de plantas.
 * @class PlantModel
 * @property {PlantService} service - Servicio que maneja las operaciones de plantas.
 * @exports PlantModel
 * @version 1.0
 */

class PlantModel {
    constructor() {
        this.service = new PlantService();
    }

    /**
     * Obtiene plantas según filtros de la base de datos.
     * @function getPlants
     * @param {Object} filters - Filtros de búsqueda.
     * @returns {Promise<DocumentSnapshot>} Promesa de la planta encontrada.
     * @throws {Error} Error al obtener la planta.
     */
    getPlants = (filters) => this.service.getPlants(filters);
}

module.exports = PlantModel;