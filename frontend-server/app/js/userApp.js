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
  // should handle it as above
})

function redirect(data){
  if (typeof data.redirect == 'string'){
    window.location = data.redirect
  }
}
