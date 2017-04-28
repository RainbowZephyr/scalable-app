var express = require('express')
var app = express()
var path = require('path')
var bodyParser = require('body-parser')
var helpers = require('./helpers')
var cookieParser = require('cookie-parser');
var expressSession = require('express-session');

app.use(bodyParser.urlencoded({ extended: true }))
app.use(bodyParser.json())
app.use(cookieParser("sdd", {signed: true}));
app.use(expressSession({secret:'somesecrettokenhere',
  resave: false,
  saveUninitialized: true}));

app.use(function(req, res, next) {
  res.header("Access-Control-Allow-Origin", "*");
    res.header('Access-Control-Allow-Methods', 'GET,PUT,POST,DELETE');
    res.header('Access-Control-Allow-Headers', 'Content-Type');
  next();
});


app.get('/', function (req, res) {
  // if user not logged in (redirect to login)
  console.log("SESSION",req.session.sessionId , !req.session.sessionId );
  if(!req.session.sessionId){
    res.redirect('/login');
  }else{
    res.sendFile(path.join(__dirname + '/pages/index.html'));
  }
})

app.get('/login', function (req, res) {
  console.log('sessionId',req.session.sessionId);
  // if user has a valid session id then redirect to home
  if(req.session.sessionId){
    res.redirect('/');
  }else{
    res.sendFile(path.join(__dirname + '/pages/login-multi.html'));
  }
})

app.post('/login', function(req, res){
  // login user
  let data = JSON.stringify(
    {
    "app_id": "",
    "receiving_app_id": "user",
    "service_type": "loginUser",
    "request_parameters": {
      "email": req.body.email,
      "password": req.body.password
      }
    }
  );
  helpers.sendPostRequest(data,
    function(result){
      let jsonObject = JSON.parse(result);
      if(jsonObject.response_status == 404){
        res.send({redirect: '/login'}); // send errors lw 7abeb (user not found)
      }else{
        console.log(jsonObject);
        req.session.sessionId = jsonObject.sessionId;
        res.send({redirect: '/'});
      }
  });
})

app.get('/register', function (req, res) {
  // if user is logged in then redirect home
  if(req.session.sessionId){
    res.redirect('/');
  }else{
    res.sendFile(path.join(__dirname + '/pages/signup.html'));
  }
})

app.post('/register', function (req, res) {
  // if user is logged in then redirect home
  if(req.session.sessionId){
    res.redirect('/');
  }else{
    let data = JSON.stringify(
      {
        "app_id": "",
        "receiving_app_id": "user",
        "service_type": "signupUser",
        "request_parameters": {
          "email": req.body.email,
          "password": req.body.password,
          "firstName": req.body.firstName,
          "lastName": req.body.lastName,
          "dateOfBirth": req.body.date,
          "createdAt": Date.now()
        }
      });

      helpers.sendPostRequest(data,
        function(result){
          let jsonObject = JSON.parse(result);
          if(jsonObject.response_status == 404){
            res.send({redirect: '/register'}); // send errors lw 7abeb (user not found)
          }else{
            res.send({redirect: '/login'}); // redirect to login
          }
      });
  }
})

app.post('/search', function(req, res){

})

app.get('/logout', function (req, res){
  req.session.destroy(function(err){
    if(err) {
    console.log(err);
    } else {
    res.redirect('/');
    }
  })
})
app.use(express.static(__dirname + '/app'))
app.use(express.static(__dirname + '/pages'))
app.use(express.static(__dirname + '/vendor'))
app.listen(3000, function () {
  console.log('FrontEnd app listening on port 3000!')
})
