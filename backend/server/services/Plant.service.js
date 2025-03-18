const Plant = require("../schemes/Plant.scheme");
const {db} = require('../config/firebase');
const { DocumentSnapshot } = require('firebase-admin/firestore');

/**
 * Servicio para gestionar las operaciones de plantas.
 * @class PlantService
 * @property {Firebase.firestore.CollectionReference} plants - Referencia a la colección de plantas.
 * @exports PlantService
 * @version 1.0
 */

class PlantService {
    constructor() {
        this.collection = process.env.PLANTS_COLLECTION;
        this.db = db;
    }

    /**
     * Obtiene plantas según filtros de la base de datos.
     * @function getPlants
     * @param {string} filters - Filtros de búsqueda.
     * @returns {Promise<DocumentSnapshot>} Promesa de la planta encontrada.
     * @throws {Error} Error al obtener la planta.
     */
    getPlants = async (filters) => {
        try {
            let query = this.db.collection(this.collection);
            for (const [key, value] of Object.entries(filters)) {
                query = query.where(key, '==', value);
            }
            const snapshot = await query.get();
            const plants = snapshot.docs.map(doc => Plant.parse(doc.data()));
            return plants;

        } catch (error) {
            throw new Error('Error al obtener las plantas.', error);
        }
    }
}

module.exports = PlantService;