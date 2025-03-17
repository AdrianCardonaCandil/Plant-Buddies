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
     * @function createUser
     * @param {Object} user - Objeto de usuario a crear.
     * @returns {Promise<Void>} Promesa de usuario creado.
     * @throws {Error} Error al crear el usuario.
     */
    createUser = (user) => this.service.createUser(user);

    /**
     * Inicia sesión de un usuario en la aplicación.
     * @function loginUser
     * @param {Object} user - Objeto de usuario a autenticar.
     * @returns {Object} Objeto de usuario autenticado
     * @throws {Error} Error al autenticar el usuario.
     */
    loginUser = (user) => this.service.loginUser(user);

    /**
     * Obtiene un usuario de la base de datos.
     * @function getUser
     * @param {string} id - Identificador único del usuario.
     * @returns {Promise<DocumentSnapshot>} Promesa de usuario encontrado.
     * @throws {Error} Error al obtener el usuario.
     */
    getUser = (id) => this.service.getUser(id);
}

module.exports = UserModel;