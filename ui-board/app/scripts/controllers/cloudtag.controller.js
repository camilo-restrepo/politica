'use strict';

boardModule.controller('cloudtagController', cloudtagController);
cloudtagController.$inject = ['$scope', 'cloudtagService'];

function cloudtagController($scope, cloudtagService) {
  var popularCandidateWords = [];
  var allWords = [];
  
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

  function success(response) {
    var minSize = 1000000;
    var maxSize = 0;
    var candidatosNoPopulares = ['CVderoux', 'MMMaldonadoC', 'RicardoAriasM', 'AlexVernot', 'danielraisbeck'];

    for(var i = 0 ; i < response.length ; i++){
      var element = response[i];
      var candidateList = element.wordCountList;
      for(var j = 0 ; j < candidateList.length ; j++){
        var wordCount = candidateList[j];
        var word = {
          word: ' '+wordCount.word+' ',
          size: wordCount.count,
          color: getCandidateColor(element.twitterId)
        };
        minSize = Math.min(minSize, wordCount.count);
        maxSize = Math.max(maxSize, wordCount.count);
        allWords.push(word);
        if(candidatosNoPopulares.indexOf(element.twitterId) === -1){
          popularCandidateWords.push(word);
        }
      }
    }

    for(var i = 0 ; i < popularCandidateWords.length ; i++){
      var w = popularCandidateWords[i];
      w.size = scale(w.size, minSize, maxSize);
    }

    $scope.words = shuffleArray(popularCandidateWords);
  }

  $scope.showAllCandidates = function() {
    $scope.words = shuffleArray(allWords);
    $scope.isTransparent = false;
    //$scope.$apply()
  };

  $scope.showPopularCandidatesOnly = function() {
    $scope.words = shuffleArray(popularCandidateWords);
    $scope.isTransparent = true;
    //$scope.$apply()
  };

  function error(response) {
    console.error(response);
  }

  $scope.changeMessageBoxState = function() {
    $scope.boxIsFull = !$scope.boxIsFull;
    $scope.showOrHide = $scope.boxIsFull ? 'Ocultar' : "Mostrar";
  };

  function initCandidatesLegend(){
    $scope.candidates = [
      {name: 'RicardoAriasM', color:'#D66F13', x:0, y:0, popular: false},
      {name: 'MMMaldonadoC', color:'#FBD103' , x:0, y:30, popular: false},
      {name: 'danielraisbeck', color:'#FF5C01' , x:0, y:60, popular: false},
      {name: 'ClaraLopezObre', color:'#FFDF00' , x:0, y:90, popular: true},
      {name: 'RafaelPardo', color:'#ED0A03' , x:0, y:120, popular: true},
      {name: 'PachoSantosC', color:'#3C68B7' , x:0, y:150, popular: true},
      {name: 'EnriquePenalosa', color:'#12ADE5' , x:0, y:180, popular: true},
      {name: 'AlexVernot', color:'#0A5C6D' , x:0, y:210, popular: false},
      {name: 'CVderoux', color:'#088543' , x:0, y:240, popular: false}
    ];
    $scope.candidates = shuffleArray($scope.candidates);
    var y = 0;
    for(var i = 0 ; i < $scope.candidates.length ; i++){
      $scope.candidates[i].y = y;
      y = y + 20;
    }
  }

  $scope.isTransparent2 = function(candidate){

    if($scope.isTransparent && !candidate.popular){
      return 'svg-cloudtag-opacity';
    }
    return '';
  }

  $scope.init = function() {
    initCandidatesLegend();
    $scope.isTransparent = true;
    $scope.boxIsFull = true;
    $scope.showOrHide = 'Ocultar';
    cloudtagService.getAllCandidatesCloudTags(success, error);
  };
}
