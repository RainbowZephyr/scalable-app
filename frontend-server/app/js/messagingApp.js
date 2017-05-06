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
  let UserId = $(".MessageApp").data("userId");
  console.log(UserId);
  console.log(contactId);
  console.log(contactName);
  createMessageThread(contactName, UserId,function(response){
    console.log(response);
    let threadId = response.threadId;
    //TODO add user with id = contactId to this thread
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


$('#sendMessage').submit(function (e){
  e.preventDefault();
  let messageThreadId = $('#messageThreadId').val();
  let userId = $('#userId').val();
  let message = $('#newMessage').val()
  
});

var sendMessage = function(messageThreadId, userId, message){
  let req = {
        url: '/sendMessage',
        type: 'POST',
        data: {
          'messageThreadId': messageThreadId,
    'userId': userId,
    'messageBody': message
        },
        success: function (response) {
          redirect(response);
        }
      };
  $.ajax(req);
}

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
});

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
});