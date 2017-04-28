function onSuccess(result){
  // display users
  console.log(result);
  // send a request to the user app to get users names (result is an array of ids)

  // on the callback of that show results
  renderResults(result.response_body.results);
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
  console.log(result);
  content.append(result);
}


function onError(error){
  // no idea what to do
    console.log(error);
}

$('#search').submit(function (e){
  e.preventDefault();
  // let session_id = helpers.getSessionId();
  let session_id = 1;
  let searchQuery = $('#searchData').val();
  // clear it
  $('#searchData').val('');

  let data = JSON.stringify(
    {
    "session_id": "",
    "receiving_app_id": "search",
    "service_type": "search_by_name",
    "request_parameters":
      {
        "user_name": searchQuery
      }
    }
  );
  sendPostRequest('http://127.0.0.1/java', data, onSuccess, onError);
});
