exports = module.exports;
var http = require("http");

exports.isUserLoggedIn = function (){
  // checks if the user is logged in
  return false;
}

exports.sendPostRequest = function (data, onSuccess, onError){
  var options = {
    hostname: '127.0.0.1', // load balancer
    port: 80,
    path: '/java',
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    }
  };
  var req = http.request(options, function(res) {
    res.setEncoding('utf8');
    res.on('data', function (body) {
      onSuccess(body);
    });
  });
  
  req.on('error', function(e) {
    onError('problem with request: ' + e.message);
  });
  // write data to request body
  req.write(data);
  req.end();
}
