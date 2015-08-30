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

    var margin = {top: 10, right: 10, bottom: 10, left: 10},
      width = 1000 - margin.left - margin.right,
      height = 500 - margin.top - margin.bottom;

    var color = d3.scale.category20c();

    var treemap = d3.layout.treemap()
    .size([width, height])
    .sticky(true)
    .value(function(d) { return d.size; });

    var div = d3.select("#cloudtagChart")
    .style("height", (height + margin.top + margin.bottom) + "px")
    .style("margin-top", "25px")
    .style("margin-left", "45px")
    .attr("class", "col-md-12");

    var node = div.datum(treeRoot).selectAll(".node")
    .data(treemap.nodes)
    .enter().append("div")
    .attr("class", "node")
    .call(position)
    .style("background", function(d) { 
      return d.children ? color(d.name) : null; })
    .text(function(d) { return d.children ? null : d.name; });

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
