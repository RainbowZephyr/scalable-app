
function sendPostRequest(url, data, success, error) {
  $.post(url, {
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
