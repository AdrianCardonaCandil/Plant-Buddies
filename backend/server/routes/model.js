/**
 * Rutas para la inferencia del modelo de aprendizaje.
 * @module routes/model
 */

const express = require('express');
const router = express.Router();
const multer = require('multer');
const { spawn } = require('child_process');
const fs = require('fs');
const path = require('path');
const upload = multer({ dest: 'uploads/' });
const PlantConversion = require('../schemes/PlantConversion.scheme');

module.exports = (PlantModel) => {

    /**
     * Realiza la inferencia de la imagen de una planta
     * obtenida desde el cliente. Devuelve el objeto de
     * la planta que se ha inferido desde la base de datos.
     * @name api/model
     */

    const plantModel = new PlantModel();
    const modelDir = path.join(__dirname, '../../model/model.py');

    router.post('/', upload.single('image'), async (req, res) => {
        try {
            // Comprobamos la existencia de la imagen y recogemos la ruta
            if (!req.file) {
                console.log('No se ha recibido ninguna imagen.')
                return res.status(400).json({
                    message: 'No se ha recibido ninguna imagen.'
                })
            }
            const image = path.join(__dirname, '..', req.file.path)
            console.log('Ruta de la imagen:', image)

            // Lanzamos el script de inferencia, monitorizando la salida
            const python = spawn('python3', [modelDir, image])
            let data = ''
            python.stdout.on('data', (chunk) => {
                data += chunk.toString()
            })

            // Al finalizar, eliminamos la imagen y devolvemos la planta inferida
            python.on('close', async (code) => {
                fs.unlinkSync(image)
                if (code !== 0) {
                    console.error('Error al inferir la planta.')
                    return res.status(500).json({
                        message: 'Error al inferir la planta.'
                    })
                }
                
                const plant = await plantModel.getPlant(PlantConversion[JSON.parse(data).class])
                if (plant) {
                    console.log('Planta inferida correctamente.')
                    return res.status(200).json({
                        message: 'Planta inferida correctamente.',
                        plant: plant
                    })
                } else {
                    console.error('Planta no encontrada en la base de datos.')
                    return res.status(404).json({
                        message: 'Planta no encontrada en la base de datos.'
                    })
                }
            })
        } catch (error) {
            console.error('Error al inferir la planta.')
            return res.status(500).json({
                message: 'Error al inferir la planta.'
            })
        }
    })
}