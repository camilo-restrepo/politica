'use strict';

boardModule.controller('scoreController', scoreController);
scoreController.$inject = ['$scope'];

function scoreController($scope) {

  function getCandidateScore(candidateName) {

    var chart = c3.generate({
      bindto: '#scoreChart',
      data: {
        columns: [
          ['Positive', 30],
          ['Negative', 10]
        ],
        type: 'bar',
        groups: [
          ['Positive', 'Negative']
        ]
      }
    });

    return chart;
  }

  $scope.init = function() {

    $scope.chart = getCandidateScore('Enrique Penalosa');
  }
}
