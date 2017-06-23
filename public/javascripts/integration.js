$(function() {

    window.addEventListener('message', function(e) {
      $('#message').val(e.data);
      $('form').submit();
    });

});
