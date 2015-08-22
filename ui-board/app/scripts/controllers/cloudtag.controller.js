'use strict';

boardModule.controller('cloudtagController', cloudtagController);
cloudtagController.$inject = ['$scope'];

function cloudtagController($scope) {

  function getTreeMap() {

    var margin = {top: 40, right: 10, bottom: 10, left: 10};
    var width = 960 - margin.left - margin.right;
    var height = 500 - margin.top - margin.bottom;

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

    var root = {
      "name": "Alcaldia Bogota 2015",
      "children": [
        {
          "name": "Enrique Penalosa",
          "children": [
            {
              "name": "Urbanismo",
              "size": 3938
            },
            {
              "name": "Bicicleta",
              "size": 3812
            },
            {
              "name": "Movilidad",
              "size": 6714
            },
            {
              "name": "Seguridad",
              "size": 743
            }
          ]
        },
        {
          "name": "Rafael Pardo",
          "children": [
            {
              "name": "Seguridad",
              "size": 3938
            },
            {
              "name": "Movilidad",
              "size": 3812
            },
            {
              "name": "Metro",
              "size": 6714
            },
            {
              "name": "Empleo",
              "size": 743
            }
          ]
        },
        {
          "name": "Carlos Vicente de Roux",
          "children": [
            {
              "name": "Honestidad",
              "size": 3938
            },
            {
              "name": "Transparencia",
              "size": 3812
            },
            {
              "name": "Metro",
              "size": 6714
            },
            {
              "name": "Movilidad",
              "size": 743
            }
          ]
        }
      ]
    };

    var node = div.datum(root).selectAll(".node")
    .data(treemap.nodes)
    .enter().append("div")
    .attr("class", "node")
    .call(position)
    .style("background", function(d) { return d.children ? color(d.name) : null; })
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

  $scope.init = function() {
    $scope.chart = getTreeMap();
  }
}
