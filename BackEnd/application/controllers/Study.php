<?php 
if (!defined('BASEPATH')) exit('No direct script access allowed');

class Study extends CI_Controller {
	private $logged_in;

 function __construct()
 {
   parent::__construct();
   $this->load->model('user','',TRUE);
    $this->load->model('study_model','',TRUE); // load study model
   if($this->session->userdata('logged_in')) {
	$this->logged_in = $this->session->userdata('logged_in');
    }
 }

 function index()
 {

   if($this->logged_in)  //check if logged in
   {
     $data['active_studies'] = $this->study_model->get_active_studies($this->study_model->get_author_id($this->logged_in['username']));
     $data['ended_studies'] = $this->study_model->get_ended_studies($this->study_model->get_author_id($this->logged_in['username']));
     $data['title'] = "Minu uuringud";
     $data['active_page'] = "own_studies";
     $data['logged_in'] = $this->logged_in;
     $this->load->view('templates/header', $data);
     $this->load->view('study/list_own', $data);
     $this->load->view('templates/footer');
   }
   else
   {
    $this->login_required();
   }

 }

 function view($id)
 {
    if($this->logged_in)
    {
      $data['study_details'] = $this->study_model->get_study_data($id, $this->study_model->get_author_id($this->logged_in['username']));
      if ($data['study_details'] != FALSE){
        $data['questions'] = $this->study_model->get_study_questions($id);
        $data['events'] = $this->study_model->get_study_events($id);
        $data['title'] = $data['study_details']['study-title'];
        $data['active_page'] = "own_studies";
        $data['logged_in'] = $this->logged_in;
        $this->load->view('templates/header', $data);
        $this->load->view('study/details_own', $data);
        $this->load->view('templates/footer');
      }
    }
    else
    {
      $this->login_required();
    }
 }

 function delete($id)
 {
    if($this->logged_in)
    {
      $data['study_details'] = $this->study_model->get_study_data($id, $this->study_model->get_author_id($this->logged_in['username']));
      if ($data['study_details'] != FALSE){
        $this->study_model->remove_questions($id);
        $this->study_model->remove_events($id);
        $this->study_model->remove_study($id);
        redirect('/', 'location');
      }
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
      $data['title'] = "Uuringu loomine";
      $data['active_page'] = "create_study";
      $data['logged_in'] = $this->logged_in;
      $this->load->view('templates/header', $data);
      $this->load->view('study/create_study');
      $this->load->view('study/create_question');
      $this->load->view('study/create_event');
      $this->load->view('templates/footer');
     } else {

	  $general_study_data = $_POST['gen'];
	  $study_questions = $_POST["question"];
	  $study_events = $_POST["event"];

	  $general_study_data['study-min-time-between-beeps'] *= 60; // hours to minutes
	  $general_study_data['study-duration-time'] *= $this->input->post('study-duration-time-unit'); // convert weeks to minutes
	
	  $author = $this->study_model->get_author_id($this->logged_in['username']); // get authors id
	  $general_study_data['author'] = $author;

	  if($id = $this->study_model->insert_study($general_study_data)) { // save study to db
		$this->study_model->insert_questions($id, $study_questions); // save study questions to db 
		$this->study_model->insert_events($id, $study_events); // ... events to db

		redirect('/', 'location');

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
