/**
 * Archivo principal del servidor backend for front-end.
 * Configuración de express y rutas de la aplicación.
 */

// Variables de entorno
require('dotenv').config();

// Dependencias
const express = require('express')
const app = express()

// Initializing firebase
require('./config/firebase')

// Middlewares
const parser = express.json()
require('./middlewares/utils')(app, parser)
//require('./middlewares/secureRedirect')(app)
require('./middlewares/static')(app)
require('./middlewares/cors')(app)


// Rutas de autenticación
app.use('/api/auth', require('./routes/auth')(require('./models/User.model')))

// Inicislización del servidor
const PORT = process.env.PORT
app.listen(PORT, () => console.log(`Servidor iniciado en http://localhost:${PORT}`))