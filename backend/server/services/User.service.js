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

    /**
     * Obtiene las plantas favoritas de un usuario.
     * @param {string} uid - Identificador único del usuario.
     * @returns {Promise<Plant[]>} Promesa de las plantas favoritas.
     * @throws {Error} Error al obtener las plantas favoritas.
     */
    getFavorites = async (uid) => {
        try {
            const snapshot = await this.db.collection(this.collection).doc(uid).get();
            if (!snapshot.exists) {
                throw new Error('Usuario no encontrado');
            }
            const plantIds = snapshot.data().favorites;
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
     * Añade una planta a la lista de plantas favoritas de un usuario.
     * @param {string} uid - Identificador único del usuario.
     * @param {string} plantId - Identificador único de la planta.
     * @returns {Promise<void>} Promesa de la planta añadida.
     * @throws {Error} Error al añadir la planta.
     */
    addFavorite = async (uid, plantId) => {
        try {
            const snapshot = await this.db.collection(this.collection).doc(uid).get();
            if (!snapshot.exists) {
                throw new Error('Usuario no encontrado');
            }
            const plantIds = snapshot.data().favorites;
            if (plantIds.includes(plantId)) {
                throw new Error('La planta ya está en la lista de favoritos del usuario');
            }
            plantIds.push(plantId);
            await this.db.collection(this.collection).doc(uid).update({favorites: plantIds});
        } catch (error) {
            throw new Error(`Error al añadir la planta: ${error.message}`);
        }
    }

    /**
     * Elimina una planta de la lista de plantas favoritas de un usuario.
     * @param {string} uid - Identificador único del usuario.
     * @param {string} plantId - Identificador único de la planta.
     * @returns {Promise<void>} Promesa de la planta eliminada.
     * @throws {Error} Error al eliminar la planta.
     */
    removeFavorite = async (uid, plantId) => {
        try {
            const snapshot = await this.db.collection(this.collection).doc(uid).get();
            if (!snapshot.exists) {
                throw new Error('Usuario no encontrado');
            }
            const plantIds = snapshot.data().favorites;
            const index = plantIds.indexOf(plantId);
            if (index === -1) {
                throw new Error('La planta no está en la lista de favoritos del usuario');
            }
            await this.db.collection(this.collection).doc(uid).update({
                favorites: admin.firestore.FieldValue.arrayRemove(plantId)
            });
        } catch (error) {
            throw new Error(`Error al eliminar la planta: ${error.message}`);
        }
    }

    /**
     * Actualiza el nombre personalizado de una planta del usuario.
     * @param {string} uid - Identificador único del usuario.
     * @param {string} plantId - Identificador único de la planta.
     * @param {string} newName - Nuevo nombre personalizado.
     * @returns {Promise<void>}
     * @throws {Error} Error al editar la planta.
     */
    updateUserPlantName = async (uid, plantId, newName) => {
        try {
            const userDocRef = this.db.collection(this.collection).doc(uid);
            const snapshot = await userDocRef.get();
            if (!snapshot.exists) {
                throw new Error("Usuario no encontrado");
            }
            const customNames = snapshot.data().customPlantNames || {};
            customNames[plantId] = newName;
            await userDocRef.update({
                customPlantNames: customNames
            });
        } catch (error) {
            throw new Error(`Error al actualizar el nombre de la planta: ${error.message}`);
        }
    }

    /**
     * Obtiene las tareas de un usuario.
     * @param {string} uid - Identificador único del usuario.
     * @returns {Promise<Task[]>} Promesa de las tareas.
     * @throws {Error} Error al obtener las tareas.
     */
    async getTasks(uid) {
        try {
            const userRef = await this.db.collection(this.collection).doc(uid);
            const tasksCollection = process.env.TASKS_COLLECTION;
            const tasksSnapshot = await userRef.collection('tasks').get();

            if (tasksSnapshot.empty) {
                console.log('No hay tareas para el usuario');
                return [];
            }

            return tasksSnapshot.docs.map(doc => 
            ({
                id: doc.id,
                dateTime: new Date(doc.data().dateTime._seconds * 1000),
                label: doc.data().label,
                type: doc.data().type,
            })
            )
        } catch (error) {
            throw new Error(`Error al obtener las tareas: ${error.message}`);
        }
    }

    /**
     * Añade una tarea a un usuario.
     * @param {string} uid - Identificador único del usuario.
     * @param {Task} task - Objeto con los datos de la tarea.
     * @returns {Promise<Task>} Promesa de la tarea añadida.
     * @throws {Error} Error al añadir la tarea.
     */
    addTask = async (uid, task) => {
        try {
            const userRef = await this.db.collection(this.collection).doc(uid);
            console.log("hello")
            const tasksCollection = process.env.TASKS_COLLECTION;
            const taskRef = userRef.collection(tasksCollection).doc();
            await taskRef.set({
                label: task.label,
                type: task.type,
                dateTime: admin.firestore.Timestamp.fromDate(new Date(task.dateTime))
            });
            return task;
        } catch (error) {
            throw new Error(`Error al añadir la tarea: ${error.message}`);
        }
    }

    /**
     * Elimina una tarea de un usuario.
     * @param {string} uid - Identificador único del usuario.
     * @param {string} taskId - Identificador único de la tarea.
     * @returns {Promise<void>} Promesa de la tarea eliminada.
     * @throws {Error} Error al eliminar la tarea.
     */
    removeTask = async (uid, taskId) => {
        try {
            const userRef = await this.db.collection(this.collection).doc(uid);
            const tasksCollection = process.env.TASKS_COLLECTION;
            const taskRef = userRef.collection(tasksCollection).doc(taskId);
            await taskRef.delete();
        } catch (error) {
            throw new Error(`Error al eliminar la tarea: ${error.message}`);
        }
    }
}

module.exports = UserService;