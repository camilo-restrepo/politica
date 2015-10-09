'use strict';

boardModule.controller('candidateTweetsController', candidateTweetsController);
candidateTweetsController.$inject = ['$scope', '$stateParams', '$websocket', 'environment', 'targetsService', 'tweetsService'];

function candidateTweetsController($scope, $stateParams, $websocket, environment, targetsService, tweetsService) {
  var tweetsLimit = 3;
  $scope.getTargetName = function(targetId){
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
    }else if(targetId === 'DanielRaisbeck'){
      return 'Daniel Raisbeck';
    }

    return '';
  };

  $scope.getCandidateColor = function(twitterId) {

    var colors = {
      RicardoAriasM: '#D66F13',
      MMMaldonadoC: '#FBD103',
      danielraisbeck: '#FF5C01',
      DanielRaisbeck: '#FF5C01',
      ClaraLopezObre: '#FFDF00',
      RafaelPardo: '#ED0A03',
      PachoSantosC: '#3C68B7',
      EnriquePenalosa: '#12ADE5',
      AlexVernot: '#0A5C6D',
      CVderoux: '#088543'
    };

    return colors[twitterId];
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

  function pushData(data) {
    var tweet = data;
    tweet.timestamp_ms = tweet.timestamp_ms.$numberLong;
    var tweetBelongsToCandidate = ($scope.candidate.twitterId.id === tweet.targetTwitterId);

    if (tweetBelongsToCandidate && (!tweetIsInList(tweet, $scope.candidate.tweets))) {

      $scope.tweetStats.totalTweets += 1;
      $scope.tweetStats.tweetsToday += 1;
      $scope.tweetStats.tweetsLastHour += 1;

      $scope.candidate.tweets.unshift(tweet);

      if($scope.candidate.tweets.length > tweetsLimit) {
        $scope.candidate.tweets.pop();
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

  var ws = $websocket(environment.boardWS + '/board/api/ws');
  function initializeWebsocket() {  
    ws.onMessage(function(message) {
      pushData(JSON.parse(message.data));
    });
  }

  function candidateTweetStatsSuccess(response) {
    $scope.tweetStats = response;
  }

  function singleTargetSuccess(response) {
    $scope.candidate = response;
    $scope.candidate.tweets = [];
    tweetsService.getLastTweetsCandidate({twitterId: $stateParams.twitterId}, lastTweetsCandidateSuccess, logError);
  }

  function logError(response) {
    console.error(response);
  }

  function lastTweetsCandidateSuccess(data){
    for(var i = 0 ; i < data.length ; i++){
      var actual = JSON.parse(data[i]);
      var tweet = {
        text: actual.text,
        prediction: actual.prediction,
        timestamp_ms: actual.timestamp_ms.$numberLong
      };
      $scope.candidate.tweets.push(tweet);
    }
  }

  $scope.$on("$destroy", function() {
    ws.close();
  });

  $scope.init = function() {
    var candidateTwitterId = $stateParams.twitterId;
    tweetsService.getCandidateTweetStats({ twitterId: candidateTwitterId }, candidateTweetStatsSuccess, logError);
    targetsService.getSingleTarget({ twitterId: candidateTwitterId }, singleTargetSuccess, logError);
    initializeWebsocket();
  };
}
