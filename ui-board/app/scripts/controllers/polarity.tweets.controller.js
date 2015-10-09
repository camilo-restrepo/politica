'use strict';

boardModule.controller('polarityTweetsController', polarityTweetsController);
polarityTweetsController.$inject = ['$scope', '$stateParams', '$websocket', 'environment', 'tweetsService'];

function polarityTweetsController($scope, $stateParams, $websocket, environment, tweetsService) {
  var tweetsLimit = 9;
  $scope.polarity = '';

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

  function lastTweetsPolarity(data){
    $scope.tweets = [];
    for(var i = 0 ; i < data.length ; i++){
      var json = JSON.parse(data[i]);
      var tweet = {
        text: json.text,
        prediction: json.prediction,
        timestamp_ms: json.timestamp_ms.$numberLong,
        twitterId: json.targetTwitterId
      };

      $scope.tweets.push(tweet);
    }
    $scope.tweets = shuffleArray($scope.tweets);
  }

  $scope.init = function() {
    $scope.polarity = $stateParams.prediction;
    tweetsService.getLastTweetsPolarity({prediction: $scope.polarity}, lastTweetsPolarity, logError);
    initializeWebsocket();
  };
}
