<?php 

if(!defined('BASEPATH')) exit('No direct script access allowed');

class Login extends CI_Controller {


	function __construct() {
		parent::__construct();
		$this->load->helper('language');
		$currentLang = 'estonian';
	    if ($this->session->userdata('language')){
	      $currentLang = $this->session->userdata('language');
	    }
		$this->lang->load('navigation', $currentLang);
		$this->lang->load('login', $currentLang);
	}

	function index() {
		if($this->session->userdata('logged_in')) {
			redirect('','refresh');
		} else {
			$this->load->helper(array('form'));
			$data['title'] = "Logi sisse";
	        $data['active_page'] = "login";
	        $data['logged_in'] = FALSE;
			$this->load->view('templates/header', $data);
			$this->load->view('login_view');
			$this->load->view('templates/footer');
		}
	}

	function logout() {
		session_destroy();
		$this->session->unset_userdata('logged_in');
		redirect('login','refresh');
	}
}

?>
