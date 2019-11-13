angular.module('App').controller('DashboardController', function ($rootScope, $scope, $http, $mdToast, $cookies, request) {
	if (!$rootScope.isCookieExist()) { window.location.href = '#login'; }

	var self = $scope;
	var root = $rootScope;

	root.closeAndDisableSearch();
	root.toolbar_menu = null;
	$rootScope.pagetitle = 'Dashboard';

    request.getDashboardProduct().then(function (resp) {
        self.order = resp.data.order;
        self.product = resp.data.product;
        self.category = resp.data.category;
    });

    request.getDashboardOthers().then(function (resp) {
        self.news = resp.data.news;
        self.app = resp.data.app;
        self.setting = resp.data.setting;
        self.notification = resp.data.notification;
    });

});
