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

  if(!req.session.sessionId){
    res.redirect('/login');
  }else{
    res.sendFile(path.join(__dirname + '/pages/index.html'));
  }
})

app.get('/login', function (req, res) {
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
        req.session.sessionId = jsonObject.sessionId;
        req.session.userId = jsonObject.userId;
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
          "dateOfBirth": req.body.dateOfBirth,
          "createdAt": Date.now()
        }
      });
      helpers.sendPostRequest(data,
        function(result){
          let jsonObject = JSON.parse(result);
          var addUserJson = JSON.stringify(
            {
              "session_id": req.session.sessionId,
              "receiving_app_id": "search",
              "service_type": "add_user",
              "request_parameters": {
                "user_id": jsonObject.id,
                "user_name": req.body.firstName
              }
            });
          if(jsonObject.response_status == 404){
            res.send({redirect: '/register'}); // send errors lw 7abeb (user not found)
          }else{
            // should send a request to add user to graph
            helpers.sendPostRequest(addUserJson,function(result){
              let jsonObject = JSON.parse(result);
              res.send({redirect: '/login'}); // redirect to login
            })
          }
      });
  }
})

app.get('/user/edit_profile', function (req, res) {
  if(!req.session.sessionId){
    res.redirect('/');
  }else{
    // serve some html for edit profile
  }
})

app.post('/user/get', function(req, res){
  // send req to user app
  if(!req.session.sessionId){
    res.redirect('/');
  }else{
    let data = JSON.stringify(
      {
        "session_id": req.session.sessionId,
        "app_id": "",
        "receiving_app_id": "user",
        "service_type": "getUser",
        "request_parameters": {
          "user_id": req.body.userId
        }
      });
      helpers.sendPostRequest(data,
        function(result){
          let jsonObject = JSON.parse(result);
          if(jsonObject.response_status == 404){
            res.send({redirect: '/'}); // send errors lw 7abeb
          }else{
            // should send a request to add user to graph
            res.send(jsonObject);    
          }
      });
  }
})

app.post('/user/edit_profile', function (req, res) {
  // if user is logged in then redirect home
  if(!req.session.sessionId){
    res.redirect('/');
  }else{
    let data = JSON.stringify(
      {
        "session_id": req.session.sessionId,
        "app_id": "",
        "receiving_app_id": "user",
        "service_type": "editProfile",
        "request_parameters": {

        }
      });
      helpers.sendPostRequest(data,
        function(result){
          let jsonObject = JSON.parse(result);
          if(jsonObject.response_status == 404){
            res.send({redirect: '/edit_profile'}); // send errors lw 7abeb
          }else{
            // should send a request to add user to graph
            console.log(jsonObject.id);
            res.send({redirect: '/'}); // redirect to home
          }
      });
  }
})

app.post('/friend/add_request', function (req, res) {
  // if user is logged in then redirect home
  if(!req.session.sessionId){
    res.redirect('/');
  }else{
    let data = JSON.stringify(
      {
        "session_id":req.session.sessionId,
        "app_id": "",
        "receiving_app_id": "user",
        "service_type": "addFriend",
        "request_parameters": {
          "user1ID": req.session.userId,
          "user2ID": req.body.friendUserId
        }
      });
      helpers.sendPostRequest(data,
        function(result){
          if(jsonObject.response_status == 404){
            res.send({redirect: '/'}); // send errors lw 7abeb (req not sent)
          }else{
            // should send a request to add user to graph
            // show message user req sent
          }
      });
  }
})


app.post('/friend/accept_request', function (req, res) {
  // if user is logged in then redirect home
  if(!req.session.sessionId){
    res.redirect('/');
  }else{
    let data = JSON.stringify(
      {
        "session_id":req.session.sessionId,
        "app_id": "",
        "receiving_app_id": "user",
        "service_type": "acceptFriendRequest",
        "request_parameters": {
          "user1ID": req.session.userId,
          "user2ID": req.body.friendUserId
        }
      });
      helpers.sendPostRequest(data,
        function(result){
          let jsonObject = JSON.parse(result);
          if(jsonObject.response_status == 404){
            res.send({redirect: '/register'}); // send errors lw 7abeb (user not found)
          }else{
            // should send a request to add user to graph
            console.log(jsonObject.id);
            res.send({redirect: '/login'}); // redirect to login
          }
      });
  }
})


app.post('/friend/decline_request', function (req, res) {
  // if user is logged in then redirect home
  if(req.session.sessionId){
    res.redirect('/');
  }else{
    let data = JSON.stringify(
      {
        "session_id":req.session.sessionId,
        "app_id": "",
        "receiving_app_id": "user",
        "service_type": "declineFriendRequest",
        "request_parameters": {
          "user1ID": req.session.userId,
          "user2ID": req.body.friendUserId
        }
      });
      helpers.sendPostRequest(data,
        function(result){
          let jsonObject = JSON.parse(result);
          if(jsonObject.response_status == 404){
            res.send({redirect: '/register'}); // send errors lw 7abeb (user not found)
          }else{
            // should send a request to add user to graph
            console.log(jsonObject.id);
            res.send({redirect: '/login'}); // redirect to login
          }
      });
  }
})


app.post('/friend/remove_friend', function (req, res) {
  // if user is logged in then redirect home
  if(req.session.sessionId){
    res.redirect('/');
  }else{
    let data = JSON.stringify(
      {
        "session_id":req.session.sessionId,
        "app_id": "",
        "receiving_app_id": "user",
        "service_type": "removeFriend",
        "request_parameters": {
          "user1ID": req.session.userId,
          "user2ID": req.body.friendUserId
        }
      });
      helpers.sendPostRequest(data,
        function(result){
          let jsonObject = JSON.parse(result);
          if(jsonObject.response_status == 404){
            res.send({redirect: '/register'}); // send errors lw 7abeb (user not found)
          }else{
            // should send a request to add user to graph
            console.log(jsonObject.id);
            res.send({redirect: '/login'}); // redirect to login
          }
      });
  }
})

app.post('/search/user', function(req, res){
  if(!req.session.sessionId){
    res.redirect('/');
  }else{
    let data = JSON.stringify(
      {
      "session_id": req.session.sessionId,
      "receiving_app_id": "search",
      "service_type": "search_by_name",
      "request_parameters":
        {
          "user_name": req.body.query
        }
      }
    );
    helpers.sendPostRequest(data,
      function(result){
        res.send(result);
    });
  }
})

app.post('/search/friendsUpTo', function(req, res){

  let data = JSON.stringify(
    {
    "session_id": req.session.sessionId,
    "receiving_app_id": "search",
    "service_type": "get_friends_up_to",
    "request_parameters":
      {
        "user_id": req.session.sessionId,
        "at": req.body.at
      }
    }
  );
  helpers.sendPostRequest(data,
    function(result){
      res.send(result);
  });
})

app.post('/search/friendsAt', function(req, res){

  let data = JSON.stringify(
    {
    "session_id": req.session.sessionId,
    "receiving_app_id": "search",
    "service_type": "get_friends_at",
    "request_parameters":
      {
        "user_id": req.session.sessionId,
        "at": req.body.at
      }
    }
  );

  helpers.sendPostRequest(data,
    function(result){
      res.send(result);
  });
})

app.post('/logout', function (req, res){
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
