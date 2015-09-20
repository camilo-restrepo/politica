'use strict';

boardModule.controller('scoreController', scoreController);
scoreController.$inject = ['$scope', 'scoreService'];

function scoreController($scope, scoreService) {

  function getCandidateScore(listOfCandidatesLists) {

    var colors = {
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

    var chart = c3.generate({
      bindto: '#scoreChart',
      size: {
        height: 530
      },
      data: {
        x: 'x',
        columns: listOfCandidatesLists,
        colors: colors,
        type: 'spline'
      },
      axis: {
        x: {
          type: 'timeseries',
          tick: {
            format: '%m-%d'
          }
        },
        y: {
          label: {
            text: 'Tweets',
            position: 'outer-middle'
          }
        }
      },
      legend: {
        position: 'right'
      }
    });

    return chart;
  }

  $scope.showAllCandidates = function() {
    $scope.chart.show(['CVderoux', 'MMMaldonadoC', 'RicardoAriasM', 'AlexVernot', 'danielraisbeck']);
  }

  $scope.showPopularCandidatesOnly = function() {
    $scope.chart.hide(['CVderoux', 'MMMaldonadoC', 'RicardoAriasM', 'AlexVernot', 'danielraisbeck']);
  }

  function getListOfCandidatesLists(response) {

    var xAxis = ['x'];
    var listOfCandidatesLists = [];

    var candidatesList = response;

    for (var i = 0; i < candidatesList.length; i++) {

      var candidate = candidatesList[i];
      var candidateTwitterId = candidate.target;
      var candidateScoreList = candidate.scores;

      var innerList = [candidateTwitterId];

      for (var j = 0; j < candidateScoreList.length; j++) {

        var scoreNode = candidateScoreList[j];

        if (i == 0) {
          var dateInMillis = scoreNode.date;
          xAxis.push(dateInMillis);
        }

        var scoreValue = scoreNode.value;
        if (!isNaN(scoreValue)) {
          innerList.push(scoreValue);
        }
      }

      listOfCandidatesLists.push(innerList);
    }

    listOfCandidatesLists.unshift(xAxis);
    return listOfCandidatesLists;
  }

  function getAllTargetsScoreSuccess(response) {

    var listOfCandidatesLists = getListOfCandidatesLists(response);
    $scope.chart = getCandidateScore(listOfCandidatesLists);
    $scope.showPopularCandidatesOnly();
  }

  function logError(response) {
    console.error(response);
  }

  $scope.changeMessageBoxState = function() {
    $scope.boxIsFull = !$scope.boxIsFull;
    $scope.showOrHide = $scope.boxIsFull ? 'Ocultar' : "Mostrar";
  }

  $scope.init = function() {

    $scope.boxIsFull = true;
    $scope.showOrHide = 'Ocultar';
    scoreService.getAllTargetsScore(getAllTargetsScoreSuccess, logError);
  }
}
