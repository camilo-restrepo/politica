'use strict';

boardModule.controller('cloudtagController', cloudtagController);
cloudtagController.$inject = ['$scope', 'cloudtagService'];

function cloudtagController($scope, cloudtagService) {

  function getResponseAsTree(response) {

    var treeRoot = {

      name: "Alcaldía Bogotá 2015",
      children: []
    };

    for (var i = 0; i < response.length; i++) {
      
      var candidateNode = response[i];
      var name = candidateNode.twitterId;
      var children = [];

      for (var j = 0; j < candidateNode.wordCountList.length; j++) {

        var wordCountNode = candidateNode.wordCountList[j];
        var child = { name: wordCountNode.word, size: wordCountNode.count };
        children.push(child);
      }

      var childNode = {
        name: name,
        children: children
      };

      treeRoot.children.push(childNode);
    }

    return treeRoot;
  }

  function getTreeMap(treeRoot) {

    var margin = {top: 40, right: 10, bottom: 10, left: 10},
      width = 960 - margin.left - margin.right,
      height = 500 - margin.top - margin.bottom;

    var color = d3.scale.category20c();

    var treemap = d3.layout.treemap()
    .size([width, height])
    .sticky(true)
    .value(function(d) { return d.size; });

    var div = d3.select("body").append("div")
    .style("position", "relative")
    .style("width", (width + margin.left + margin.right) + "px")
    .style("height", (height + margin.top + margin.bottom) + "px")
    .style("left", margin.left + "px")
    .style("top", margin.top + "px");

    var node = div.datum(treeRoot).selectAll(".node")
    .data(treemap.nodes)
    .enter().append("div")
    .attr("class", "node")
    .call(position)
    .style("background", function(d) { 
      console.debug('');
      return d.children ? color(d.name) : null; })
    .text(function(d) { return d.children ? null : d.name; });

    d3.selectAll("input").on("change", function change() {
      var value = this.value === "count"
        ? function() { return 1; }
        : function(d) { return d.size; };

        node
        .data(treemap.value(value).nodes)
        .transition()
        .duration(1500)
        .call(position);
    });

    function position() {
      this.style("left", function(d) { return d.x + "px"; })
      .style("top", function(d) { return d.y + "px"; })
      .style("width", function(d) { return Math.max(0, d.dx - 1) + "px"; })
      .style("height", function(d) { return Math.max(0, d.dy - 1) + "px"; });
    }

  }

  function success(response) {
    var treeRoot = getResponseAsTree(response);
    $scope.chart = getTreeMap(treeRoot);
  }

  function error(response) {
    console.error(response);
  }

  $scope.init = function() {

    cloudtagService.getAllCandidatesCloudTags(success, error);
  }
}
