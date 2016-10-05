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
	  $study_questions = $_POST["question"];
	  $study_events = $_POST["event"];

	  $general_study_data['study-min-time-between-beeps'] *= 60; // hours to minutes
	  $general_study_data['study-duration-time'] *= 10080; // convert weeks to minutes
	
	  $author = $this->study_model->get_author_id($this->logged_in['username']); // get authors id
	  $general_study_data['author'] = $author;

	  if($id = $this->study_model->insert_study($general_study_data)) { // save study to db
		$this->study_model->insert_questions($id, $study_questions); // save study questions to db 
		$this->study_model->insert_events($id, $study_events); // ... events to db

		return true; // redirect or do something

	  } else {

		echo $id; // error

	  }
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
