/**
 * Middleware que redirige peticiones HTTP no seguras a HTTPS.
 * Para ello, se deben cumplir varias condiciones
 */

module.exports = app => {
    app.use((req, res, next) => {
        if (process.env.ENV === 'DEV') next()
        else if (req.secure) next()
        else res.redirect(`https://${req.headers.host + req.url}`)
    })
}