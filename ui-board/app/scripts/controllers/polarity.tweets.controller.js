'use strict';

boardModule.controller('polarityTweetsController', polarityTweetsController);
polarityTweetsController.$inject = ['$scope', '$stateParams', '$websocket', 'environment', 'tweetsService', '$state'];

function polarityTweetsController($scope, $stateParams, $websocket, environment, tweetsService, $state) {
  var tweetsLimit = 9;
  $scope.polarity = '';

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

  function shuffleArray(o) {
    for(var j, x, i = o.length; i; j = Math.floor(Math.random() * i), x = o[--i], o[i] = o[j], o[j] = x);
    return o;
  }

  function pushData(data) {
    console.log(data);
    var tweet = data;
    tweet.timestamp_ms = tweet.timestamp_ms.$numberLong;
    var tweetBelongsToPolarity = ($scope.polarity === tweet.prediction);

    if (tweetBelongsToPolarity && (!tweetIsInList(tweet, $scope.tweets))) {
      var lastTweet = {
        text: tweet.text,
        prediction: tweet.prediction,
        timestamp_ms: tweet.timestamp_ms,
        twitterId: tweet.targetTwitterId
      };
      $scope.tweets.unshift(lastTweet);
      if($scope.tweets.length > tweetsLimit) {
        $scope.tweets.pop();
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

  $scope.getTweetLabelClassNoHover = function(predictionValue) {
    var labelClass = 'label-warning-no-hover';
    if (predictionValue === 'negative') {
      labelClass = 'label-danger-no-hover';
    } else if (predictionValue === 'positive') {
      labelClass = 'label-success-no-hover';
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

  function initializeWebsocket() {
    var ws = $websocket(environment.boardWS + '/board/api/ws');
    ws.onMessage(function(message) {
      pushData(JSON.parse(message.data));
    });
  }

  function logError(response) {
    console.error(response);
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

  function lastTweetsPolarity(data) {

    $scope.tweets = [];

    for(var i = 0 ; i < data.length ; i++){

      var json = JSON.parse(data[i]);

      var tweet = {
        text: json.text,
        prediction: json.prediction,
        timestamp_ms: json.timestamp_ms.$numberLong,
        twitterId: json.targetTwitterId
      };

      var isCandidateFromCity = false;

      console.debug('*********************************** 1: ' + $scope.cityId);
      console.debug('*********************************** 2: ' + tweet.twitterId);

      if ($scope.cityId == 'bogota') {
        isCandidateFromCity = (bogotaCandidates[tweet.twitterId.id]);
      } else {
        isCandidateFromCity = (medellinCandidates[tweet.twitterId.id]);
      }

      if (isCandidateFromCity) {
        $scope.tweets.push(tweet);
      }
    }

    $scope.tweets = shuffleArray($scope.tweets);
  }

  $scope.init = function() {

    $scope.cityId = $stateParams.cityId;

    if ($scope.cityId) {
      $scope.polarity = $stateParams.prediction;
      tweetsService.getLastTweetsPolarity({prediction: $scope.polarity}, lastTweetsPolarity, logError);
      initializeWebsocket();
    } else {
      $state.go('select');
    }

  };
}
