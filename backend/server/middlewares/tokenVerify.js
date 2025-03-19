/**
 * Middleware para verificar el token Firebase enviado desde el frontend..
 */

const { auth } = require('../config/firebase')

/**
 * Verifica el token Firebase enviado desde el frontend.
 * @function tokenVerify
 * @param {Object} req - Objeto de solicitud.
 * @param {Object} res - Objeto de respuesta.
 * @param {Function} next - Función que cede el control al siguiente middleware.
 * @returns {void}
*/

const tokenVerify = async (req, res, next) => {
    // Obtenemos y verificamos la cabecera de autorización.
    const authHeader = req.headers.authorization;
    if (!authHeader || !authHeader.startsWith('Bearer ')) {
        return res.status(401).json({error: 'Token no proporcionado o formato incorrecto'})
    }

    // Extraemos el token de la cabecera.
    const token = authHeader.split(' ')[1]

    // Verificamos el token y extraemos la información del usuario.
    try {
        const decodeToken = await auth.verifyIdToken(token)
        req.user = {
            uid: decodeToken.uid,
            email: decodeToken.email,
            name: decodeToken.name || decodeToken.email
        }

        // Cede el control al siguiente middleware.
        next();
    } catch (error) {
        console.error('Error al verificar el token:', error);
        return res.status(401).json({error: 'Token no válido o expirado'})
    }
}

module.exports = tokenVerify