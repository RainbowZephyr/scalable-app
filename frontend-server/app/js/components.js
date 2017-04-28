
function sendPostRequest(url, data, success, error) {
  $.ajax({
    async: true,
    crossDomain: true,
    url: url,
    method: "POST",
    dataType: "JSON",
    processData: false,
    data: data,
    success: success,
    error: error
  });
}

// any app could call this but only User app writes the definition here
function getSessionId(){

}

// call to set the user session id on success of a login (User App should handle this)
function setSessionId(sessionId){

}
