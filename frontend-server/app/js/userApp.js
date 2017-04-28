$('#login').submit(function (e){
  e.preventDefault();
  let email = $('#inputEmail').val();
  let pass =  $('#inputPassword').val();
  let req = {
        url: '/login',
        type: 'POST',
        data: {
          email: email,
          password: pass
        },
        success: function (response) {
          redirect(response);
        }
      };
  $.ajax(req);
});

$('#signup').submit(function (e){
  e.preventDefault();
  let email = $('#signupInputEmail1').val();
  let pass =  $('#signupInputPassword1').val();
  let repass = $('#signupInputRePassword1').val();
  let firstName = $('#signupInputFirstName1').val();
  let lastName = $('#signupInputFirstName1').val();
  let dateOfBirth = $('#signupInputDateOfBirth1').val();
  
  if(pass === repass){
   let req = {
           url: '/register',
           type: 'POST',
           data: {
             email: email,
             password: pass,
             firstName: firstName,
             lastName: lastName,
             dateOfBirth: dateOfBirth
           },
           success: function (response) {
             redirect(response);
           }
         };
     $.ajax(req);
  }
})

function redirect(data){
  if (typeof data.redirect == 'string'){
    window.location = data.redirect
  }
}
