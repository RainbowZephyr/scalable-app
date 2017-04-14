var express = require('express')
var app = express()

app.get('/', function (req, res) {
    res.send('Hello World!')
    console.log("Received on 4000")
})

app.listen(4000, function () {
    console.log('Example app listening on port 4000!')
})
