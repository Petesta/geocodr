var slideTime = 750; // ms

Geocodr.hideLoginPage = function() {
  var $container = $('.login-page-container');
  $container.animate({
    'top': '-100%'
  }, slideTime, function() {
    $container.remove();
  });
}

Geocodr.showGraphPage = function(username) {
  var $container = $('.graph-page-container');
  $container.load('/graph?username=' + username, function() {
    $('body').animate({ backgroundColor: '#f4f4f4' });
    $container.animate({
      'top': '50px',
    }, slideTime);

    Geocodr.initGraph(username);
  });
}

// Load and slide in user page in from the right
Geocodr.showUserPage = function(options) {
  // TODO: fix params
  $('.users-page-container').load('/users?username=' + options.username, function() {
    Geocodr.drawLangPiechart('.chart-you');
    Geocodr.drawLangPiechart('.chart-them');

    $('.user-photo.you').css('background', "url('"+options.selfphoto+"')");
    $('.user-photo.them').css('background', "url('"+options.otherphoto+"')");

    Geocodr.showUserDrawer();
  });
}



$(function() {
  // TODO: Why isn't HTML autofocus working? All of my hate.
  $('#name').focus();

  $('.graph-page-container').css({
    left: ($(window).outerWidth() - $('.graph-page-container').outerWidth()) / 2
  })

  $('.username-form').submit(function(e) {
    var $field   = $(this).find("#name"),
        username = $field.val();

    if (username === '') {
      $field.addClass('field-error')
    } else {
      Geocodr.hideLoginPage()
      Geocodr.showGraphPage();
    }

    e.preventDefault();
    return false;
  });
});
