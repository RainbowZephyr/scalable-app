function onError(result){
  console.log(result);
}

// renders result to html
function renderResults(results){
  content = $('.main-content'); // clear div
  content.empty();
  var result =
  "<h3>Search Results<br><small>Users</small></h3>";
  results.forEach(
    function(user) {
      console.log(user);
      result +=
      "<div class=\"row\">"+
        "<div class=\"col-lg-12\">"+
          "<form id='form' method='post'>"+
            "<input type='hidden' name='userId' value="+user.key+" />"+
            "<input class=\"btn btn-default\" type=\"submit\" value="+user.value+">"+
          "</form>"+
         "</div>"+
      "</div>"});
  if(results.length == 0){
    result +=
    "<div class=\"row\">"+
      "<div class=\"col-lg-12\">"+
          "<p> User couldn't be found! </p>"+
       "</div>"+
    "</div>"
  }
  content.append(result);
}

function renderUser(user){
  content = $('.main-content'); // clear div
  content.empty();
  content.append(
    "<h3>Search Results<br><small>Users</small></h3>"+
    "<div class=\"item\">"+
       "<div class=\"p-lg\">"+
          "<div class=\"text-center\">"+
             "<p>"+
                "<img src=\"img/user/02.jpg\" alt=\"Image\" class=\"img-circle img-thumbnail thumb64\">"+
             "</p>"+
             "<p>"+
                "<h5> First name: "+ user.firstName +"</h5>"+
                "<h5> Last name: "+ user.lastName +"</h5>"+
                "<h5> Date Of Birth: "+ user.dateOfBirth +"</h5>"+
                "<h5> Email: "+ user.email +"</h5>"+
             "</p>"+
          "</div>"+
       "</div>"+
    "</div>"
  );
}


function onError(error){
  // no idea what to do
    console.log(error);
}

$(document.body).on('submit', '#form', function(e){
  e.preventDefault();
  let userId = $(this).serializeArray()[0].value;
  let req = {
          url: '/user/get',
          type: 'POST',
          data: {
            userId: userId
          },
          success: function (response) {
            renderUser(response);
          },
          error: onError
        };
    $.ajax(req);
});

$('#search').submit(function (e){
  e.preventDefault();
  // let session_id = helpers.getSessionId();
  let searchQuery = $('#searchData').val();
  // clear it
  $('#searchData').val('');

  let req = {
          url: '/search/user',
          type: 'POST',
          data: {
            query: searchQuery
          },
          success: function (response) {
            response = JSON.parse(response);
              renderResults(response.response_body.results);
          },
          error: onError
        };
    $.ajax(req);
});
