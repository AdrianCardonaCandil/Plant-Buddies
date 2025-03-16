/**
 * Configuración e inicialización del SDK de Firebase Admin.
*/

const {initializeApp, cert, getApps} = require('firebase-admin/app');
const {getFirestore} = require('firebase-admin/firestore');
const {getAuth} = require('firebase-admin/auth');

try {
    const serviceAccount = require(process.env.FIREBASE_SERVICE_ACCOUNT_KEY_PATH);
    console.log("hello")
    if (getApps().length === 0){
        initializeApp({
            credential: cert(serviceAccount)
        });
    }
    console.log('Firebase Admin SDK inicializado correctamente');
    module.exports = {
        db: getFirestore(),
        auth: getAuth()
    };
} catch (error) {
    console.error('Error al inicializar Firebase Admin SDK:', error);
    process.exit(1);
}