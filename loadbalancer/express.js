var express = require('express')
var app = express()
var timeInMs = Date.now();

app.get('/', function (req, res) {
    res.setHeader("Expires", new Date(Date.now() + 2592000000).toUTCString());
    console.log(res);

    res.send('HELLO');
    console.log("Received on 3000")
});

app.listen(3000, function () {
    console.log('Example app listening on port 3000!')
});
