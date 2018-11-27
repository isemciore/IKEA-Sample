var demo = angular.module('demo', []);

// var application = angular.module('application', [])

demo.controller('Background', function ($scope, $http, $location) {
    $scope.raw_data = 'n/a';
    $scope.raw_status = 'n/a';
    $scope.raw_header = 'n/a';
    $scope.blub = '12312312';
    $scope.overview_customer = 'n/a';
    $scope.host_port_url = "http://".concat($location.host(), ":", $location.port());

    $scope.bg_logger = function(data, status, header){
        $scope.raw_data = data;
        $scope.raw_status = status;
        $scope.raw_header = header;
    };

});

demo.controller('PostObject', function($scope, $http){
    $scope.counter = 1;
    $scope.increment = function () {
        $scope.counter++;
    };

    $scope.addCustomer = function(){
        url_customer = $scope.host_port_url.concat('/customer');
        $scope.blub = url_customer;
        var newCustomer =
            {
                "name": $scope.customer_name,
                "hidden": false,
            };
        var parameter = JSON.stringify(newCustomer);
        $http.post(url_customer, parameter).
            success(function(data, status, headers, config) {
                $scope.bg_logger(data, status, headers);
                console.log('success');
            }).
            error(function(data, status, headers, config) {
                $scope.bg_logger(data, status, headers);
                console.log('fail');
            });
    };
    $scope.addShoppingList = function (){
        var customer_id = $scope.customer_id.toString();
        url_shopping_list = $scope.host_port_url.concat('/customer/', customer_id);
        var newShoppingList =
            {
                "name": $scope.shopping_list_name
            };

        var parameter = JSON.stringify(newShoppingList);
        $http.post(url_shopping_list, parameter).
            success(function(data, status, headers, config) {
                $scope.bg_logger(data, status, headers);
                console.log('success');
            }).
            error(function(data, status, headers, config) {
                $scope.bg_logger(data, status, headers);
                console.log('fail');
            });

    };

    $scope.addItem = function(){
        url_item = $scope.host_port_url.concat('/item');
        var newItem =
            {
                "name": $scope.item_name,
                "itemIdentification": $scope.item_identification
            };
        var parameter = JSON.stringify(newItem);
        $http.post(url_item, parameter).
            success(function(data, status, headers, config) {
                $scope.bg_logger(data, status, headers);
                console.log('success');
            }).
            error(function(data, status, headers, config) {
                $scope.bg_logger(data, status, headers);
                console.log('fail');
            });
    };

    $scope.addItemToShoppingList = function(){
        new_item_url = $scope.host_port_url.concat('/shopping_list/', $scope.add_shopping_list_id,
                                                   '/item/', $scope.add_item_id);
        var newAmount = {
            "amount": $scope.item_amount,
        };
        var parameter = JSON.stringify(newAmount);
        $http.post($scope.host_port_url.concat('/shopping_list/2000/item/3001'), parameter).
            success(function(data, status, headers, config) {
                $scope.bg_logger(data, status, headers);
                console.log('success');
            }).
            error(function(data, status, headers, config) {
                $scope.bg_logger(data, status, headers);
                console.log('fail');
            });
    }
});


demo.controller('GetData', function($scope, $http){
    url_customer = $scope.host_port_url.concat('/customer/');
    $scope.blub = url_customer
    $scope.updateOverview = function () {
        $http.get(url_customer).then(function (response) {
            $scope.overview_customer = response.data;
        });
    };
});