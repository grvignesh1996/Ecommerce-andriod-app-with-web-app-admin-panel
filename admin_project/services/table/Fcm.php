<?php
require_once(realpath(dirname(__FILE__) . "/../tools/rest.php"));
require_once(realpath(dirname(__FILE__) . "/../conf.php"));

class Fcm extends REST{
	
	private $mysqli = NULL;
	private $db = NULL; 
	public $conf = NULL;
	
	public function __construct($db) {
		parent::__construct();
		$this->db = $db;
		$this->mysqli = $db->mysqli;
		$this->conf = new CONF(); // Create config class
    }
	
	public function findAll(){
		if($this->get_request_method() != "GET") $this->response('',406); 
		$query="SELECT DISTINCT * FROM fcm f ORDER BY f.last_update DESC";
		$this->show_response($this->db->get_list($query));
	}
	
	public function allCount(){
		if($this->get_request_method() != "GET") $this->response('',406);
		$q = "";
		if(isset($this->_request['q'])) $q = $this->_request['q'];
		if($q != ""){
			$query=	"SELECT COUNT(DISTINCT f.id) FROM fcm f "
					."WHERE device REGEXP '$q' OR serial REGEXP '$q' OR app_version REGEXP '$q' OR os_version REGEXP '$q' ";
		} else {
			$query="SELECT COUNT(DISTINCT f.id) FROM fcm f";
		}
		$this->show_response_plain($this->db->get_count($query));
	}

	public function allCountPlain(){
		$query="SELECT COUNT(DISTINCT f.id) FROM fcm f";
		return $this->db->get_count($query);
	}
	
	public function findAllByPage(){
		if($this->get_request_method() != "GET") $this->response('',406);
		if(!isset($this->_request['limit']) || !isset($this->_request['page']))$this->responseInvalidParam();
		$limit = (int)$this->_request['limit'];
		$offset = ((int)$this->_request['page']) - 1;
		$q = "";
		if(isset($this->_request['q'])) $q = $this->_request['q'];
		if($q != ""){
			$query=	"SELECT DISTINCT * FROM fcm f "
					."WHERE device REGEXP '$q' OR serial REGEXP '$q' OR app_version REGEXP '$q' OR os_version REGEXP '$q' "
					."ORDER BY f.last_update DESC LIMIT $limit OFFSET $offset";
		} else {
			$query="SELECT DISTINCT * FROM fcm f ORDER BY f.last_update DESC LIMIT $limit OFFSET $offset";
		}
		$this->show_response($this->db->get_list($query));
	}

    public function findBySerial($serial){
        $query="SELECT distinct * FROM fcm f WHERE f.serial='$serial'";
        $result = $this->db->get_one($query);
        return $result;
    }

	public function insertOne(){
		if($this->get_request_method() != "POST") $this->response('',406);
		$fcm 			= json_decode(file_get_contents("php://input"),true);

        // checking security code
        if(!isset($this->_header['Security']) || $this->_header['Security'] != $this->conf->SECURITY_CODE){
            $m = array('status' => 'failed', 'msg' => 'Invalid security code', 'data' => null);
            $this->show_response($m);
            return;
        }

		$device 		= $fcm['device'];
		$os_version 	= $fcm['os_version'];
		$app_version 	= $fcm['app_version'];
		$serial  		= $fcm['serial'];
		$regid  		= $fcm['regid'];
		$resp 			= array();
		$column_names 	= array('device', 'os_version', 'app_version', 'serial', 'regid', 'created_at', 'last_update');
		$table_name 	= 'fcm';
		$pk 			= 'id';

		$query="SELECT f.* FROM fcm f WHERE f.serial='$serial' LIMIT 1";
		$r = $this->mysqli->query($query) or die($this->mysqli->error.__LINE__);
	    $nowTime = round(microtime(true) * 1000);

		if($r->num_rows > 0){ // update
			$result = $r->fetch_assoc();
			$new_fcm['id'] = (int)$result['id'];
			$new_fcm['fcm'] = $fcm;
			$new_fcm['fcm']['created_at'] = (int)$result['created_at'];
			$new_fcm['fcm']['last_update'] = $nowTime;
			$resp = $this->db->post_update($new_fcm['id'], $new_fcm, $pk, $column_names, $table_name);
		}else{ // insert
			$fcm['created_at'] = $nowTime;
			$fcm['last_update'] = $nowTime;
			$resp = $this->db->post_one($fcm, $pk, $column_names, $table_name);
		}
		$this->show_response($resp);
	}
	
