'use strict';

boardModule.controller('polarityController', polarityController);
polarityController.$inject = ['$scope', 'tweetsService', '$state', '$stateParams'];

function polarityController($scope, tweetsService, $state, $stateParams) {

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

  $scope.getTargetName = function(targetId) {

    var candidateNames = {
      RicardoAriasM: 'Ricardo Arias Mora',
      MMMaldonadoC: 'María Mercedes Maldonado',
      danielraisbeck: 'Daniel Raisbeck',
      ClaraLopezObre: 'Clara López Obregón',
      RafaelPardo: 'Rafael Pardo',
      PachoSantosC: 'Francisco Santos',
      EnriquePenalosa: 'Enrique Peñalosa',
      AlexVernot: 'Alex Vernot',
      CVderoux: 'Carlos Vicente de Roux',
      FicoGutierrez: 'Federico Gutiérrez',
      AlcaldeAlonsoS: 'Alonso Salazar',
      RICOGabriel: 'Gabriel Jaime Rico',
      jcvelezuribe: 'Juan Carlos Vélez'
    }

    return candidateNames[targetId];
  };

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

  var bogotaCandidates = {
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

  var medellinCandidates = {
    FicoGutierrez: '#D2EDFA',
    AlcaldeAlonsoS: '#83AC2A',
    RICOGabriel: '#F6783B',
    jcvelezuribe: '#183A64'
  };

  function getCandidatesFromCity(cityId, candidatos) {

    var candidatesFromCity = [];

    for (var i = 0; i < candidatos.length; i++) {

      var candidateColor = null;
      var candidate = candidatos[i];

      if (cityId == 'bogota') {
        candidateColor = bogotaCandidates[candidate.twitterId];
      } else {
        candidateColor = medellinCandidates[candidate.twitterId];
      }

      if (candidateColor) {
        candidatesFromCity.push(candidate);
      }
    }

    return candidatesFromCity;
  }

  function success(response) {

    var candidatesFromCity = getCandidatesFromCity($scope.cityId, response);
    var polarityArray = shuffleArray(candidatesFromCity);
    var candidatosNoPopulares = [];

    if ($scope.cityId == 'bogota') {
      candidatosNoPopulares = ['CVderoux', 'MMMaldonadoC', 'RicardoAriasM', 'AlexVernot', 'danielraisbeck'];
    }

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

    $scope.cityId = $stateParams.cityId;

    if ($scope.cityId) {
      $scope.boxIsFull = true;
      $scope.showOrHide = 'Ocultar';
      tweetsService.getAllCandidatesPolarity({time: 'day'}, success, error);
    } else {
      $state.go('select');
    }
  };
}
