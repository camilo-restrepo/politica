'use strict';

boardModule.controller('polarityController', polarityController);
polarityController.$inject = ['$scope', 'tweetsService'];

function polarityController($scope, tweetsService) {

  function getAllCandidatesPolarity(columnNamesArray, positiveArray, negativeArray) {

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
      bindto: '#polarityChart',
      size: {
        height: 530
      },
      data: {
        columns: [ positiveArray, negativeArray ],
        colors: { 
          Positivo: '#BDDA9B', 
          Negativo: '#F4A8AF'
        },
        type: 'bar',
        
      },
      axis: {
        x: {
          type: 'category',
          categories: columnNamesArray
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

  function getTargetName(targetId){
    if(targetId === 'CVderoux'){
      return 'Carlos Vicente de Roux';
    }else if(targetId === 'EnriquePenalosa'){
      return 'Enrique Peñalosa';
    }else if(targetId === 'PachoSantosC'){
      return 'Francisco Santos';
    }else if(targetId === 'ClaraLopezObre'){
      return 'Clara López Obregon';
    }else if(targetId === 'AlexVernot'){
      return 'Alex Vernot';
    }else if(targetId === 'RicardoAriasM'){
      return 'Ricardo Arias Mora';
    }else if(targetId === 'RafaelPardo'){
      return 'Rafael Pardo';
    }else if(targetId === 'MMMaldonadoC'){
      return 'María Mercedes Maldonado';
    }else if(targetId === 'danielraisbeck'){
      return 'Daniel Raisbeck';
    }

    return '';
  }

  function success(response) {

    var polarityArray = response;
    var columnNamesArray = [];
    var positiveArray = ['Positivo'];
    var negativeArray = ['Negativo'];

    for (var i = 0; i < polarityArray.length; i++) {

      var candidatePolarity = polarityArray[i];
      columnNamesArray.push(getTargetName(candidatePolarity.twitterId));
      positiveArray.push(candidatePolarity.positivePolarity);
      negativeArray.push(candidatePolarity.negativePolarity);
    }

    $scope.chart = getAllCandidatesPolarity(columnNamesArray, positiveArray, negativeArray);
  }

  function error(response) {
    console.error(response);
  }

  $scope.changeMessageBoxState = function() {
    $scope.boxIsFull = !$scope.boxIsFull;
    $scope.showOrHide = $scope.boxIsFull ? 'Ocultar' : "Mostrar";
  };

  $scope.init = function() {

    $scope.boxIsFull = true;
    $scope.showOrHide = 'Ocultar';
    tweetsService.getAllCandidatesPolarity({time: 'day'}, success, error);
  };
}
