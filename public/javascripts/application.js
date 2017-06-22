$(function() {
    var showHideContent = new GOVUK.ShowHideContent();
    showHideContent.init();

  $('.nav-item').click(function(e) {

    e.preventDefault();

    var target = $(this).data('target');

    console.log('Jumping to page:', target);

    $('#jumpNumber').val(target);
    $('#reportForm').submit();
  });

});