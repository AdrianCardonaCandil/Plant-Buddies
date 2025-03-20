const User = require("../schemes/User.scheme");
const {db} = require("../config/firebase");
const { DocumentSnapshot } = require("firebase-admin/firestore");
const admin = require("firebase-admin");

/**
 * Servicio para gestionar operaciones realizadas sobre objetos de usuario.
 * @class UserService
 * @property {Collection} collection - Colección de usuarios en la base de datos.
 * @property {FirebaseFirestore} db - Instancia de la base de datos Firestore.
 * @exports UserService
 * @version 1.0
 */

class UserService {
    constructor() {
        this.collection = process.env.USERS_COLLECTION;
        this.db = db;
    }

    /**
     * Crea un nuevo usuario en la base de datos.
     * @param {Object} user - Objeto de usuario a crear.
     * @returns {Promise<void>} Promesa de usuario creado.
     * @throws {Error} Error al crear el usuario.
     */
    createUser = async (user) => {
        try {
            await this.db.collection(this.collection).doc(user.uid).set({...user});
        } catch (error) {
            throw new Error('Error al crear el usuario', {cause: error});
        }
    }

    /**
     * Inicia sesión de un usuario en la aplicación.
     * @param {String} uid - Identificador del usuario a autenticar.
     * @returns {Promise<User>} Objeto de usuario autenticado.
     * @throws {Error} Error al autenticar el usuario.
     */
    loginUser = async(uid) => {
        try {
            const foundUser = await this.getUser(uid);
            if (!foundUser) {
                throw new Error('Usuario no encontrado');
            }
            return foundUser;
        } catch (error) {
            throw new Error('Error al autenticar el usuario', {cause: error});
        }
    }

    /**
     * Obtiene un usuario de la base de datos.
     * @param {string} uid - Identificador único del usuario.
     * @returns {Promise<User>} Promesa de usuario encontrado.
     * @throws {Error} Error al obtener el usuario.
     */
    getUser = async (uid) => {
        try {
            const snapshot = await this.db.collection(this.collection).doc(uid).get();
            if (!snapshot.exists) {
                throw new Error('Usuario no encontrado');
            }
            return User.parse(snapshot.data());
        } catch (error) {
            throw new Error('Error al buscar el usuario', {cause: error});
        }
    }

    /**
     * Obtiene las plantas de un usuario.
     * @param {string} uid - Identificador único del usuario.
     * @returns {Promise<Plant[]>} Promesa de las plantas encontradas.
     * @throws {Error} Error al obtener las plantas.
     */
    getPlants = async (uid) => {
        try {
            const snapshot = await this.db.collection(this.collection).doc(uid).get();
            if (!snapshot.exists) {
                throw new Error('Usuario no encontrado');
            }
            const plantIds = snapshot.data().plants;
            const plantsCollection = process.env.PLANTS_COLLECTION;
            const plants = await Promise.all(plantIds.map(async (id) => {
                const plantSnapshot = await this.db.collection(plantsCollection).doc(id).get();
                return plantSnapshot.data();
            }));
            return plants;
        } catch (error) {
            throw new Error(`Error al obtener las plantas: ${error.message}`);
        }
    }

    /**
     * Añade una planta a un usuario.
     * @param {string} uid - Identificador único del usuario.
     * @param {string} plantId - Identificador único de la planta.
     * @returns {Promise<void>} Promesa de la planta añadida.
     * @throws {Error} Error al añadir la planta.
     */
    addPlant = async (uid, plantId) => {
        try {
            const snapshot = await this.db.collection(this.collection).doc(uid).get();
            if (!snapshot.exists) {
                throw new Error('Usuario no encontrado');
            }
            const plantIds = snapshot.data().plants;
            if (plantIds.includes(plantId)) {
                throw new Error('La planta ya está en la lista del usuario');
            }
            plantIds.push(plantId);
            await this.db.collection(this.collection).doc(uid).update({plants: plantIds});
        } catch (error) {
            throw new Error(`Error al añadir la planta: ${error.message}`);
        }
    }

    /**
     * Elimina una planta de un usuario.
     * @param {string} uid - Identificador único del usuario.
     * @param {string} plantId - Identificador único de la planta.
     * @returns {Promise<void>} Promesa de la planta eliminada.
     * @throws {Error} Error al eliminar la planta.
     */
    removePlant = async (uid, plantId) => {
        try {
            const snapshot = await this.db.collection(this.collection).doc(uid).get();
            if (!snapshot.exists) {
                throw new Error('Usuario no encontrado');
            }
            const plantIds = snapshot.data().plants;
            const index = plantIds.indexOf(plantId);
            if (index === -1) {
                throw new Error('La planta no está en la lista del usuario');
            }
            await this.db.collection(this.collection).doc(uid).update({
                plants: admin.firestore.FieldValue.arrayRemove(plantId)
            });
        } catch (error) {
            throw new Error(`Error al eliminar la planta: ${error.message}`);
        }
    }
}

module.exports = UserService;