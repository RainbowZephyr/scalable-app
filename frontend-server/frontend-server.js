var express = require('express')
var app = express()
var path = require('path');

app.get('/', function (req, res) {
   res.sendFile(path.join(__dirname + '/pages/index.html'));
})


app.use(express.static(__dirname + '/app'));
app.use(express.static(__dirname + '/pages'));
app.use(express.static(__dirname + '/vendor'));

app.use(function(req, res, next) {
  res.header("Access-Control-Allow-Origin", "*");
    res.header('Access-Control-Allow-Methods', 'GET,PUT,POST,DELETE');
    res.header('Access-Control-Allow-Headers', 'Content-Type');
  next();
});

app.listen(3000, function () {
  console.log('FrontEnd app listening on port 3000!')
})
