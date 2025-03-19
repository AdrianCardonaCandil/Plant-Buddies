/**
 * Middlewares aplicados para diferentes utilidades, entre las que se
 * encuentran el parseo de objetos, logging de información, manejo de
 * formularios, etc.
 */

module.exports = (app, parser) => {
    app.use(require('morgan')('dev'))
    app.use(parser)
    app.use(require('cookie-parser')())
}