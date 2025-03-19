/**
 * Configuración de CORS.
 * Posibilita el acceso a la API únicamente desde el cliente.
 */

const cors = require('cors')
const whitelist = [process.env.CLIENT]

module.exports = app => app.use(cors({
    origin: (origin, cb) => cb(null, whitelist.includes(origin)),
    credentials: true
}))