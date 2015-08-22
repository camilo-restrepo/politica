'use strict';

boardModule.controller('polarityController', polarityController);
polarityController.$inject = ['$scope'];

function polarityController($scope) {

  function getPolarityForCandidate(candidateName) {

    var chart = c3.generate({
      bindto: '#polarityChart',
      data: {
        columns: [
          [candidateName, 30, 200, 100, 400, 150, 250],
        ]
      }
    });

    return chart;
  }

  $scope.init = function() {

    $scope.chart = getPolarityForCandidate('Enrique Penalosa');
  }
}
