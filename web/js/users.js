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



$(function() {
  // TODO: Why isn't HTML autofocus working? All of my hate.
  $('#name').focus();

  $('.username-form').submit(function() {
    var $field =  $(this).find("#name"),
        username = $field.val();

    if (username === '') {
      $field.addClass('field-error')
    } else {
      // TODO: this should load the graph, this is a placeholder
      $('.users-page-container').load('/users/' + username, function() {
        drawLangPiechart('.chart-you');
        drawLangPiechart('.chart-them');
        showDrawer();
      });
    }

    return false;
  });
});
