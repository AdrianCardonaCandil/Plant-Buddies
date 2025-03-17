/**
 * Clase que define el esquema de usuario.
 * @typedef {Class} User
 * @property {string} uid - Identificador único del usuario.
 * @property {string} email - Correo electrónico del usuario.
 * @property {string} name - Nombre del usuario.
 * @property {string} password - Contraseña del usuario.
 * @property {string} country - País del usuario.
 * @property {string} description - Descripción del usuario.
 * @property {image} image - Imagen de perfil del usuario.
 * @property {Array<Plants>} plants - Plantas del usuario.
 */

class User {
    constructor({uid, email, name, password, country, description, image, plants}){
        this.uid = uid;
        this.email = email;
        this.name = name;
        this.password = password;
        this.country = country;
        this.description = description;
        this.image = image;
        this.plants = plants;
    }
    static parse = user => new User(user)
    static stringify = user => JSON.stringify(user)
}

module.exports = User;


