'use strict';

boardModule.controller('polarityController', polarityController);
polarityController.$inject = ['$scope', 'tweetsService'];

function polarityController($scope, tweetsService) {

    var todosLosCandidatos = {
      columnNamesArray: [],
      positiveArray: ['Positivo'],
      negativeArray : ['Negativo']
    };

    var candidatosPopulares = {
      columnNamesArray: [],
      positiveArray: ['Positivo'],
      negativeArray : ['Negativo']
    };

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

  function shuffleArray(o) {
    for(var j, x, i = o.length; i; j = Math.floor(Math.random() * i), x = o[--i], o[i] = o[j], o[j] = x);
    return o;
  }

  $scope.showAllCandidates = function() {
    $scope.chart = getAllCandidatesPolarity(todosLosCandidatos.columnNamesArray, todosLosCandidatos.positiveArray, 
      todosLosCandidatos.negativeArray);
  };

  $scope.showPopularCandidatesOnly = function() {
    $scope.chart = getAllCandidatesPolarity(candidatosPopulares.columnNamesArray, candidatosPopulares.positiveArray, 
      candidatosPopulares.negativeArray);
  };

  function success(response) {
    var candidatosNoPopulares = ['CVderoux', 'MMMaldonadoC', 'RicardoAriasM', 'AlexVernot', 'danielraisbeck'];
    var polarityArray = shuffleArray(response);

    for (var i = 0; i < polarityArray.length; i++) {

      var candidatePolarity = polarityArray[i];
      todosLosCandidatos.columnNamesArray.push(candidatePolarity.twitterId);
      todosLosCandidatos.positiveArray.push(candidatePolarity.positivePolarity);
      todosLosCandidatos.negativeArray.push(candidatePolarity.negativePolarity);

      if(candidatosNoPopulares.indexOf(candidatePolarity.twitterId) === -1){
        candidatosPopulares.columnNamesArray.push(candidatePolarity.twitterId);
        candidatosPopulares.positiveArray.push(candidatePolarity.positivePolarity);
        candidatosPopulares.negativeArray.push(candidatePolarity.negativePolarity);
      }
    }
    $scope.showPopularCandidatesOnly();
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
