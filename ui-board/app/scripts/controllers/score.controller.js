'use strict';

boardModule.controller('scoreController', scoreController);
scoreController.$inject = ['$scope'];

function scoreController($scope) {

  function getCandidateScore(candidateName) {

    var chart = c3.generate({
      bindto: '#scoreChart',
      data: {
        columns: [
          ['Positive', 70, 50, 23],
          ['Negative', 30, 50, 77]
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
