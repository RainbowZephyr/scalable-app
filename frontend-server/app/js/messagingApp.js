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

            console.log(response);
          redirect(response);
        }
      };
  $.ajax(req);
});
