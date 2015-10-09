'use strict';

boardModule.controller('dialogController', dialogController);
dialogController.$inject = ['$scope', 'ngDialog'];


function dialogController($scope, ngDialog) {

  var opened = false;

  function showInitialDialog() {
    ngDialog.open({
      template: 'views/initial-dialog.html',
      scope: $scope
    });
  }

  $scope.closeDialog = function(){
    ngDialog.closeAll();
  };

  $scope.init = function() {
    showInitialDialog();
  };
}
