'use strict';

boardModule.controller('candidateCloudtagController', candidateCloudtagController);
candidateCloudtagController.$inject = ['$scope', '$stateParams', 'cloudtagService'];

function candidateCloudtagController($scope, $stateParams, cloudtagService) {

  function getCandidateColor(candidateTwitterId) {
    var colors = {
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
    var col = colors[candidateTwitterId];
    return col;
  }

  function scale(value, min, max){
    var newValue = 1 + (((value - min)*(10 - 1))/(max-min));
    return Math.ceil(newValue);
  }

  function shuffleArray(o) {
    for(var j, x, i = o.length; i; j = Math.floor(Math.random() * i), x = o[--i], o[i] = o[j], o[j] = x);
    return o;
  }

  function cloudTagSuccess(response) {
    var minSize = 1000000;
    var maxSize = 0;
    var allWords = [];
    var wordCountList = response.wordCountList;
    for(var i = 0 ; i < wordCountList.length ; i++){
      var wordCount = wordCountList[i];
      var word = {
        word: ' '+wordCount.word+' ',
        size: wordCount.count,
        color: getCandidateColor($stateParams.twitterId)
      };
      minSize = Math.min(minSize, wordCount.count);
      maxSize = Math.max(maxSize, wordCount.count);
      allWords.push(word);
    }

    for(var i = 0 ; i < allWords.length ; i++){
       var w = allWords[i];
       w.size = scale(w.size, minSize, maxSize);
    }

    $scope.words = shuffleArray(allWords);
  }

  function cloudTagError(response) {
    console.error(response);
  }

  $scope.init = function() {
    var candidateTwitterId = $stateParams.twitterId;
    cloudtagService.getCandidateCloudTag({ twitterId: candidateTwitterId, limit: 30 }, cloudTagSuccess, cloudTagError);
  };
}
