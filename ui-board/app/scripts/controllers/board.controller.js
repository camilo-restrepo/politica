'use strict';

boardModule.controller('boardController', boardController);
boardController.$inject = ['$scope', '$websocket'];

function boardController($scope, $websocket) {

	$scope.primerCandidato = [];

    var ws = $websocket.$new({
		url: 'ws://localhost:9001/board/api/ws',
		protocols: []
	}); 

    ws.$on('$open', function () {
    	console.log('Oh my gosh, websocket is really open! Fukken awesome!');
    });

    ws.$on('$message', function (data) {
       	pushData(data);
       	//ws.$close();
    });

    ws.$on('$close', function () {
       	console.log('Noooooooooou, I want to have more fun with ngWebsocket, damn it!');
    });

    ws.$open();

    function pushData(data){
		$scope.primerCandidato.push(data);
		//console.log(data);
		if($scope.primerCandidato.length > 5){
			$scope.primerCandidato.shift();
		}
		$scope.$apply();
    }


}
