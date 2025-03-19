const User = require("../schemes/User.scheme");
const {db} = require("../config/firebase");
const { DocumentSnapshot } = require("firebase-admin/firestore");

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
}

module.exports = UserService;