'use strict';

boardModule.controller('dialogController', dialogController);
dialogController.$inject = ['$scope', '$cookies','ngDialog'];

function dialogController($scope, $cookies, ngDialog) {
  function showInitialDialog() {
    var open = $cookies.get('dialog');
    if(!open){
      ngDialog.open({
        template: 'views/initial-dialog.html',
        scope: $scope
      });
      $cookies.put('dialog', true);
    }
  }

  $scope.closeDialog = function(){
    ngDialog.closeAll();
  };

  $scope.init = function() {
    showInitialDialog();
  };
}
