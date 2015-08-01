'use strict';

boardModule.controller('boardController', boardController);
boardController.$inject = ['$scope', '$websocket', 'TargetsService'];

function boardController($scope, $websocket, TargetsService) {
  $scope.candidatos = [];

  var ws = $websocket.$new({
    url: 'ws://localhost:9001/board/api/ws',
    protocols: []
  }); 

  ws.$on('$open', function () {
    console.log('Oh my gosh, websocket is really open! Fukken awesome!');
  });

  ws.$on('$message', function (data) {
    pushData(data);
    //ws.$close();
  });

  ws.$on('$close', function () {
    console.log('Noooooooooou, I want to have more fun with ngWebsocket, damn it!');
  });

  ws.$open();

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

  function pushData(data){
    var tweet = data;
    console.log(tweet);
    for(var i = 0; i < $scope.candidatos.length; i++){
      if($scope.candidatos[i].twitterId.id === tweet.targets[0]){
        if (!tweetIsInList(tweet, $scope.candidatos[i].tweets)) {
          $scope.candidatos[i].tweets.push(tweet);
          if($scope.candidatos[i].tweets.length > 5){
            $scope.candidatos[i].tweets.shift();
          }
        }
      }
    }
    $scope.$apply();
  }

  function onError(data){
    console.log(data);
  }

  function onSuccess(data){
    for (var i = 0; i < data.length; i++) {
      $scope.candidatos.push(data[i]);
    }

    for (i = 0; i < $scope.candidatos.length; i++) {
      $scope.candidatos[i].tweets = [];
    }
  }

  $scope.init = function(){
    TargetsService.getTargets(onSuccess, onError);
  };
}
