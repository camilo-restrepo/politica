'use strict';

boardModule.controller('candidateTweetsController', candidateTweetsController);
candidateTweetsController.$inject = ['$scope', '$stateParams', '$websocket', 'targetsService', 'tweetsService'];

function candidateTweetsController($scope, $stateParams, $websocket, targetsService, tweetsService) {

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

      $scope.candidate.tweets.push(tweet);

      if($scope.candidatos[i].tweets.length > 5) {
        $scope.candidatos[i].tweets.shift();
      }
    }

    $scope.$apply();
  }

  function initializeWebsocket() {

    var ws = $websocket.$new({
      url: 'ws://104.236.26.163:9001/board/api/ws',
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
