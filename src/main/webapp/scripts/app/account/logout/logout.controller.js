'use strict';

angular.module('generalAuthserverApp')
    .controller('LogoutController', function (Auth) {
        Auth.logout();
    });
