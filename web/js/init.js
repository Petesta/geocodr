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

    Geocodr.initGraph();
  });
}

// Load and slide in user page in from the right
Geocodr.showUserPage = function(username) {
  // TODO: fix params
  $('.users-page-container').load('/users?username=' + username, function() {
    Geocodr.drawLangPiechart('.chart-you');
    Geocodr.drawLangPiechart('.chart-them');
    $('.user-photo.you').css('background', "url('https://0.gravatar.com/avatar/eedc3687a5e76c282e43508e29cd67b7?d=https%3A%2F%2Fidenticons.github.com%2F2cd91248fe0d57b51dc83ffbe5782325.png&s=440')");
    $('.user-photo.them').css('background', "url('http://i.imgur.com/HdeUiJP.jpg')");
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