	public function getAllRegId(){
		$query="SELECT DISTINCT f.regid FROM fcm f";
		return $this->db->get_list($query);
	}

	public function processNotification() {
		if($this->get_request_method() != "POST") $this->response('',406);
		$body = json_decode(file_get_contents("php://input"), true);
        if($this->conf->DEMO_VERSION){
            $m = array('status' => "failed", "msg" => "Ops, this is demo version", "data" => null);
            $this->show_response($m);
            return;
        }
		if(isset($body['registration_ids']) && $body['registration_ids'] != null && $body['registration_ids'] != ""){
			$registration_ids = $body['registration_ids'];
		} else {
			$array_regid = $this->getAllRegId();
			$registration_ids = array();
			foreach($array_regid as $r){
				array_push($registration_ids, $r['regid']);
			}
			if(sizeof($registration_ids) <=0 ){
			    $m = array('status' => "failed", "msg" => "Ops, FCM data is empty", "data" => null);
                $this->show_response($m);
                return;
			}
		}
		
		$data = $body['data'];
		$regid_arr = array();
		$i = 0;
		// split regid per 1000 item
		foreach($registration_ids as $reg_id){
			$i++;
			$regid_arr[floor($i/1000)][] = $reg_id;
		}
		// send notif per 1000 items
		$pushStatus = array();
		foreach($regid_arr as $val){
			$pushStatus[] = $this->sendPushNotification($val, $data);
		}
		
		$success_count = 0;
		$failure_count = 0;
		foreach($pushStatus as $s){
			if(!empty($s['success'])) $success_count = $success_count + $s['success']; 
			if(!empty($s['failure'])) $failure_count = $failure_count + ($s['failure']); 
		}
		
		$obj_data = array();
		if(!empty($pushStatus)){
			$obj_data['success'] = $success_count;
			$obj_data['failure'] = $failure_count;
		$resp['data'] = $obj_data;
			$this->response($this->json($resp), 200);
		}else{
			$this->response('',204);	// "No Content" status
		}

	}
	
	public function sendPushNotification($registration_ids, $data){
		// Set POST variables
		$url = 'https://fcm.googleapis.com/fcm/send';
		$fields = array(
			'registration_ids' => $registration_ids,
			'data' => $data
		);
		$api_key = $this->conf->FCM_KEY;
		$headers = array( 'Authorization: key='.$api_key, 'Content-Type: application/json' );
		// Open connection
		$ch = curl_init();

		// Set the url, number of POST vars, POST data
		curl_setopt($ch, CURLOPT_URL, $url);
		curl_setopt($ch, CURLOPT_POST, true);
		curl_setopt($ch, CURLOPT_HTTPHEADER, $headers);
		curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);

		// Disabling SSL Certificate support temporary
		curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, false);
		curl_setopt($ch, CURLOPT_POSTFIELDS, $this->json($fields));
		// Execute post
		$result = curl_exec($ch);
		if ($result === FALSE) { die('Curl failed: ' . curl_error($ch)); }
		// Close connection
		curl_close($ch);
		$result_data = json_decode($result);
		$result_arr = array();
		$result_arr['success'] = $result_data->success; 
		$result_arr['failure'] = $result_data->failure;
		return $result_arr;
	}
	
}	
?>