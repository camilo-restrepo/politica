'use strict';

boardModule.controller('polarityController', polarityController);
polarityController.$inject = ['$scope', 'tweetsService', 'ngDialog'];

function polarityController($scope, tweetsService, ngDialog) {

  function getAllCandidatesPolarity(columnNamesArray, positiveArray, negativeArray) {

    var chart = c3.generate({
      bindto: '#polarityChart',
      data: {
        columns: [ positiveArray, negativeArray ],
        type: 'bar',
        groups: [
          ['Positive', 'Negative']
        ]
      },
      axis: {
        x: {
          type: 'category',
          categories: columnNamesArray
        }
      }
    });

    return chart;
  }

  function success(response) {

    var polarityArray = response;
    var columnNamesArray = [];
    var positiveArray = ['Positivo'];
    var negativeArray = ['Negativo'];

    for (var i = 0; i < polarityArray.length; i++) {

      var candidatePolarity = polarityArray[i];
      columnNamesArray.push(candidatePolarity.twitterId);
      positiveArray.push(candidatePolarity.positivePolarity);
      negativeArray.push(candidatePolarity.negativePolarity);
    }

    $scope.chart = getAllCandidatesPolarity(columnNamesArray, positiveArray, negativeArray);
  }

  function error(response) {
    console.error(response);
  }

  function showInitialDialog() {

    ngDialog.open({
      template: 'views/initial-dialog.html',
      scope: $scope
    });
  }

  $scope.init = function() {

    showInitialDialog();
    tweetsService.getAllCandidatesPolarity({time: 'day'}, success, error);
  }
}
