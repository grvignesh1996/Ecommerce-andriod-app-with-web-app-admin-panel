angular.module('App').controller('ProductController', function ($rootScope, $scope, $http, $mdToast, $mdDialog, $route, request) {
	var self = $scope;
	var root = $rootScope;

	// not login checker
	if (!root.isCookieExist()) { window.location.href = '#login'; }
	
	root.pagetitle = 'Product';
	self.loading = true;
	self.category_id = -1;
	self.max_item = 20;
	self.max_item_array = [];
	for(var i = 1; i<5; i++){
	    var _value = 20*i;
	    var _text = _value+" items";
	    self.max_item_array.push({value:_value, text:_text});
	}

	root.search_enable = true;
	root.toolbar_menu = { title: 'Add Product' };

	// receiver barAction from rootScope
	self.$on('barAction', function (event, data) {		
		root.setCurProductId("");
		window.location.href = '#create_product';
	});
	
	// receiver submitSearch from rootScope
	self.$on('submitSearch', function (event, data) {
		self.q = data;
		self.loadPages();
	});

	request.getAllCategory().then(function(resp){
		var temp_category = {id:-1, name:'All Category'};
		self.categories_data = resp.data;
		self.categories_data.unshift(temp_category);
	});
	
	// load pages from database and display
	self.loadPages = function () {
		$_q = self.q ? self.q : '';
		self.paging.limit = self.max_item;
		request.getAllProductCount($_q, self.category_id).then(function (resp) {
			self.paging.total = Math.ceil(resp.data / self.paging.limit);
			self.paging.modulo_item = resp.data % self.paging.limit;
		});
		$limit = self.paging.limit;
		$current = (self.paging.current * self.paging.limit) - self.paging.limit + 1;
		if (self.paging.current == self.paging.total && self.paging.modulo_item > 0) {
			self.limit = self.paging.modulo_item;
		}
		request.getAllProductByPage($current, $limit, $_q, self.category_id).then(function (resp) {
			self.product = resp.data;
			self.loading = false;
		});
		
	};

	// pagination property
	self.paging = {
		total: 0, // total whole item
		current: 1, // start page
		step: 3, // count number display
		limit: self.max_item, // max item per page
		modulo_item: 0,
		onPageChanged: self.loadPages,
	};
	
	self.editProduct = function(ev, p) {
		root.setCurProductId(p.id);
		window.location.href = '#create_product';
	};
	
	self.detailsProduct = function(ev, p) {
		$mdDialog.show({
			controller          : DetailsProductControllerDialog,
			templateUrl         : 'view/product/details.html',
			parent              : angular.element(document.body),
			targetEvent         : ev,
			clickOutsideToClose : true,
			product             : p
		})
	};

	self.deleteProduct = function(ev, p) {
		var confirm = $mdDialog.confirm().title('Delete Confirmation');
			confirm.content('Are you sure want to delete Product : '+p.name+' ?');
			confirm.targetEvent(ev).ok('OK').cancel('CANCEL');
			
		var dir = "../../../uploads/product/";
		var images_obj = new Array();	
		images_obj.push(p.image);
		request.getAllProductImageByProductId(p.id).then(function(resp){
			for (var i = 0; i < resp.data.length; i++) {
				images_obj.push(resp.data[i].name);
			}
		});
		
		$mdDialog.show(confirm).then(function() {
			request.deleteOneProduct(p.id).then(function(res){
				if(res.status == 'success'){
					request.deleteFiles(dir, images_obj).then(function(res){ });
                    root.showConfirmDialogSimple('', 'Delete Product '+p.name+' <b>Success</b>!', function(){
                        window.location.reload();
                    });
				}else{
                    root.showInfoDialogSimple('', 'Opps , <b>Failed Delete</b> Product '+p.name);
				}
			});
		});

	};

    /* dialog Publish confirmation*/
    self.publishDialog = function (ev, o) {
        $mdDialog.show({
            controller : PublishProductDialogCtl,
            parent: angular.element(document.body), targetEvent: ev, clickOutsideToClose: true, obj: o,
            template:
            '<md-dialog ng-cloak aria-label="publishData">' +
            '  <md-dialog-content>' +
            '   <h2 class="md-title">Publish Confirmation</h2> ' +
            '   <p>Are you sure want to publish Product : <b>{{obj.name}}</b> ?</p><br>' +
            '   <md-checkbox ng-model="send_notif">Send Notification to users</md-checkbox>' +
            '   <div layout="row"> <span flex></span>' +
            '       <md-button ng-if="!submit_loading" class="md-warn" ng-click="cancel()" >CANCEL</md-button>' +
            '       <md-button ng-click="publish()" class="md-raised md-primary">YES</md-button>' +
            '   </div>' +
            '  </md-dialog-content>' +
            '</md-dialog>'
        });
        function PublishProductDialogCtl($scope, $mdDialog, $mdToast, obj) {
        	$scope.obj = angular.copy(obj);
        	$scope.cancel = function() { $mdDialog.cancel(); };
        	$scope.publish = function() {
        	    $scope.obj.draft = 0;
                request.updateOneProduct($scope.obj.id, $scope.obj).then(function(resp){
                    if(resp.status == 'success'){
                        if($scope.send_notif) $scope.sendNotification(obj);
                        root.showConfirmDialogSimple('', 'Publish Product '+obj.name+' <b>Success</b>!', function(){
                            window.location.reload();
                        });
                    }else{
                        var failed_txt = 'Opps , <b>Failed Publish</b> Product '+obj.name;
                        if(resp.msg != null) failed_txt = resp.msg;
                        root.showInfoDialogSimple('', failed_txt);
                    }
                });
        	};

            /* for notification when publish*/
            $scope.sendNotification = function(obj){
                var content  = root.PRODUCT_NEW + obj.name;
                var body = root.getNotificationBody('PRODUCT', obj, content, null);
                root.requestPostNotification(body, function(resp){});
            }
        }
    };
	
});

function DetailsProductControllerDialog($scope, $mdDialog, request, $mdToast, $route, product) {
	var self        = $scope;
	self.product    = product;
	self.categories = [];
	self.images     = [];
	self.hide   = function() { $mdDialog.hide(); };
	self.cancel = function() { $mdDialog.cancel(); };

	request.getAllCategoryByProductId(self.product.id).then(function(resp){
		self.categories = resp.data;
	});
	request.getAllProductImageByProductId(self.product.id).then(function(resp){
		self.images = resp.data;
	});
}

