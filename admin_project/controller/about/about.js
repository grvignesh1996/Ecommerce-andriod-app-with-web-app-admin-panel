angular.module('App').controller('AboutController', function ($rootScope, $scope, $http, $mdToast, $cookies) {
	var self = $scope;
	var root = $rootScope;

	if (!root.isCookieExist()) { window.location.href = '#login'; }

	root.closeAndDisableSearch();
	root.toolbar_menu = null;
	$rootScope.pagetitle = 'About';

	self.PANEL_NAME = root.PANEL_NAME;
    self.PANEL_VERSION = root.PANEL_VERSION;

});
