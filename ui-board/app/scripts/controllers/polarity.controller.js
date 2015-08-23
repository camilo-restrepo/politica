'use strict';

boardModule.controller('polarityController', polarityController);
polarityController.$inject = ['$scope'];

function polarityController($scope) {

  function getPolarityForCandidate(candidateName) {

    var chart = c3.generate({
      bindto: '#polarityChart',
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

    $scope.chart = getPolarityForCandidate('Enrique Penalosa');
  }
}
