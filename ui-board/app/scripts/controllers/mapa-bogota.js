'use strict';

boardModule.controller('mapController', mapController);
mapController.$inject = ['$scope', 'tweetsService', '$state', '$stateParams'];

function mapController($scope, tweetsService, $state, $stateParams) {

	var centroids = [];
	var width = 500, height = 900;
	var mapa = d3.select("#mapa").append("svg").attr("width", width).attr("height", height);
		var projection = d3.geo.mercator().scale(135000).translate([width / 2, height / 2]).center([-74.11991,4.649404]);
		var path = d3.geo.path().projection(projection);
		var div = d3.select("#mapa").append("div") .attr("class", "tooltip").style("opacity", 0);

	$scope.changeMessageBoxState = function() {
    	$scope.boxIsFull = !$scope.boxIsFull;
    	$scope.showOrHide = $scope.boxIsFull ? 'Ocultar' : "Mostrar";
  	};

  	function onError(data){
    	console.log(data);
  	}

  	function success(data){
  		for(var i = 0 ; i < data.length ; i++){
  			var location = data[i];
  			centroids.push([location[0], location[1]]);
  		}
  		mapa.selectAll("dot").data(centroids).enter().append("circle").attr("class", "dot").attr("r", 2).attr("transform", function(d) {return "translate(" + projection([d[1],d[0]]) + ")";});
  	}

	$scope.init = function(){
		$scope.cityId = $stateParams.cityId;
		$scope.boxIsFull = true;
      	$scope.showOrHide = 'Ocultar';

		d3.json("scripts/controllers/unidades.json", function(error, bogota) {
			
			var unidades = topojson.feature(bogota, bogota.objects.zonasurbanas),
			vecinos = topojson.neighbors(bogota.objects.zonasurbanas.geometries);

			unidades.features.forEach(function(unidad, i) {
				unidad.centroid = path.centroid(unidad);
    			if (unidad.centroid.some(isNaN)) unidad.centroid = null; // off the map
    				unidad.neighbors = unidad.centroid ? vecinos[i].filter(function(j) { return j < i && unidades.features[j].centroid; }).map(function(j) { return unidades.features[j]; }) : [];
    		});

			mapa.selectAll(".unidad").data(unidades.features).enter().append("path").attr("class", function(){return "unidad"}).attr("d", path)
			.on("mouseover", function(d) { 
				div.html("<b>"+d.properties.name+"</b>");
				div.transition().duration(200).style("opacity", .9);
				div.style("left", (d.centroid[0]) + "px").style("top", (d.centroid[1] - 18) + "px");
            })          
			.on("mouseout", function() {   
				div.transition().duration(500).style("opacity", 0); 
			});			
		});
		tweetsService.getTweetsLocation(success, onError);
	};
}