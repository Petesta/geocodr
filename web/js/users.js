var data = [
  {
    name: 'ruby',
    percent: 90
  },
  {
    name: 'coffeescript',
    percent: 10
  }
];


// Lang pie charts
// ------------------------------------
function drawLangPiechart(selector) {
  // Dimensions
  var w = h = $('.chart').outerWidth(); // Delegate width to CSS

  var ringThickness = 35,
      outerRadius   = w / 2,
      innerRadius   = outerRadius - ringThickness;

  // Function that takes in dataset and returns dataset annotated with arc angles, etc
  var pie = d3.layout.pie()
            .value(function(d) { return d.percent; }); // TODO

  var color = d3.scale.category20();

  // Arc drawing function
  var arc = d3.svg.arc()
          .innerRadius(innerRadius)
          .outerRadius(outerRadius);

  // Create svg element
  var svg = d3.select(selector + " svg")
              .append("svg")
              .attr('width', w)
              .attr('height', h);

  // Set up groups
  arcs = svg.selectAll("g.arc")
            .data(pie(data))
            .enter()
            .append('g')
            .attr('class', 'arc')
            .attr('transform', "translate(#{outerRadius},#{outerRadius})")
            .attr('transform', "translate(" + outerRadius + "," + outerRadius + ")");

  // Draw arc paths
  // A path's path description is defined in the d attribute
  // so we call the arc generator, which generates the path information
  // based on the data already bound to this group
  arcs.append('path')
      .attr('fill', function(d, i) { return color(i) })
      .attr('d', arc);

  // Draw legend w/ labels
  function swatchFor(d, i) {
    if (d.percent === 0) return;

    //<span class="swatch" style="background-color: #08c"></span> Ruby
    return "<span class='swatch' style='background-color: " + color(i) + "'></span> " + d.name;
  }

  d3.select(selector + " .legend")
    .selectAll('li')
    .data(data)
    .enter()
    .append('li')
    .html(function(d, i) { return swatchFor(d,i); })

}

// Drawer animation
// ------------------------------------
var drawerTransitionTime = 600; // ms

function showDrawer() {
  animateDrawer({
    position: 'relative',
    left: ($(window).outerWidth() - $('.users-container').outerWidth()) / 2
  });
}

function hideDrawer() {
  animateDrawer({
    position: 'absolute',
    left: '100%'
  });
}

function animateDrawer(opts) {
  $('.users-container').animate(opts, drawerTransitionTime);
}






var slideTime = 750; // ms





function hideLoginPage() {
  var $container = $('.login-page-container');
  $container.animate({
    'top': '-100%'
  }, slideTime, function() {
    $container.remove();
  });
}


function showGraphPage(username) {
  var $container = $('.graph-page-container');
  $container.load('/graph?username=' + username, function() {
    $container.animate({
      'top': '50px',
    }, slideTime);

    initGraph();
    startGraph();
  });
}


// Load and slide in user page in from the right
function showUserPage(username) {
  // TODO: fix params
  //$('.users-page-container').load('/users?username=' + username, function() {
    //$('body').animate({ backgroundColor: '#f4f4f4' });

    //drawLangPiechart('.chart-you');
    //drawLangPiechart('.chart-them');
    //showDrawer();
  //});
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
      hideLoginPage()
      showGraphPage();
    }

    e.preventDefault();
    return false;
  });
});
