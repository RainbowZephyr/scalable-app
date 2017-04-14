var express = require('express')
var app = express()
var timeInMs = Date.now();

app.get('/', function (req, res) {
    res.send('Date now in Ms:'+Date.now())
    console.log("Received on 3000")
})

app.listen(3000, function () {
    console.log('Example app listening on port 3000!')
})
