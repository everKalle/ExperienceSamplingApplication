<?php 
if (!defined('BASEPATH')) exit('No direct script access allowed');

class Study extends CI_Controller {

 function __construct()
 {
   parent::__construct();
   $this->load->model('user','',TRUE);
 }

 function index()
 {
   if (if($this->session->userdata('logged_in') != FALSE))//  //check if logged in
   {
     $this->load->view('templates/header');
     //load and show study list
     $this->load->view('templates/footer');
   }
   else
   {
    redirect('/login/', 'location');
   }

 }

 function create()
 {
   if (if($this->session->userdata('logged_in') != FALSE))  //check if logged in
   {
     $this->load->helper('form');
     $this->load->library('form_validation');
      $this->form_validation->set_rules('study-title', 'Pealkiri', 'required');
     if ($this->form_validation->run() === FALSE){
      $this->load->view('templates/header');
      $this->load->view('study/create_study');
      $this->load->view('study/create_question');
      $this->load->view('templates/footer');
     } else {
      echo json_encode($this->input->post(NULL));  //create real saving system instead of just printint out
     }
   }
   else
   {
    redirect('/login/', 'location');
   }

 }
}
?>
