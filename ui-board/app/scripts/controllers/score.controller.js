'use strict';

boardModule.controller('scoreController', scoreController);
scoreController.$inject = ['$scope'];

function scoreController($scope) {

  function getCandidateScore(candidateName) {

    var chart = c3.generate({
      bindto: '#scoreChart',
      data: {
        columns: [
          [candidateName, 30, 200, 100, 400, 150, 250],
          ['Rafael Pardo', 80, 150, 30, 300, 320, 125],
          ['Clara Lopez', 40, 190, 230, 80, 20, 300]
        ]
      }
    });

    return chart;
  }

  $scope.init = function() {

    $scope.chart = getCandidateScore('Enrique Penalosa');
  }
}
