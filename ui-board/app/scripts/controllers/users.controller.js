'use strict';

boardModule.controller('usersController', usersController);
usersController.$inject = ['$scope', 'environment', '$state', '$stateParams', 'usersService'];

function usersController($scope, environment, $state, $stateParams, usersService) {

	var todosLosCandidatos = {
		columnNamesArray: [],
		positiveArray: ['Positivo'],
		negativeArray : ['Negativo']
	};

	var candidatosPopulares = {
		columnNamesArray: [],
		positiveArray: ['Positivo'],
		negativeArray : ['Negativo']
	};

	var candidatosNoPopulares = ['CVderoux', 'MMMaldonadoC', 'RicardoAriasM', 'AlexVernot', 'danielraisbeck'];

	var bogotaCandidates = {
		RicardoAriasM: '#D66F13',
		MMMaldonadoC: '#FBD103',
		danielraisbeck: '#FF5C01',
		ClaraLopezObre: '#FFDF00',
		RafaelPardo: '#ED0A03',
		PachoSantosC: '#3C68B7',
		EnriquePenalosa: '#12ADE5',
		AlexVernot: '#0A5C6D',
		CVderoux: '#088543'
	};

	var medellinCandidates = {
		FicoGutierrez: '#D2EDFA',
		AlcaldeAlonsoS: '#83AC2A',
		RICOGabriel: '#F6783B',
		jcvelezuribe: '#183A64',
		HectorHAlcalde: '#FFDF00'
	};

	var data = [];

	function getCandidatesFromCity(cityId, candidatos) {

		var candidatesFromCity = [];
		for (var i = 0; i < candidatos.length; i++) {
			var candidateColor = null;
			var candidate = candidatos[i].target;
			if (cityId === 'bogota') {
				candidateColor = bogotaCandidates[candidate];
			} else {
				candidateColor = medellinCandidates[candidate];
			}
			if (candidateColor) {
				candidatesFromCity.push(candidate);
			}
		}
		return candidatesFromCity;
	}

	function getAllCandidatesPolarity(candidatosNoPopulares) {
		var chartData = ['Usuarios'];
		var targets = [];
		var data2 = shuffleArray(data);
		
		for(var i = 0 ; i < data2.length ; i++){
			var actual = data2[i];
			if(candidatosNoPopulares){
				if(candidatosNoPopulares.indexOf(actual.target) === -1){
					chartData.push(actual.count);
					targets.push(actual.target);
				}
			}else{
				chartData.push(actual.count);
				targets.push(actual.target);
			}
		}

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

		var chart = c3.generate({
			bindto: '#usersChart',
			size: {
				height: 530
			},
			data: {
				columns: [chartData],
				type: 'bar',
				color: function(color, d){
					return colors[targets[d.index]];
				}
			},
			axis: {
				x: {
					type: 'category',
					categories: targets
				},
				y: {
					label: {
						text: 'Usuarios',
						position: 'outer-middle'
					}
				}
			},
			legend: {
				show: false
			}
		});
		return chart;
	}


	function shuffleArray(o) {
		for(var j, x, i = o.length; i; j = Math.floor(Math.random() * i), x = o[--i], o[i] = o[j], o[j] = x);
			return o;
	}

	$scope.showAllCandidates = function() {
		$scope.chart = getAllCandidatesPolarity();
	};

	$scope.showPopularCandidatesOnly = function() {
		$scope.chart = getAllCandidatesPolarity(candidatosNoPopulares);
	};

	function success(response){
		data = response;
		var totalCount = 0;
		for(var i = 0 ; i < response.length ; i++){
			totalCount += response[i].count;
		}
		$scope.userCount = totalCount;

		$scope.showPopularCandidatesOnly();
	}

	function onError(response){
		console.log(response);
	}

	$scope.changeMessageBoxState = function() {
		$scope.boxIsFull = !$scope.boxIsFull;
		$scope.showOrHide = $scope.boxIsFull ? 'Ocultar' : "Mostrar";
	};

	function successCreation(response){

		var chartData = ['data'];
		var dates = ['x'];
		
		for(var i = 0 ; i < response.length ; i++){
			var actual = response[i];
			chartData.push(actual.users);
			dates.push(actual.time);
		}

		c3.generate({
			bindto: '#creationChart',
			size: {
				height: 530
			},
			data: {
				x: 'x',
				xFormat: '%m-%Y',
				columns: [dates, chartData],
				type: 'bar',
			},
			axis: {
				x: {
					type: 'timeseries',
					categories: dates,
					tick: {
						format: '%m-%Y'
					}
				},
				y: {
					label: {
						text: 'Usuarios',
						position: 'outer-middle'
					}
				}
			},
			legend: {
				show: false
			}
		});
	}

	function successVenn(response){

		var candidates = {
			RafaelPardo: 0,
			PachoSantosC: 1,
			AlexVernot: 2,
			ClaraLopezObre:3,
			HOLLMANMORRIS:4,
			CVderoux:5,
			danielraisbeck:6,
			EnriquePenalosa:7,
			MMMaldonadoC:8,
			RicardoAriasM:9
		};

		var sets = [];

		for(var i = 0 ; i < response.length ; i++){
			var actual = response[i];
			var data = [];
			for(var j = 0 ; j < actual.candidates.length ; j++){
				var c = actual.candidates[j];
				data.push(candidates[c]);
			}

			var set = {
				sets: data,
				size: actual.count
			};

			if(actual.candidates.length === 1){
				set.label = actual.candidates[0];
			}
			sets.push(set);
		}

		var div = d3.select("#venn");
		div.datum(sets).call(venn.VennDiagram());

		var tooltip = d3.select("body").append("div").attr("class", "venntooltip");
		div.selectAll("g").on("mouseover", function(d, i) {
        	// sort all the areas relative to the current item
        	venn.sortAreas(div, d);

        	// Display a tooltip with the current size
        	tooltip.transition().duration(400).style("opacity", .9);
        	tooltip.text(d.size + " usuarios");

        	// highlight the current path
        	var selection = d3.select(this).transition("tooltip").duration(400);
        	selection.select("path").style("stroke-width", 3).style("fill-opacity", d.sets.length === 1 ? .4 : .1).style("stroke-opacity", 1);
    	}).on("mousemove", function() {
    		tooltip.style("left", (d3.event.pageX) + "px").style("top", (d3.event.pageY - 28) + "px");
    	}).on("mouseout", function(d, i) {
    		tooltip.transition().duration(400).style("opacity", 0);
    		var selection = d3.select(this).transition("tooltip").duration(400);
    		selection.select("path").style("stroke-width", 0).style("fill-opacity", d.sets.length === 1 ? .25 : .0).style("stroke-opacity", 0);
    	});

    	d3.selectAll("#venn .venn-circle path").style("fill", function(d){
    		return bogotaCandidates[d.label];
    	});
		d3.selectAll("#venn text").style("fill", function(d){
			return bogotaCandidates[d.label];
		});
	}

	$scope.init = function(){
		$scope.boxIsFull = true;
		$scope.showOrHide = 'Ocultar';
		usersService.getAllUserCount(success, onError);
		usersService.getCreationSummary(successCreation, onError);
		usersService.getVennData(successVenn, onError);
	};
}