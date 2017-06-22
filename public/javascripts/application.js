$(function() {

  // Show/hide content
  var showHideContent = new GOVUK.ShowHideContent();
  showHideContent.init();

  /**
   * Navigation items
   */
  $('.nav-item').click(function(e) {

    e.preventDefault();

    var target = $(this).data('target');
    $('#jumpNumber').val(target);
    $('#reportForm').submit();
  });

});