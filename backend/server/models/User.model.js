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
     * Crea un nuevo usuario en la base de datos.
     * @param {Object} user - Objeto de usuario a crear.
     * @returns {Promise<void>} Promesa de usuario creado.
     * @throws {Error} Error al crear el usuario.
     */
    createUser = (user) => this.service.createUser(user);

    /**
     * Inicia sesión de un usuario en la aplicación.
     * @param {string} uid - Identificador del usuario a autenticar.
     * @returns {Promise<User>} Objeto de usuario autenticado.
     * @throws {Error} Error al autenticar el usuario.
     */
    loginUser = (user) => this.service.loginUser(user);

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
}

module.exports = UserModel;