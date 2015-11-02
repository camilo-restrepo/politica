'use strict';

boardModule.controller('candidateUsersController', candidateUsersController);
candidateUsersController.$inject = ['$scope', 'environment', '$state', '$stateParams', 'usersService'];

function candidateUsersController($scope, environment, $state, $stateParams, usersService) {

	function success(response){
		$scope.data = response;
	}

	function successCreation(response){
		var chartData = ['data'];
		var dates = ['x'];
		
		for(var i = 0 ; i < response.length ; i++){
			var actual = response[i];
			chartData.push(actual.count);
			dates.push(actual.timestamp);
		}

		var chart = c3.generate({
			bindto: '#userCreationChart',
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

	function onError(response){
		console.log(response);
	}

	$scope.init = function(){
		var candidateTwitterId = $stateParams.twitterId;
		usersService.getTargetUsersCount({ twitterId: candidateTwitterId }, success, onError);
		usersService.getUserCreationCandidate({ twitterId: candidateTwitterId }, successCreation, onError);
	}
}