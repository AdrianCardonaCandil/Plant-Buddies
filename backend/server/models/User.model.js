const { DocumentSnapshot } = require('firebase-admin/firestore');
const UserService = require('../services/User.service');

/**
 * Modelo para gestionar operaciones realizadas sobre objetos de usuario.
 * @class UserModel
 * @property {UserService} service - Servicio que maneja las operaciones de usuario.
 * @exports UserModel
 * @version 1.0
 */

class UserModel {
    constructor() {
        this.service = new UserService();
    }

    /**
     * Registra a un usuario en los servicios de firebase de la aplicación.
     * Si el registro es exitoso, devuelve un token de usuario. En caso
     * contrario, lanza un error.
     * @param {{name, email, password}} params - Objeto con los datos del usuario a registrar.
     * @returns {Promise<string>} Promesa de token de usuario.
     * @throws {Error} Error al registrar el usuario.
     */
    createUser = (params) => this.service.createUser(params);

    /**
     * Inicia sesión de un usuario en la aplicación.
     * Si el inicio de sesión es exitoso, devuelve un token de usuario. En caso
     * contrario, lanza un error.
     * @param {{email, password}} params - Objeto con los datos del usuario a autenticar.
     * @returns {Promise<string>} Promesa de token de usuario.
     * @throws {Error} Error al autenticar el usuario.
     */
    loginUser = (params) => this.service.loginUser(params);

    /**
     * Obtiene un usuario de la base de datos.
     * @param {string} uid - Identificador único del usuario.
     * @returns {Promise<User>} Promesa de usuario encontrado.
     * @throws {Error} Error al obtener el usuario.
     */
    getUser = (id) => this.service.getUser(id);

    /**
     * Obtiene las plantas de un usuario.
     * @param {string} uid - Identificador único del usuario.
     * @returns {Promise<Plant[]>} Promesa de las plantas encontradas.
     * @throws {Error} Error al obtener las plantas.
     */
    getPlants = (uid) => this.service.getPlants(uid);

    /**
     * Añade una planta a un usuario.
     * @param {string} uid - Identificador único del usuario.
     * @param {string} plantId - Identificador único de la planta.
     * @returns {Promise<void>} Promesa de la planta añadida.
     * @throws {Error} Error al añadir la planta.
     */
    addPlant = (uid, plantId) => this.service.addPlant(uid, plantId);

    /**
     * Elimina una planta de un usuario.
     * @param {string} uid - Identificador único del usuario.
     * @param {string} plantId - Identificador único de la planta.
     * @returns {Promise<void>} Promesa de la planta eliminada.
     * @throws {Error} Error al eliminar la planta.
     */
    removePlant = (uid, plantId) => this.service.removePlant(uid, plantId);

    /**
     * Añade una planta a la lista de plantas favoritas de un usuario.
     * @param {string} uid - Identificador único del usuario.
     * @param {string} plantId - Identificador único de la planta.
     * @returns {Promise<void>} Promesa de la planta añadida.
     * @throws {Error} Error al añadir la planta.
     */
    addFavorite = (uid, plantId) => this.service.addFavorite(uid, plantId);

    /**
     * Elimina una planta de la lista de plantas favoritas de un usuario.
     * @param {string} uid - Identificador único del usuario.
     * @param {string} plantId - Identificador único de la planta.
     * @returns {Promise<void>} Promesa de la planta eliminada.
     * @throws {Error} Error al eliminar la planta.
     */
    removeFavorite = (uid, plantId) => this.service.removeFavorite(uid, plantId);

    /**
     * Obtiene las plantas favoritas de un usuario.
     * @param {string} uid - Identificador único del usuario.
     * @returns {Promise<Plant[]>} Promesa de las plantas favoritas.
     * @throws {Error} Error al obtener las plantas favoritas.
     */
    getFavorites = (uid) => this.service.getFavorites(uid);
}

module.exports = UserModel;