'use strict';

boardModule.controller('aboutController', aboutController);
aboutController.$inject = ['$scope', 'emailService'];

function aboutController($scope, emailService) {

  $scope.showConfirmationBox = false;

  function showConfirmationBox() {
    $scope.contactForm = {};
    $scope.showConfirmationBox = true;
  }

  $scope.sendEmail = function(contactForm) {

    if (contactForm.name && contactForm.email && contactForm.message) {

      var email = {
        recipients: [ 'elpisco@gmail.com', 'julianespinel@gmail.com' ],
        subject: 'No Creo: contact form',
        body: (' Name: ' + contactForm.name + '\n Email: ' + contactForm.email + '\n Message: ' + contactForm.message)
      };

      var emailSendPromise = emailService.sendEmail(email).$promise;
      emailSendPromise.then(showConfirmationBox());
    }
  };
}
