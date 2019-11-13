angular.module('App').controller('NewsController', function ($rootScope, $scope, $http, $mdToast, $mdDialog, $route, request) {
	// not login checker
	if (!$rootScope.isCookieExist()) { window.location.href = '#login'; }

	var self = $scope;
	var root = $rootScope;
	
	root.pagetitle = 'News Info';
	self.loading = true;

	root.search_enable = true;
	root.toolbar_menu = { title: 'Add News' }

	// receiver barAction from rootScope
	self.$on('barAction', function (event, data) {		
		root.setCurNewsInfoId("");
		window.location.href = '#create_news';
	});
	
	// receiver submitSearch from rootScope
	self.$on('submitSearch', function (event, data) {
		self.q = data;
		self.loadPages();
	});
	
	// load pages from database and display
	self.loadPages = function () {
		$_q = self.q ? self.q : '';
		request.getAllNewsInfoCount($_q).then(function (resp) {
			self.paging.total = Math.ceil(resp.data / self.paging.limit);
			self.paging.modulo_item = resp.data % self.paging.limit;
		});
		$limit = self.paging.limit;
		$current = (self.paging.current * self.paging.limit) - self.paging.limit + 1;
		if (self.paging.current == self.paging.total && self.paging.modulo_item > 0) {
			self.limit = self.paging.modulo_item;
		}
		request.getAllNewsInfoByPage($current, $limit, $_q).then(function (resp) {
			self.news = resp.data;
			self.loading = false;
		});
		
	};

	// pagination property
	self.paging = {
		total: 0, // total whole item
		current: 1, // start page
		step: 3, // count number display
		limit: 30, // max item per page
		modulo_item: 0,
		onPageChanged: self.loadPages,
	};
	
	self.editNewsInfo = function(ev, n) {
		root.setCurNewsInfoId(n.id);
		window.location.href = '#create_news';
	};
	
	self.detailsNewsInfo = function(ev, n) {
		$mdDialog.show({
			controller          : DetailsNewsInfoControllerDialog,
			templateUrl         : 'view/news/details.html',
			parent              : angular.element(document.body),
			targetEvent         : ev,
			clickOutsideToClose : true,
			news             	: n
		})
	};

	self.deleteNewsInfo = function(ev, n) {
		var confirm = $mdDialog.confirm().title('Delete Confirmation');
			confirm.content('Are you sure want to delete News Info : '+n.title+' ?');
			confirm.targetEvent(ev).ok('OK').cancel('CANCEL');
			
		var dir = "../../../uploads/news/";
		var images_obj = new Array();	
		images_obj.push(n.image);
		$mdDialog.show(confirm).then(function() {
			request.deleteOneNewsInfo(n.id).then(function(resp){
				if(resp.status == 'success'){
					request.deleteFiles(dir, images_obj).then(function(res){ });
				    root.showConfirmDialogSimple('', 'Delete News Info '+n.title+' <b>Success</b>!', function(){
				        window.location.reload();
				    });
				}else{
				    var failed_txt = '';
                    if(resp.msg != null) failed_txt += '<br>' + resp.msg;
                    root.showInfoDialogSimple('Delete Failed', failed_txt);
				}
			});
		});

	};

    /* dialog Publish confirmation*/
    self.publishDialog = function (ev, o) {
        $mdDialog.show({
            controller : PublishNewsInfoDialogCtl,
            parent: angular.element(document.body), targetEvent: ev, clickOutsideToClose: true, obj: o,
            template:
            '<md-dialog ng-cloak aria-label="publishData">' +
            '  <md-dialog-content>' +
            '   <h2 class="md-title">Publish Confirmation</h2> ' +
            '   <p>Are you sure want to publish News Info : <b>{{obj.title}}</b> ?</p><br>' +
            '   <md-checkbox ng-model="send_notif">Send Notification to users</md-checkbox>' +
            '   <div layout="row"> <span flex></span>' +
            '       <md-button ng-if="!submit_loading" class="md-warn" ng-click="cancel()" >CANCEL</md-button>' +
            '       <md-button ng-click="publish()" class="md-raised md-primary">YES</md-button>' +
            '   </div>' +
            '  </md-dialog-content>' +
            '</md-dialog>'
        });
        function PublishNewsInfoDialogCtl($scope, $mdDialog, $mdToast, obj) {
            $scope.obj = angular.copy(obj);
            $scope.cancel = function() { $mdDialog.cancel(); };
            $scope.publish = function() {
                $scope.obj.draft = 0;
                request.updateOneNewsInfo($scope.obj.id, $scope.obj).then(function(resp){
                    if(resp.status == 'success'){
                        if($scope.send_notif) $scope.sendNotification(obj);
                        root.showConfirmDialogSimple('', 'Publish News Info '+obj.title+' <b>Success</b>!', function(){
                            window.location.reload();
                        });
                    }else{
                        var failed_txt = 'Opps , <b>Failed Publish</b> News Info '+obj.title;
                        if(resp.msg != null) failed_txt = resp.msg;
				        root.showInfoDialogSimple('', failed_txt);
                    }
                });
            };

            /* for notification when publish*/
            $scope.sendNotification = function(obj){
                var title  = obj.title;
                var content  = obj.brief_content;
                var body = root.getNotificationBody('NEWS_INFO', obj, content, null);
                body.data.title = title;
                root.requestPostNotification(body, function(resp){});
            }
        }
    };
	
});

function DetailsNewsInfoControllerDialog($scope, $mdDialog, request, $mdToast, $route, news) {
	var self    = $scope;
	self.news   = news;
	self.hide   = function() { $mdDialog.hide(); };
	self.cancel = function() { $mdDialog.cancel(); };
}

