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
   if(TRUE)  //check if logged in
   {
     $this->load->view('templates/header');
     //load and show study list
     $this->load->view('templates/footer');
   }
   else
   {
    //redirect
   }

 }

 function create()
 {
   if(TRUE)  //check if logged in
   {
     $this->load->helper('form');
     $this->load->library('form_validation');

     if ($this->form_validation->run() === FALSE){
      $this->load->view('templates/header');
      $this->load->view('study/create_study');
      $this->load->view('templates/footer');
     } else {
      // save data
     }
   }
   else
   {
    //redirect
   }

 }
}
?>
