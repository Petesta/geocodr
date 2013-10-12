Geocodr.initGraph = function(username) {
  var svg = d3.select(".graph-container svg")

  var $svg   = $('.graph-container svg'),
       width = $svg.width(),
      height = $svg.height();

  var imgWidth = imgHeight = 100;

  var numNodes = 10;

  var fill = d3.scale.category10();

  var nodes = [
    {
      index: 0,
      img:   "/assets/images/default.png",
      name:  username,
      login:  username,
      fixed: true,
      x: width/2,
      y: height/2
    },
  ]

  var loading = true,
      numLoading = 20;

  var loadingDots = svg.insert("g", ":first-child")
      .attr("transform", "translate(" + width/2 + "," + height/2 + ")")

  d3.range(numLoading).forEach(function(i) {
      loadingDots.append("circle")
        .attr("class", "loading-dot")
        .attr("r", 5)
        .attr("cx", (imgWidth/2 + 32) * Math.cos(i / numLoading * 2 * Math.PI))
        .attr("cy", (imgWidth/2 + 32) * Math.sin(i / numLoading * 2 * Math.PI))
        });

  function tween(d, i, a) {
    return d3.interpolateString("translate(" + width/2 + "," + height/2 + ")rotate(0)",
        "translate(" + width/2 + "," + height/2 + ")rotate("+360/numLoading+")");
  }

  function loadingAnim() {
    if (loading) {
      loadingDots.transition()
        .duration(300)
        .ease("linear")
        .attrTween("transform", tween)
        .each("end", loadingAnim);
    } else {
      var called = false;
      loadingDots.selectAll("circle")
        .transition()
        .duration(250)
        .attr("cx", 0)
        .attr("cy", 0)
        .each("end", function(s) {
          if (!called) {
            called = true;
            restart();
          }
        });
    }
  }


  function endLoading() {
    loading = false;
  }

  var links, force, link, node;
  link = svg.selectAll("line");
  node = svg.selectAll(".node");

  function restart() {
    links = d3.range(nodes.length - 1).map(function(i) {
      return {
        source: 0,
        target: i + 1
      };
    });

   force = d3.layout.force()
      .nodes(nodes)
      .links(links)
      .linkDistance(200)
      .friction(.8)
      .size([width, height])
      .charge(-300)
      .on("tick", tick)
      .start();


    link = link.data(links);
    link.exit().remove();

    link.enter().insert("svg:line", ":first-child")
          .attr("class", "graph-link")
          .attr("x1", function(d) { return d.source.x; })
          .attr("y1", function(d) { return d.source.y; })
          .attr("x2", function(d) { return d.target.x; })
          .attr("y2", function(d) { return d.target.y; })

    node = node.data(nodes);
    node.exit().remove();

    n = node.enter().insert("g", "g.node")
        .attr("class", function(d) { return d.fixed ? "node node-self" : "node" })
        .attr("transform", function(d) { return "translate(" + d.x + "," + d.y + ")"; })
        .attr("desc", function(d) { return d.login; })
        .on("click", function() {
          // TODO: hasClass isn't working here? WTF
          if ($(this).attr("class").indexOf("node-self") > -1) return false;

          selfphoto  = $('.node-self').find("image").last().attr("href")
          otherphoto = $(this).find("image").attr("href");
          username   = $(this).attr("desc");

          Geocodr.showUserPage({
            username: username,
            selfphoto: selfphoto,
            otherphoto: otherphoto
          });
        });

    n.append("image")
          .attr("class", "profile-picture")
          .attr("x", - imgWidth/2)
          .attr("y", - imgHeight/2)
          .attr("xlink:href", function(d) { return d.img })
          .attr("width", imgWidth)
          .attr("height", imgHeight)
          .attr("clip-path", "url(#clip)")

    n.append("circle")
          .attr("x", - imgWidth/2)
          .attr("y", - imgHeight/2)
          .attr("r", imgHeight/2)
          .attr("class", "graph-circle")

    n.append("text")
        .text(function(d) { return d.name; })
        .attr("x", 0)
        .attr("y", imgHeight/2 + 15)
        .attr("text-anchor", "middle")
        .attr("class", "profile-name")
  }

  function tick(e) {
    node.attr("transform", function(d) { return "translate("+d.x+","+d.y+")"; })

    link.attr("x1", function(d) { return d.source.x; })
          .attr("y1", function(d) { return d.source.y; })
          .attr("x2", function(d) { return d.target.x; })
          .attr("y2", function(d) { return d.target.y; });
  }




  restart();
  force.stop();
  loadingAnim();

  // Load user graph
  $.getJSON("/users/info/"+username, function(data) {
    nodes[0].name = data.name;
    nodes[0].img = data.avatarUrl;
    $(".node-self").find("image").attr("href", data.avatarUrl);
    $(".node-self").find("text").text(data.name);

    $header = $('.graph-container h1');
    $header.find(".location").text(data.location);
    $header.find(".count").text(data.nearbyUsers.length);
    $header.fadeIn();

    // data.nearbyUsers.forEach(function(n) {
    //   var r = Math.random() * 2 * Math.PI;
    //   n.x = imgWidth/2 + Math.cos(r) * 100;
    //   n.y = imgHeight/2 + Math.sin(r) * 100;
    //   n.img = n.gravtar_url;
    // });
    // nodes = nodes.concat(data.users);
    endLoading()
  });

};
