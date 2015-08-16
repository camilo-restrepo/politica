'use strict';

/**
 * @ngdoc function
 * @name webClassifierWebApp.controller:MainCtrl
 * @description
 * # MainCtrl
 * Controller of the webClassifierWebApp
 */
angular.module('webClassifierWebApp').controller('MainCtrl', function ($scope, TweetsService, TweetService) {
    
    $scope.tweets = [];

    function onError(data){
    	console.error('Error: ' + JSON.stringify(data));
    }

    function onSuccessInit(data){
    	$scope.tweets = data;
    }

    $scope.init = function(){
    	TweetsService.getTweets(onSuccessInit, onError);
    };

    function updateTweetOnSuccess(response){
        console.debug('updateTweetSuccess');
        console.debug(JSON.stringify(response));
    }

    function onSuccess(data){
        $scope.tweets.push(data[0]);
    }

    $scope.setPolarity = function(tweet, polarity, index){
    	tweet.polarity = polarity;
        console.log(tweet);
        TweetService.updateTweet({id: tweet.id}, tweet, updateTweetOnSuccess, onError);
    	$scope.tweets.splice(index, 1);
        TweetsService.getTweets({offset: 10, limit: 1}, onSuccess, onError);
    };

    $scope.ignore = function(tweet, index){
        TweetService.deleteTweet({id: tweet.id}, updateTweetOnSuccess, onError);
        TweetsService.getTweets({offset: 10, limit: 1}, onSuccess, onError);
        $scope.tweets.splice(index, 1);
    };
});
