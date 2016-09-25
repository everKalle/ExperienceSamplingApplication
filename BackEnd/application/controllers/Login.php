<?php 

if(!defined('BASEPATH')) exit('No direct script access allowed');

class Login extends CI_Controller {


	function __construct() {
		parent::__construct();
	}

	function index() {
		if($this->session->userdata('logged_in')) {
			redirect('','refresh');
		} else {
			$this->load->helper(array('form'));
			$this->load->view('templates/header');
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
