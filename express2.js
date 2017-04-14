var express = require('express')
var app = express()
var timeInMs = Date.now();

app.get('/', function (req, res) {
    res.send('Date in Ms'+Date.now())
    console.log("Received on 4000")
})

app.listen(4000, function () {
    console.log('Example app listening on port 4000!')
})
