/**
 * Clase que define el esquema de planta.
 * @typedef {Class} Plant
 * @property {string} id - Identificador único de la planta.
 * @property {string} scientificName - Nombre científico de la planta.
 * @property {string} commonName - Nombre común de la planta.
 * @property {string} description - Descripción de la planta.
 * @property {float} waterNeeds - Necesidades de agua de la planta.
 * @property {float} sunlightNeeds - Necesidades de luz de la planta.
 * @property {string} careLevel - Nivel de cuidado de la planta.
 * @property {Array<string>} careTips - Consejos de cuidado de la planta.
 * @property {string} imageUrl - Imagen de la planta.
 */

class Plant {
    constructor(id, scientificName, commonName, description, waterNeeds, sunlightNeeds, careLevel, careTips, imageUrl){
        this.id = id;
        this.scientificName = scientificName;
        this.commonName = commonName;
        this.description = description;
        this.waterNeeds = waterNeeds;
        this.sunlightNeeds = sunlightNeeds;
        this.careLevel = careLevel;
        this.careTips = careTips;
        this.imageUrl = imageUrl;
    }
    static parse = plant => new Plant(plant)
    static stringify = plant => JSON.stringify(plant)
}

module.exports = Plant;