'use strict';

angular.module('generalAuthserverApp')
    .factory('Register', function ($resource) {
        return $resource('api/register', {}, {
        });
    });


