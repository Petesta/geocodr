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
Geocodr.drawLangPiechart = function(selector) {
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

Geocodr.showUserDrawer = function() {
  $('.btn-back').show();
  $('body').animate({ backgroundColor: '#e5e5e5' }); // Darken a bit

  this.animateUserDrawer({
    position: 'relative',
    left: ($(window).outerWidth() - $('.users-container').outerWidth()) / 2
  });

  var $container = $('.users-container')
  $('body').on('click', function(e) {
    var offset = $container.offset()
    if (e.clientX < offset.left || e.clientX > (offset.left + $container.outerWidth())) {
      Geocodr.hideUserDrawer();
    }
  });
}

Geocodr.hideUserDrawer = function() {
  $('.btn-back').hide();
  $('body').animate({ backgroundColor: '#f4f4f4' }); // Back to light

  this.animateUserDrawer({
    position: 'absolute',
    left: '100%'
  });

  $('body').off('click');
}

Geocodr.animateUserDrawer = function(opts) {
  $('.users-container').animate(opts, drawerTransitionTime);
}

$(function() {
  $(document).on('click', '.btn-back', function() {
    Geocodr.hideUserDrawer();
    return false;
  });

})

window.go = function() {
  Geocodr.showUserPage("andrew")
}

//$(function() {
  //setTimeout(go, 1000);
//})
