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
            let commonName = null;
            for (const [key, value] of Object.entries(filters)) {
                if (key === 'commonName') {
                    commonName = value;
                    continue;
                }
                query = query.where(key, '==', value);
            }
            const snapshot = await query.get();
            const plants = snapshot.docs.map(doc => Plant.parse(doc.data()));
            if (commonName) {
                return plants.filter(plant => 
                    plant.commonName.toLowerCase().includes(commonName.toLowerCase()) ||
                    plant.scientificName.toLowerCase().includes(commonName.toLowerCase()))
            }
            return plants;

        } catch (error) {
            throw new Error('Error al obtener las plantas.', error);
        }
    }

    /**
     * Obtiene planta según el ID de la base de datos.
     * @function getPlantById
     * @param {String} id - ID de la planta.
     * @returns {Promise<DocumentSnapshot>} Promesa de la planta encontrada.
     * @throws {Error} Error al obtener la planta.
     */
    getPlantById = async (id) => {
        try {
            const snapshot = await this.db.collection(this.collection).doc(id).get();
            return Plant.parse(snapshot.data());
        } catch (error) {
            throw new Error('Error al obtener la planta.', error);
        }
    }
}

module.exports = PlantService;