$('#SearchForThreads').keyup(function (e){
  if(e.keyCode == 13){
  e.preventDefault();
    let nameQuery = $('#SearchForThreads').val();
    let req = {
          url: '/SearchForThreads',
          type: 'POST',
          data: {
            nameQuery: nameQuery
          },
          success: function (response) {
            console.log(response);
            var threadNames = '';
            for(var i = 0; i < response.ThreadNames.length; i++){
                var threadName = response.ThreadNames[i].threadName;
                threadNames += '<a href="#" class="list-group-item">'
                                                                            +'<div class="media">'
                                                                               +'<div class="pull-left">'
                                                                                  +'<img src="img/group.png" alt="Image" class="media-object img-circle thumb48">'
                                                                               +'</div>'
                                                                               +'<div class="media-body clearfix">'
                                                                                  +'<small class="pull-right">2h</small>'
                                                                                  +'<strong class="media-heading text-primary">'
                                                                                     +'<span class="point point-success point-md"></span>' + threadName + '</strong>'
                                                                                  +'<p class="mb-sm">'
                                                                                     +'<small>Last message</small>'
                                                                                  +'</p>'
                                                                               +'</div>'
                                                                            +'</div>'
                                                                         +'</a>';
            }
            document.getElementById("SearchForThreadsResult").innerHTML = threadNames;

            redirect(response);
          }
        };
    $.ajax(req);
  }
});

$('.messageContact').click(function(e){
  let contactId = $(".messageContactName",this).data("concatUserId");
  let contactName = $(".messageContactName",this).text();
  let userId = $(".messageApp").data("userId");
  console.log(userId);
  console.log(contactId);
  console.log(contactName);
  createMessageThread(contactName, userId,function(response){
    console.log(response);
    let threadId = response.threadId;
    //TODO add user with id = contactId to this thread
    $('#messageModal').data("messageThreadId",threadId);
    $('#messageModal').find(".modal-body").text('"'+contactName+'" message thread has been created with id = "'+threadId+'"');
    $('#messageModal').modal('show');
    redirect(response);
  });

})

var createMessageThread = function(messageThreadName, userId,callback){
    let req = {
        url: '/createMessageThread',
        type: 'POST',
        data: {
          'messageThreadName': messageThreadName,
    'userId': userId
        },
        success: function (response) {
          callback(response);
        }
      };
  $.ajax(req);
}


$('#sendMessageButton').click(function (e){
  e.preventDefault();
  let messageThreadId = $('#messageModal').data("messageThreadId");
  let userId = $(".messageApp").data("userId");
  let message = $('#sendMessageContent').val();
  console.log(messageThreadId);
  console.log(userId);
  console.log(message);
  sendMessage(messageThreadId, userId, message, function(response){
    console.log(response);
    $('#messageModal').find(".modal-body").append('<br />"'+message+'" message is sent successfully');
    $('#sendMessageContent').val("");
  });
  
});

var sendMessage = function(messageThreadId, userId, message, callback){
  let req = {
        url: '/sendMessage',
        type: 'POST',
        data: {
          'messageThreadId': messageThreadId,
    'userId': userId,
    'messageBody': message
        },
        success: function (response) {
          callback(response);
          redirect(response);
        }
      };
  $.ajax(req);
}


$('#showUsersButton').click(function (e){
  e.preventDefault();
  let messageThreadId = $('#messageModal').data("messageThreadId");
  getAllusersInThread(messageThreadId, function(response){
    console.log(response);
  });
  
});


var getAllusersInThread = function(messageThreadId, callback){
   let req = {
        url: '/getUsersInThread',
        type: 'POST',
        data: {
          'messageThreadId': messageThreadId
        },
        success: function (response) {
          redirect(response);
        }
      };
  $.ajax(req);
}

/*
$('#getUsersInThread').submit(function (e){
  e.preventDefault();
  let messageThreadId = $('#messageThreadId').val();
  let req = {
        url: '/getUsersInThread',
        type: 'POST',
        data: {
          'messageThreadId': messageThreadId
        },
        success: function (response) {
          redirect(response);
        }
      };
  $.ajax(req);
});*/


$('#leaveButton').click(function (e){
  e.preventDefault();
  let messageThreadId = $('#messageModal').data("messageThreadId");
  let userId = $(".messageApp").data("userId");
  removeFromThread(messageThreadId, userId, function(response){
    console.log(response);
    $('#messageModal').find(".modal-body").append('<br />Left successfully');
  });
  
});


var removeFromThread = function(messageThreadId, userId, callback){
   let req = {
        url: '/removeUserFromThread',
        type: 'POST',
        data: {
          'messageThreadId': messageThreadId,
          'userId' : userId
        },
        success: function (response) {
          redirect(response);
        }
      };
  $.ajax(req);
}
/*
$('#removeUserFromThread').submit(function (e){
  e.preventDefault();
  let messageThreadId = $('#messageThreadId').val();
  let userId = $('#userId').val();
  let req = {
        url: '/removeUserFromThread',
        type: 'POST',
        data: {
          'messageThreadId': messageThreadId,
          'userId' : userId
        },
        success: function (response) {
          redirect(response);
        }
      };
  $.ajax(req);
});*/
