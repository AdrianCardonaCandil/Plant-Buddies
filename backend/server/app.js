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


// Rutas de la aplicación
app.use('/api/auth', require('./routes/auth')(require('./models/User.model')))
app.use('/api/plants', require('./routes/plants')(require('./models/Plant.model')))
app.use('/api/model', require('./routes/model')(require('./models/Plant.model')))
app.use('/api/users', require('./routes/users')(require('./models/User.model')))

// Inicialización del servidor
const PORT = process.env.PORT
const DOMAIN = process.env.DOMAIN
app.listen(PORT, '0.0.0.0', () => console.log(`Server running on http://${DOMAIN}:${PORT}`))
