'use strict';

boardModule.controller('candidateController', candidateController);
candidateController.$inject = ['$scope', '$stateParams', 'tweetsService', 'cloudtagService'];

function candidateController($scope, $stateParams, tweetsService, cloudtagService) {

  function tweetStatsSuccess(response) {

    
  }
  
  $scope.init = function() {

    var candidateTwitterId = $stateParams.twitterId;
    tweetsService.getCandidateTweetStats(candidateTwitterId, tweetStatsSuccess, tweetStatsError);
    cloudtagService.getCandidateCloudTag(candidateTwitterId, cloudTagSuccess, cloudTagError)
  }
}
