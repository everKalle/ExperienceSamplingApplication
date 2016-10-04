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
      $this->form_validation->set_rules('gen[study-title]', 'Pealkiri', 'required');
     if ($this->form_validation->run() === FALSE){
      $this->load->view('templates/header', $this->logged_in);
      $this->load->view('study/create_study');
      $this->load->view('study/create_question');
      $this->load->view('study/create_event');
      $this->load->view('templates/footer');
     } else {
	  $this->load->model('study_model','',TRUE); // load study model

	  $general_study_data = $_POST['gen'];


	  // ------------ m2his ------------- \\
		// time asemel kasutada mdiagi muud andmebaasis.

	  $general_study_data['study-min-time-between-beeps'] *= 5900; // convert seconds to hours ** (stored as seconds)
	  $general_study_data['study-postpone-time'] *= 60; // convert seconds to minutes ** (stored as seconds)
	  $general_study_data['study-duration-time'] *= 604800; // convert seconds to weeks ** (stored as seconds)
	  // ------------- m2his ---------- \\


	  $author = $this->study_model->get_author_id($this->logged_in['username']); // get authors id
	  $general_study_data['author'] = $author;
	  print_r($general_study_data);
	  if($k = $this->study_model->insert_study($general_study_data)) {
		// success, do something
		return true;
	  } else {
		echo $k;
	  }
	 	 
	 
	  print_r( $this->input->post(NULL));
     }
   }
   else
   {
    $this->login_required();
   }

 }

 function addStudy() {
	$k = $_POST;
	print_r($k);
 }

 private function login_required() {
	redirect('/login');
 }
}
?>
