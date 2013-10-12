Geocodr.initGraph = function() {
  var svgIdent = ".graph-container svg";
  var svg = d3.select(".graph-container svg")

  var width  = $(svgIdent).width(),
      height = $(svgIdent).height();

  var imgWidth  = 100,
      imgHeight = 100;

  var numNodes = 10;

  var fill = d3.scale.category10();

  // TODO: remove dummy data
  var nodes = [
    {
      index: 0,
      img:   "tristan.png",
      name:  "Tristan",
      fixed: true,
      x: width/2,
      y: height/2
    },
    ]

      console.log(nodes)
  var nodes2 = [
    {
      index: 1,
      img:   "pete.png",
      name:  "Pete",
      x: width/2,
      y: height/2
    },
    {
      index: 2,
      img:   "jared.png",
      name:  "Jared",
      x: width/2,
      y: height/2
    },
    {
      index: 3,
      img:   "andrew.png",
      name:  "Andrew",
      x: width/2,
      y: height/2
    }
  ]




  nodes2.forEach(function(n) {
      var r = Math.random() * 2 * Math.PI;
      console.log(Math.cos(r));
      n.x += Math.cos(r) * 10;
      n.y += Math.sin(r) * 10;
  });

  var loading = true;

  var numLoading = 20;
  var loadingDots = svg.append("g")
      .attr("transform", "translate(" + width/2 + "," + height/2 + ")")

  d3.range(numLoading).forEach(function(i) {
      loadingDots.append("circle")
        .attr("class", "loading-dot")
        .attr("r", 5)
        .attr("cx", (imgWidth/2 + 22) * Math.cos(i / numLoading * 2 * Math.PI))
        .attr("cy", (imgWidth/2 + 22) * Math.sin(i / numLoading * 2 * Math.PI))
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
      var first = loadingDots.selectAll("circle")
        .transition()
        .duration(250)
        .attr("cx", 0)
        .attr("cy", 0)
        .each("end", function(){
          if (!called) {
            called = true;
            restart();
          }
        });
    }
  }


  function endLoading() {
    nodes = nodes.concat(nodes2);
    loading = false;
  }

  svg.on("click", function() {
    endLoading();
  });

  var links, force, link, node;

  function restart() {
    console.log("restarting");
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

    link = svg.selectAll("line")
          .data(links)
        .enter().insert("svg:line", ":first-child")
          .attr("x1", function(d) { return d.source.x; })
          .attr("y1", function(d) { return d.source.y; })
          .attr("x2", function(d) { return d.target.x; })
          .attr("y2", function(d) { return d.target.y; })
          .style("stroke", function(d, i) { return d3.rgb(0, 0, 0); })

    node = svg.selectAll(".node")
        .data(nodes)
      .enter().insert("g")
        .attr("class", ".node")
        .attr("transform", "translate(" + width/2 + "," + height/2 + ")")

    node.append("image")
          .attr("class", "profile-picture")
          .attr("x", - imgWidth/2)
          .attr("y", - imgHeight/2)
          .attr("xlink:href", function(d) { return d.img })
          .attr("width", imgWidth)
          .attr("height", imgHeight)
          .attr("clip-path", "url(#clip)")

    node.append("circle")
          .attr("x", - imgWidth/2)
          .attr("y", - imgHeight/2)
          .attr("r", imgHeight/2)
          .attr("class", "graph-circle")

    node.append("text")
        .text(function(d) { return d.name; })
        .attr("x", 0)
        .attr("y", imgHeight/2 + 15)
        .attr("text-anchor", "middle")
        .attr("class", "profile-name")

  }


  // svg.style("opacity", 1e-6)
  //   .transition()
  //     .duration(1000)
  //     .style("opacity", 1);

  function tick(e) {
    node.attr("transform", function(d) { return "translate("+d.x+","+d.y+")"; })

    link.attr("x1", function(d) { return d.source.x; })
          .attr("y1", function(d) { return d.source.y; })
          .attr("x2", function(d) { return d.target.x; })
          .attr("y2", function(d) { return d.target.y; });
  }

  // function mousedown() {
  //   nodes.forEach(function(o, i) {
  //     o.x += (Math.random() - .5) * 40;
  //     o.y += (Math.random() - .5) * 40;
  //   });
  //   force.resume();
  // }


  restart();
  force.stop();
  loadingAnim();
  endLoading();

};
