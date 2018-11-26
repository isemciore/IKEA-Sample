var demo = angular.module('demo', []);

// var application = angular.module('application', [])

demo.controller('Background', function ($scope, $http) {
    $scope.raw_data = 'n/a';
    $scope.raw_status = 'n/a';
    $scope.raw_header = 'n/a';
    $scope.blub = '12312312';
    $scope.overview_customer = 'n/a';
});

demo.controller('PostObject', function($scope){
    $scope.counter = 1;
    $scope.increment = function () {
        $scope.counter++;
    };
});
