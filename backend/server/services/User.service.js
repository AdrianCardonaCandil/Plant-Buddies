const User = require("../schemes/User.scheme");
const {db, auth} = require("../config/firebase");
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
        this.tokenDuration = process.env.TOKEN_DURATION;
    }

    /**
     * Registra a un usuario en los servicios de firebase de la aplicación.
     * Si el registro es exitoso, crea el usuario en la base de datos y
     * devuelve un token de usuario. En caso contrario, lanza un error.
     * @param {{name, email, password}} params - Objeto con los datos del usuario a registrar.
     * @returns {Promise<string>} Promesa de token de usuario.
     * @throws {Error} Error al registrar el usuario.
     */
    createUser = async (params) => {
        try {
            const {name, email, password} = params
            const userRecord = await auth.createUser({
                email: email,
                password: password,
                displayName: name
            })
            const user = User.parse({
                uid: userRecord.uid,
                email: userRecord.email,
                name: userRecord.displayName,
                password: password,
            })
            await this.db.collection(this.collection).doc(user.uid).set({...user})
            return await auth.createCustomToken(user.uid, {expiresIn: this.tokenDuration})
        } catch (error) {
            throw new Error('Error al registrar el usuario', {cause: error})
        }
    }

    /**
     * Inicia sesión de un usuario en la aplicación.
     * Si el inicio de sesión es exitoso, devuelve un token de usuario. En caso
     * contrario, lanza un error.
     * @param {{email, password}} params - Objeto con los datos del usuario a autenticar.
     * @returns {Promise<string>} Promesa de token de usuario.
     * @throws {Error} Error al autenticar el usuario.
     */
    loginUser = async(params) => {
        try {
            const {email, password} = params
            const userRecord = await auth.getUserByEmail(email)
            const user = await this.getUser(userRecord.uid)
            if (user.password !== password) {
                throw new Error('Contraseña incorrecta')
            }
            return await auth.createCustomToken(userRecord.uid, {expiresIn: this.tokenDuration})
        } catch (error) {
            throw new Error('Error al autenticar el usuario', {cause: error})
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
            const snapshot = await this.db.collection(this.collection).doc(uid).get()
            if (!snapshot.exists) {
                throw new Error('Usuario no encontrado')
            }
            return User.parse(snapshot.data())
        } catch (error) {
            throw new Error('Error al buscar el usuario', {cause: error})
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