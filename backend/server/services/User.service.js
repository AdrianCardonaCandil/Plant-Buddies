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
    constructor(){
        this.collection = process.env.USERS_COLLECTION;
        this.db = db
    }

    /**
     * Crea un nuevo usuario en la base de datos.
     * @function createUser
     * @param {Object} user - Objeto de usuario a crear.
     * @returns {Promise<void>} Promesa de usuario creado.
     * @throws {Error} Error al crear el usuario.
     */
    createUser = (user) => {
        try {
            return this.db.collection(this.collection).doc(user.uid).set({...user})
        } catch (error) {
            throw new Error('Error al crear el usuario:', error);
        }
    }

    /**
     * Inicia sesión de un usuario en la aplicación.
     * @function loginUser
     * @param {Object} user - Objeto de usuario a autenticar.
     * @returns {Object} Objeto de usuario autenticado
     * @throws {Error} Error al autenticar el usuario.
     */
    loginUser = (user) => {
        return this.getUser(user.uid)
            .then((doc) => {
                if (doc.exists) {
                    return doc.data()
                } else {
                    throw new Error('Usuario no encontrado');
                }
            }).catch((error) => {
                throw new Error('Error al autenticar el usuario:', error);
            })
    }

    /**
     * Obtiene un usuario de la base de datos.
     * @function getUser
     * @param {string} uid - Identificador único del usuario.
     * @returns {Promise<DocumentSnapshot>} Promesa de usuario encontrado.
     * @throws {Error} Error al obtener el usuario.
     */
    getUser = (uid) => {
        try {
            return this.db.collection(this.collection).doc(uid).get();
        } catch (error) {
            throw new Error('Error al obtener el usuario:', error);
        }
    }
}

module.exports = UserService;