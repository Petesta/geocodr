Geocodr.langsRoute = '/users/languages/' // + username
Geocodr.starsRoute = '/users/starred/'   // + user1/user2






// Lang intersection summary
// ------------------------------------
// Sort an array of language objects to remove those
// with empty titles and then sort by percent desc
function filterAndSort(langs) {
  return langs.filter(function(e) { return e.language })
              .sort(function(a,b) { return b.percent - a.percent })
}

// Intersection of two arrays
function intersection(a, b) {
  a = filterAndSort(a).map(function(e) { return e.language })
  b = filterAndSort(b).map(function(e) { return e.language })

  return a.filter(function(e) { return b.indexOf(e) != -1; })

  // TODO: this code doesn't play nice with objects?
  //var ai=0, bi=0, result = new Array();

  //while ( ai < a.length && bi < b.length ) {
    //if      (a[ai] < b[bi] ){ ai++; }
    //else if (a[ai] > b[bi] ){ bi++; }
    //else {
      //result.push(a[ai]);
      //ai++; bi++;
    //}
  //}
  //return result;
}

// Like Rails #to_sentence. Joins an array of language strings, eg.
// 'ruby, python, and 2 others' or 'ruby and python'
function langClause(commonLangs) {
  var commonLen = commonLangs.length,
      clause = '';

  if (commonLen >= 4) {
    // Ruby, Python, and 2 others
    var rest = commonLangs.slice(3);
    clause = commonLangs.slice(0,3).join(', ') + ", and " + (rest.length === 1 ? " 1 other language" : rest.length + " other languages");
  } else if (commonLen === 3 || commonLen === 2) {
    // Ruby and Python
    clause = commonLangs.join(' and ');
  } else if (commonLen === 1) {
    // Ruby
    clause = commonLangs[0];
  }

  return clause;
}

Geocodr.fillLangSummary = function(self, other, selfLangs, otherLangs) {
  var clause = langClause(intersection(selfLangs, otherLangs)),
      text   = self.username + ' and ' + other.username;

  if (clause === '') {
    text += " don't write any of the same languages!";
  } else {
    text += ' both write ' + clause + '.';
  }

  $('.lang-summary').text(text);
}



// Lang pie charts
// ------------------------------------
Geocodr.drawLangPiechart = function(selector, langData) {
  // langData = [
  //   {
  //     name: 'ruby',
  //     percent: 90
  //   },
  //   {
  //     name: 'coffeescript',
  //     percent: 10
  //   }
  // ];

  langData = filterAndSort(langData);

  // Dimensions
  var w = h = $('.chart').outerWidth(); // Delegate width to CSS

  var ringThickness = 35,
      outerRadius   = w / 2,
      innerRadius   = outerRadius - ringThickness;

  // Function that takes in dataset and returns dataset annotated with arc angles, etc
  var pie = d3.layout.pie()
            .value(function(d) { return d.percent; });

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
            .data(pie(langData))
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
  //var legendLangs = langData.sort(function(a,b) { return b.percent - a.percent }),
  var legendLangs = langData,
      drawEllipses = false;

  if (legendLangs.length > 5) {
    legendLangs = legendLangs.slice(0,5);
    drawEllipses = true;
  }


  function swatchFor(d, i) {
    if (d.percent === 0) return;

    //<span class="swatch" style="background-color: #08c"></span> Ruby
    return "<span class='swatch' style='background-color: " + color(i) + "'></span> " + d.language +
      "<span class='percent'>("+d.percent.toFixed(2)+"%)</span>"
  }

  d3.select(selector + " .legend")
    .selectAll('li')
    .data(legendLangs)
    .enter()
    .append('li')
    .html(function(d, i) { return swatchFor(d,i); })

  if (drawEllipses === true) {
    $(selector).append("<div class='ellipses'>& "+(langData.length-5).toString()+" more...</div>");
  }
}



// Starred repos table
// ------------------------------------
Geocodr.fillStarsTable = function(self, other) {
  function repoLink(owner, name) {
    return "<a target='_blank' href='http://www.github.com/"+owner+"/"+name+"'>"+name+"</a>";
  }

  var $tbody = $('.starred-repos tbody')
  // var repos  = [
  //   { full_name: "andrewberls/regularity" },
  //   { full_name: "jroesch/tweak" },
  //   { full_name: "andrewberls/kona" },
  //   { full_name: "petesta/geocodr" },
  // ]

  $.getJSON(Geocodr.starsRoute+self.username+"/"+other.username, function(repos) {
    $('.common-repo-count').text("You and " + other.username + " have " + repos.length + " starred repos in common.");

    if (repos.length === 0) return;

    var $row, $cell, repo;

    for (var i=0; i<repos.length; i++)  {
      repo     = repos[i];
      segments = repo.full_name.split("/");
      owner    = segments[0];
      name     = segments[1];
      $row     = $("<tr>");
      $cell    = $("<td>");
      $cell.append("<i class='icon icon-star'></i>")
      $cell.append(owner + " / " + repoLink(owner, name))
      $row.append($cell);
      $tbody.append($row);
    }
  });
}


// Drawer animation
// ------------------------------------
var drawerTransitionTime = 600; // ms

Geocodr.showUserDrawer = function() {
  $('.btn-back').show();
  $('.back-to-login').hide()

  $('body').animate({ backgroundColor: '#ddd' }, function() { // Darken a bit
    Geocodr.animateUserDrawer({
      position: 'relative',
      left: ($(window).outerWidth() - $('.users-container').outerWidth()) / 2
    }, 700);

  });


  // Hide drawer if clicking in gutter
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
  $('.back-to-login').show();
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

// Fill in all modules for a user
Geocodr.renderUserStats = function(self, other) {
  // User photos
  $('.user-photo.you').css('background', "url('"+self.photo+"')");
  $('.user-photo.them').css('background', "url('"+other.photo+"')");

  $('.username-you').text(self.username);
  $('.username-them').text(other.username);

  // Language breakdown
  // FUK U FUTURES Y U NO EXIST
  $.getJSON(Geocodr.langsRoute+self.username, function(selfLangs) {

    $.getJSON(Geocodr.langsRoute+other.username, function(otherLangs) {
      Geocodr.fillLangSummary(self, other, selfLangs, otherLangs)

      Geocodr.drawLangPiechart('.chart-you', selfLangs);
      Geocodr.drawLangPiechart('.chart-them', otherLangs);
    });
  });

  // Common stars
  Geocodr.fillStarsTable(self, other);

  Geocodr.showUserDrawer();
}



$(function() {
  $(document).on('click', '.btn-back', function() {
    Geocodr.hideUserDrawer();
    return false;
  });
});






window.go = function() {
  Geocodr.showUserPage({
    'self': {
      username: "jcody",
      photo: "https://0.gravatar.com/avatar/eedc3687a5e76c282e43508e29cd67b7?d=https%3A%2F%2Fidenticons.github.com%2F2cd91248fe0d57b51dc83ffbe5782325.png&s=440",
    },
    'other': {
      username: "jroesch",
      photo: "https://1.gravatar.com/avatar/41b3f81fe12349bcfa70eff20eaeb187?d=https%3A%2F%2Fidenticons.github.com%2Fa1e0a41acabf07a7b060cfab2e882e16.png&s=440"
    }
  });
}
