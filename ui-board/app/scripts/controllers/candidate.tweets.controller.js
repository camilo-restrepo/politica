'use strict';

boardModule.controller('candidateTweetsController', candidateTweetsController);
candidateTweetsController.$inject = ['$scope', '$stateParams', '$websocket', 'environment', 'targetsService', 'tweetsService'];

function candidateTweetsController($scope, $stateParams, $websocket, environment, targetsService, tweetsService) {

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
    var tweetsLimit = 5;
    tweet.timestamp_ms = tweet.timestamp_ms;
    var tweetBelongsToCandidate = ($scope.candidate.twitterId.id === tweet.targetTwitterId);

    if (tweetBelongsToCandidate && (!tweetIsInList(tweet, $scope.candidate.tweets))) {

      $scope.tweetStats.totalTweets += 1;
      $scope.tweetStats.tweetsToday += 1;
      $scope.tweetStats.tweetsLastHour += 1;

      $scope.candidate.tweets.push(tweet);

      if($scope.candidatos[i].tweets.length > tweetsLimit) {
        $scope.candidatos[i].tweets.shift();
      }
    }

    $scope.$apply();
  }

  function initializeWebsocket() {

    var ws = $websocket.$new({
      url: environment.boardWS + '/board/api/ws',
      protocols: []
    }); 

    ws.$on('$open', function () {
      //console.log('open');
    });

    ws.$on('$message', function (data) {
      pushData(data);
      //ws.$close();
    });

    ws.$on('$close', function () {
      //console.log('close');
    });

    ws.$open();
  }

  function candidateTweetStatsSuccess(response) {
    $scope.tweetStats = response;
  }

  function singleTargetSuccess(response) {
    $scope.candidate = response;
    $scope.candidate.tweets = [];
    initializeWebsocket();
  }

  function logError(response) {
    console.error(response);
  }

  $scope.init = function() {

    var candidateTwitterId = $stateParams.twitterId;

    console.debug('candidateTweetsController, candidateTwitterId: ' + candidateTwitterId);

    tweetsService.getCandidateTweetStats({ twitterId: candidateTwitterId }, candidateTweetStatsSuccess, logError);
    targetsService.getSingleTarget({ twitterId: candidateTwitterId }, singleTargetSuccess, logError);
  }
}
