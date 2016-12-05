<?php 

if(!defined('BASEPATH')) exit('No direct script access allowed');

class Login extends CI_Controller {


	function __construct() {
		parent::__construct();
		$this->load->model('user','',TRUE);
		$this->load->helper('language');
		$currentLang = 'estonian';
	    if ($this->session->userdata('language')){
	      $currentLang = $this->session->userdata('language');
	    }
   		$this->lang->load('account', $currentLang);
		$this->lang->load('navigation', $currentLang);
		$this->lang->load('login', $currentLang);
	}

	function index() {
		if($this->session->userdata('logged_in')) {
			redirect('','refresh');
		} else {
			$this->load->helper(array('form'));
			$data['title'] = $this->lang->line('log-in');
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

	function activate($id, $token) {
		$data['title'] = $this->lang->line('activate-title');
		$data['active_page'] = "activate_account";
		$data['logged_in'] = FALSE;
		$user = $this->user->get_username_via_id_activation($id);
		if ($user != FALSE){
			if (hash('sha256', $user['username']) == $token){
				$this->load->library('form_validation');
				$this->load->helper(array('form'));

			   	$this->form_validation->set_rules('password', 'Password', 'trim|required|min_length[8]|max_length[30]');
			   	$this->form_validation->set_rules('password_repeat', 'Repeat password', 'trim|required|matches[password]');
				if($this->form_validation->run() == FALSE){
			        $data['act_link'] = $id . '/' . $token;
					$this->load->view('templates/header', $data);
					$this->load->view('activate/activate_view');
					$this->load->view('templates/footer');
				} else {
					$this->user->activate_account($id, $this->input->post('password'));
					$this->load->view('templates/header', $data);
					$this->load->view('activate/activate_success');
					$this->load->view('templates/footer');
				}
			} else {
				$this->load->view('templates/header', $data);
				$data['error_msg'] = $this->lang->line('act-fail-invalid-link');
				$this->load->view('activate/activate_fail', $data);
				$this->load->view('templates/footer');
			}
		} else {
			$this->load->view('templates/header', $data);
			$data['error_msg'] = $this->lang->line('act-fail-does-not-exist');
			$this->load->view('activate/activate_fail', $data);
			$this->load->view('templates/footer');
		}
	}
}

?>
