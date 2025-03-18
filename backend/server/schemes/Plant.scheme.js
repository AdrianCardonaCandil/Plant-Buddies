/**
 * Clase que define el esquema de planta.
 * @typedef {Class} Plant
 * @property {string} id - Identificador de la planta.
 * @property {string} scientificName - Nombre científico de la planta.
 * @property {string} commonName - Nombre común de la planta.
 * @property {string} description - Descripción de la planta.
 * @property {string} family - Familia de la planta.
 * @property {string} genus - Género de la planta.
 * @property {string} dimensions - Dimensiones de la planta.
 * @property {string} watering - Necesidades de riego de la planta.
 * @property {string} sunlight - Necesidades de luz de la planta.
 * @property {string} prunningMonth - Meses de poda de la planta.
 * @property {string} seeds - Existencia de semillas.
 * @property {string} growthRate - Tasa de crecimiento de la planta.
 * @property {string} indoor - Planta de interior.
 * @property {string} careLevel - Nivel de cuidado de la planta.
 * @property {string} flowers - Existencia de flores.
 * @property {string} fruits - Existencia de frutos.
 * @property {string} harvestSeason - Temporada de cosecha de la planta.
 * @property {string} leaf - Existencia de hojas.
 * @property {string} poisonous - Planta venenosa.
 * @property {string} careGuides - Guías de cuidado de la planta.
 */

class Plant {
    constructor({id, scientificName, commonName, description, family, genus, dimensions, watering, sunlight, prunningMonth, seeds, growthRate, indoor, careLevel, flowers, fruits, harvestSeason, leaf, poisonous, careGuides}){
        this.id = id;
        this.scientificName = scientificName;
        this.commonName = commonName;
        this.description = description;
        this.family = family;
        this.genus = genus;
        this.dimensions = dimensions;
        this.watering = watering;
        this.sunlight = sunlight;
        this.prunningMonth = prunningMonth;
        this.seeds = seeds;
        this.growthRate = growthRate;
        this.indoor = indoor;
        this.careLevel = careLevel;
        this.flowers = flowers;
        this.fruits = fruits;
        this.harvestSeason = harvestSeason;
        this.leaf = leaf;
        this.poisonous = poisonous;
        this.careGuides = careGuides
    }
    static parse = plant => new Plant(plant)
    static stringify = plant => JSON.stringify(plant)
}

module.exports = Plant;