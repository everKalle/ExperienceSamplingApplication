<?php 
if (!defined('BASEPATH')) exit('No direct script access allowed');

class Study extends CI_Controller {
	private $logged_in;

 function __construct()
 {
   parent::__construct();
   $this->load->model('user','',TRUE);
   if($this->session->userdata('logged_in')) {
	$this->logged_in = $this->session->userdata('logged_in');
    }
 }

 function index()
 {

   if($this->logged_in)  //check if logged in
   {
     $this->load->view('templates/header', $this->logged_in);
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
      $this->form_validation->set_rules('study-title', 'Pealkiri', 'required');
     if ($this->form_validation->run() === FALSE){
      $this->load->view('templates/header', $this->logged_in);
      $this->load->view('study/create_study');
      $this->load->view('study/create_question');
      $this->load->view('templates/footer');
     } else {
      echo json_encode($this->input->post(NULL));  //create real saving system instead of just printint out
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
