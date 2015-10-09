'use strict';

boardModule.controller('tweetsController', tweetsController);
tweetsController.$inject = ['$scope', '$websocket' , '$interval', 'environment', 'targetsService', 'tweetsService'];

function tweetsController($scope, $websocket, $interval, environment, targetsService, tweetsService) {

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

  function lastTweetsCandidateSuccess(data){
    var last = JSON.parse(data[0]);
    var tweet = {
        text: last.text,
        prediction: last.prediction,
        timestamp_ms: last.timestamp_ms.$numberLong
    } ;
    for(var i = 0 ; i < $scope.candidatos.length ; i++){
      var actual = $scope.candidatos[i];
      if(actual.twitterId.id === last.targetTwitterId){
        $scope.candidatos[i].tweets = [tweet];
      }
    }
  }

  function getTargetsSuccess(data) {

    for (var i = 0; i < data.length; i++) {
      var actual = data[i];
      $scope.candidatos.push(actual);
    }

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
    targetsService.getTargets(getTargetsSuccess, onError);
    tweetsService.getAllTweetsCount(getAllTweetsCountSuccess, onError);
    
    stop = $interval(function() {
      tweetsService.getAllTweetsCount(getAllTweetsCountSuccess, onError);
    }, 30000);
    initializeWebsocket();
  };
}
