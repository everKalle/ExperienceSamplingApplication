<?php 
if (!defined('BASEPATH')) exit('No direct script access allowed');

class Study extends CI_Controller {
	private $log_data;
	private $logged_in;

 function __construct()
 {
   parent::__construct();
   $this->load->model('user','',TRUE);
   if($this->session->userdata('logged_in')) {
	$this->log_data = array(
		'logged_in' => True
	);
	$this->logged_in = True;
    }
 }

 function index()
 {
   if($this->logged_in)  //check if logged in
   {
     $this->load->view('templates/header', $this->log_data);
     //load and show study list
     $this->load->view('templates/footer');
   }
   else
   {
    $this->login_required();
   }

 }

 function create()
 {
   if($this->logged_in)  //check if logged in
   {
     $this->load->helper('form');
     $this->load->library('form_validation');

     if ($this->form_validation->run() === FALSE){
      $this->load->view('templates/header', $this->log_data);
      $this->load->view('study/create_study');
      $this->load->view('templates/footer');
     } else {
      // save data
     }
   }
   else
   {
    $this->login_required();
   }

 }

 private function login_required() {
	redirect('/login');
 }
}
?>
