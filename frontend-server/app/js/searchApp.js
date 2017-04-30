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
      result +=
      "<div class=\"row\">"+
        "<div class=\"col-lg-12\">"+
            "<p>"+ user +"</p>"+
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


function onError(error){
  // no idea what to do
    console.log(error);
}

$('#search').submit(function (e){
  e.preventDefault();
  // let session_id = helpers.getSessionId();
  let searchQuery = $('#searchData').val();
  // clear it
  $('#searchData').val('');

  let req = {
          url: '/search',
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
