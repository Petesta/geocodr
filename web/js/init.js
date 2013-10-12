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
  var self = options.self,
      other = options.other;

  $('.users-page-container').load('/users?username='+ other.username, function() {
    Geocodr.fillLangSummary(self, other)
    Geocodr.drawLangPiechart('.chart-you');
    Geocodr.drawLangPiechart('.chart-them');

    $('.user-photo.you').css('background', "url('"+self.photo+"')");
    $('.user-photo.them').css('background', "url('"+other.photo+"')");

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
      Geocodr.showGraphPage(username);
    }

    e.preventDefault();
    return false;
  });
});
