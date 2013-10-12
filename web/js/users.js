// Lang intersection summary
// ------------------------------------
// Intersection of two arrays
function intersection(a, b) {
  var ai=0, bi=0, result = new Array();

  while ( ai < a.length && bi < b.length ) {
    if      (a[ai] < b[bi] ){ ai++; }
    else if (a[ai] > b[bi] ){ bi++; }
    else { // they're equal
      result.push(a[ai]);
      ai++; bi++;
    }
  }
  return result;
}

// Like Rails #to_sentence. Joins an array of language strings, eg.
// 'ruby, python, and 2 others' or 'ruby and python'
function langClause(commonLangs) {
  var commonLen = commonLangs.length,
      clause;

  if (commonLen >= 3) {
    // Ruby, Python, and 2 others
    clause = commonLangs.slice(0,2).join(', ') + ", and " + commonLangs.slice(2).length + " other languages";
  } else if (commonLen === 2) {
    // Ruby and Python
    clause = commonLangs.join(' and ');
  } else if (commonLen === 1) {
    // Ruby
    clause = commonLangs[0];
  } else {
    clause = '';
  }

  return clause;
}

Geocodr.fillLangSummary = function(self, other) {
  // TODO: FUK U FUTURES Y U NO EXIST
  $.getJSON('/users/lang?username='+self.username, function(selfData) {
    $.getJSON('/users/lang?username='+other.username, function(otherData) {
      var commonLangs = intersection(selfData.langs, otherData.langs),
          clause      = langClause(commonLangs),
          text        = self.username + ' and ' + other.username;

      if (clause === '') {
        console.log("no common langs? that's hopefully wrong");
        text += " don't write any of the same languages!";
      } else {
        text += ' both write ' + clause + '.';
      }

      $('.lang-summary').text(text);
    });
  });
}



// Lang pie charts
// ------------------------------------
Geocodr.drawLangPiechart = function(selector) {
  //$.getJSON('/users/', function(data) {
  //  var langData = data.langs;
  //});

  var langData = [
    {
      name: 'ruby',
      percent: 90
    },
    {
      name: 'coffeescript',
      percent: 10
    }
  ];

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
  function swatchFor(d, i) {
    if (d.percent === 0) return;

    //<span class="swatch" style="background-color: #08c"></span> Ruby
    return "<span class='swatch' style='background-color: " + color(i) + "'></span> " + d.name;
  }

  d3.select(selector + " .legend")
    .selectAll('li')
    .data(langData)
    .enter()
    .append('li')
    .html(function(d, i) { return swatchFor(d,i); })
}



// Starred repos table
// ------------------------------------
Geocodr.fillStarsTable = function(self, other) {
  function repoLink(owner, name) {
    return "<a href='http://www.github.com/"+owner+"/"+name+"'>"+name+"</a>";
  }

  var $tbody = $('.starred-repos tbody')
  // $.getJSON('/users/repos?username='+other.username, function(data) {
  //   var repos = data.repos;
  //   $('.common-repo-count').text("You and " + other.username + " have " + repos.length + " starred repos in common.");

  //   var $row, $cell, repo;

  //   for (var i=0; i<repos.length; i++)  {
  //     repo  = repos[i];
  //     $row  = $("<tr>");
  //     $cell = $("<td>");
  //     $cell.append("<i class='icon icon-star'></i>")
  //     $cell.append(repo.owner + " / " + repoLink(repo.owner, repo.name))
  //     $row.append($cell);
  //     $tbody.append($row);
  //   }
  // });

  var repos  = [
    { owner: "andrewberls", name: "regularity" },
    { owner: "jroesch", name: "tweak" },
    { owner: "andrewberls", name: "kona" },
    { owner: "petesta", name: "geocodr" },
    ]

  $('.common-repo-count').text("You and " + other.username + " have " + repos.length + " starred repos in common.");

  var $row, $cell, repo;

  for (var i=0; i<repos.length; i++)  {
    repo  = repos[i];
    $row  = $("<tr>");
    $cell = $("<td>");
    $cell.append("<i class='icon icon-star'></i>")
    $cell.append(repo.owner + " / " + repoLink(repo.owner, repo.name))
    $row.append($cell);
    $tbody.append($row);
  }
}


// Drawer animation
// ------------------------------------
var drawerTransitionTime = 600; // ms

Geocodr.showUserDrawer = function() {
  $('.btn-back').show();

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
  $('.user-photo.you').css('background', "url('"+self.photo+"')");
  $('.user-photo.them').css('background', "url('"+other.photo+"')");

  Geocodr.fillLangSummary(self, other)
  Geocodr.drawLangPiechart('.chart-you');
  Geocodr.drawLangPiechart('.chart-them');

  Geocodr.fillStarsTable(self, other);

  Geocodr.showUserDrawer();
}



$(function() {
  $(document).on('click', '.btn-back', function() {
    Geocodr.hideUserDrawer();
    return false;
  });

})

window.go = function() {
  Geocodr.showUserPage({
    'self': {
      username: "andrewberls",
      photo: "https://0.gravatar.com/avatar/eedc3687a5e76c282e43508e29cd67b7?d=https%3A%2F%2Fidenticons.github.com%2F2cd91248fe0d57b51dc83ffbe5782325.png&s=440",
    },
    'other': {
      username: "roeschinc",
      photo: "https://1.gravatar.com/avatar/41b3f81fe12349bcfa70eff20eaeb187?d=https%3A%2F%2Fidenticons.github.com%2Fa1e0a41acabf07a7b060cfab2e882e16.png&s=440"
    }
  });
}

//$(function() {
  //setTimeout(go, 1000);
//})
