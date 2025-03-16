const express = require('express')

module.exports = app => app.use(express.static(require('path').join(__dirname, '..', 'public')))