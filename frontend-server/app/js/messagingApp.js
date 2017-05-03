$('#SearchForThreads').submit(function (e){
  e.preventDefault();
  let nameQuery = $('#inputNameQuery').val();
  let req = {
        url: '/SearchForThreads',
        type: 'POST',
        data: {
          nameQuery: nameQuery
        },
        success: function (response) {
          redirect(response);
        }
      };
  $.ajax(req);
});

$('#createMessageThread').submit(function (e){
  e.preventDefault();
  let messageThreadName = $('#inputNameQuery').val();
  let userId = $('#userId').val();
  let req = {
        url: '/createMessageThread',
        type: 'POST',
        data: {
          'messageThreadName': messageThreadName,
	  'userId': userId
        },
        success: function (response) {
          redirect(response);
        }
      };
  $.ajax(req);
});

$('#sendMessage').submit(function (e){
  e.preventDefault();
  let messageThreadId = $('#messageThreadId').val();
  let userId = $('#userId').val();
  let message = $('#newMessage').val()
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
});
