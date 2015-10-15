'use strict';

boardModule.controller('tweetsController', tweetsController);
tweetsController.$inject = ['$scope', '$websocket' , '$interval', 'environment', 'targetsService', 'tweetsService', '$state',
  '$stateParams'];

function tweetsController($scope, $websocket, $interval, environment, targetsService, tweetsService, $state, $stateParams) {

  $scope.candidatos = [];
  var stop;

  function compareTweets(tweet1, tweet2) {
    return (tweet1.text === tweet2.text) && (tweet1.userId === tweet2.userId); 
  }

  function tweetIsInList(tweet, tweetList) {
    var isInList = false;
    for (var i = 0; i < tweetList.length && !isInList; i++) {
      isInList = compareTweets(tweet, tweetList[i]);
    }
    return isInList;
  }

  function pushData(data) {

    var tweet = data;
    var tweetsLimit = 1;
    tweet.timestamp_ms = tweet.timestamp_ms.$numberLong;
    $scope.tweetsCount = $scope.tweetsCount + 1;
    $scope.tweetsPerMinute = $scope.tweetsPerMinute + 1;

    for(var i = 0; i < $scope.candidatos.length; i++) {
      if($scope.candidatos[i].twitterId.id === tweet.targetTwitterId) {
        if (!tweetIsInList(tweet, $scope.candidatos[i].tweets)) {
          $scope.candidatos[i].tweets.push(tweet);
          if($scope.candidatos[i].tweets.length > tweetsLimit) {
            $scope.candidatos[i].tweets.shift();
          }
        }
      }
    }

    $scope.$apply();
  }

  $scope.getTweetLabelClass = function(predictionValue) {

    var labelClass = 'label-warning';

    if (predictionValue === 'negative') {
      labelClass = 'label-danger';
    } else if (predictionValue === 'positive') {
      labelClass = 'label-success';
    }

    return labelClass;
  };

  $scope.getTweetLabelText = function(predictionValue) {

    var labelText = 'Neutro ::';

    if (predictionValue === 'negative') {
      labelText = 'Negativo >:(';
    } else if (predictionValue === 'positive') {
      labelText = 'Positivo :)';
    }

    return labelText;
  };

  function onError(data){
    console.log(data);
  }

  function shuffleArray(o) {
    for(var j, x, i = o.length; i; j = Math.floor(Math.random() * i), x = o[--i], o[i] = o[j], o[j] = x);
    return o;
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

  function lastTweetsCandidateSuccess(data){
    var last = JSON.parse(data[0]);
    var tweet = {
      text: last.text,
      prediction: last.prediction,
      timestamp_ms: last.timestamp_ms.$numberLong
    };

    for(var i = 0 ; i < $scope.candidatos.length ; i++){
      var actual = $scope.candidatos[i];
      if(actual.twitterId.id === last.targetTwitterId){
        $scope.candidatos[i].tweets = [tweet];
      }
    }
  }

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
        candidateColor = bogotaCandidates[candidate.twitterId.id];
      } else {
        candidateColor = medellinCandidates[candidate.twitterId.id];
      }

      if (candidateColor) {
        candidatesFromCity.push(candidate);
      }
    }

    return candidatesFromCity;
  }

  function getTargetsSuccess(data) {

    for (var i = 0; i < data.length; i++) {
      var actual = data[i];
      $scope.candidatos.push(actual);
    }

    $scope.candidatos = getCandidatesFromCity($scope.cityId, $scope.candidatos);

    for(var i = 0 ; i < $scope.candidatos.length ; i++){
      var actual = $scope.candidatos[i];
      tweetsService.getLastTweetsCandidate({twitterId: actual.twitterId.id}, lastTweetsCandidateSuccess, onError);
    }

    $scope.candidatos = shuffleArray($scope.candidatos);
  }

  function getAllTweetsCountSuccess(data){
    $scope.tweetsCount = data.count;
    $scope.tweetsPerMinute = data.perMinute;
  }

  var ws = $websocket(environment.boardWS + '/board/api/ws');

  function initializeWebsocket() {  
    ws.onMessage(function(message) {
      pushData(JSON.parse(message.data));
    });
  }

  $scope.$on("$destroy", function() {
    ws.close();
  });

  $scope.init = function() {

    $scope.cityId = $stateParams.cityId;

    if ($scope.cityId) {

      targetsService.getTargets(getTargetsSuccess, onError);
      tweetsService.getAllTweetsCount(getAllTweetsCountSuccess, onError);

      stop = $interval(function() {
        tweetsService.getAllTweetsCount(getAllTweetsCountSuccess, onError);
      }, 30000);

      initializeWebsocket();

    } else {

      $state.go('select');
    }
  };
}
