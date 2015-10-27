'use strict';

boardModule.controller('candidateMapController', candidateMapController);
candidateMapController.$inject = ['$scope', 'tweetsService', '$state', '$stateParams', '$rootScope'];

function candidateMapController($scope, tweetsService, $state, $stateParams, $rootScope) {

	var candidateId = "";
	var centroids = [];
	var width = 500, height = 900;
	var mapa = d3.select("#candidateMap").append("svg").attr("width", width).attr("height", height);
	var projection = d3.geo.mercator().scale(95000).translate([width / 2, height / 2]).center([-74.11991,4.649404]);
	var path = d3.geo.path().projection(projection);
	var div = d3.select("#candidateMap").append("div") .attr("class", "tooltip").style("opacity", 0);


	function onError(data){
		console.log(data);
	}

	function getCandidateColor(twitterId) {

		var colors = {
			RicardoAriasM: '#D66F13',
			MMMaldonadoC: '#FBD103',
			danielraisbeck: '#FF5C01',
			ClaraLopezObre: '#FFDF00',
			RafaelPardo: '#ED0A03',
			PachoSantosC: '#3C68B7',
			EnriquePenalosa: '#12ADE5',
			AlexVernot: '#0A5C6D',
			CVderoux: '#088543',
			FicoGutierrez: '#FE5859',
			AlcaldeAlonsoS: '#83AC2A',
			RICOGabriel: '#F6783B',
			jcvelezuribe: '#183A64',
			HectorHAlcalde: '#FFDF00'
		};

		return colors[twitterId];
	};

	function success(data){
		centroids = data;
  		
  		mapa.selectAll("dot").data(centroids).enter().append("circle").style("fill", function(d){return getCandidateColor(d.targetTwitterId)}).attr("r", 3).attr("transform", function(d) {return "translate(" + projection([d.geo.coordinates[1],d.geo.coordinates[0]]) + ")";})
  		.on("mouseover", function(d) {
  				var date = new Date(d.timestamp_ms);
  				var dateStr = date.getDate()+"-"+(date.getMonth()+1)+"-"+date.getFullYear()+ " "+ date.getHours()+":"+date.getMinutes()+":"+date.getSeconds();
   				var html = "<b>"+d.targetTwitterId+"</b> <p><u>Tweet:</u> "+d.text+"</p><p><u>Fecha:</u> "+dateStr+"</p><p><u>Polaridad:</u> ";
  				if(d.prediction === "negative"){
  					html = html + "Negativo</p>"
  				}else if(d.prediction === "positive"){
  					html = html + "Positivo</p>"
  				}else{
					html = html + "Neutral</p>"
  				}

				div.html(html);
				div.transition().duration(200).style("opacity", .9);
				div.style("z-index", "2000").style("width" , "300px")
				.style("height", d3.select("div.tooltip").node().getBoundingClientRect().height);
				div.style("left", (d.geo.coordinates[0] + 50) + "px").style("top", (d.geo.coordinates[1] + 460) + "px");
        }).on("mouseout", function() {   
				div.transition().duration(500).style("opacity", 0); 
		});
	}

	$scope.init = function(){
		$scope.cityId = $stateParams.cityId;
		candidateId = $stateParams.twitterId;
		$rootScope.cityId = $stateParams.cityId;

		d3.json("resources/unidades.json", function(error, bogota) {
			
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
				div.transition().duration(200).style("opacity", .9).style("width" , "150px")
				.style("height", d3.select("div.tooltip").node().getBoundingClientRect().height);
				div.style("left", (d.centroid[0]) + "px").style("top", (d.centroid[1] - 18) + "px");
			})          
			.on("mouseout", function() {   
				div.transition().duration(500).style("opacity", 0); 
			});			
		});
		tweetsService.getTweetsCandidateLocation({ twitterId: candidateId }, success, onError);
	};
}
