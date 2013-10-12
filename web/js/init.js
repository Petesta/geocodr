var slideTime = 750; // ms

// Slide login page down
Geocodr.showLoginPage = function() {
  $('#name').val('')
  $('.login-page-container').animate({
    'top': '50px'
  }, slideTime, function() { $('#name').focus() });
}

// Slide login page up
Geocodr.hideLoginPage = function() {
  var $container = $('.login-page-container');
  $container.animate({
    'top': '-100%'
  }, slideTime);
}


// Slide graph page up
Geocodr.showGraphPage = function(username) {
  var $container = $('.graph-page-container');
  $container.load('/graph?username=' + username, function() {
    $('body').animate({ backgroundColor: '#f4f4f4' });
    $container.animate({
      'top': '50px',
    }, slideTime);

    $('.back-to-login').click(function() {
      Geocodr.hideGraphPage();
      Geocodr.showLoginPage();
      return false;
    });

    Geocodr.initGraph(username);
  });
}

// Slide graph page down
Geocodr.hideGraphPage = function() {
  $('.graph-page-container').animate({
    'top': '100%',
  }, slideTime, function() {
    $('.graph-container').empty()
  });
}



// Load and slide in user page in from the right
Geocodr.showUserPage = function(options) {
  // TODO: fix params
  var self = options.self,
      other = options.other;

  $('.users-page-container').load('/users?username='+ other.username, function() {
    Geocodr.renderUserStats(self, other)
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
      Geocodr.showGraphPage(username);
    }

    e.preventDefault();
    return false;
  });
});
