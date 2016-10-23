<?php 
if (!defined('BASEPATH')) exit('No direct script access allowed');

class Participant extends CI_Controller {

 function __construct()
 {
   parent::__construct();
   $this->load->model('participant_model','',TRUE);
   $this->load->helper('string');
 }

 function login()
 {
   $email = $this->input->post('email');
   $password = $this->input->post('password');
   if($email != NULL && $password != NULL){
     $data = $this->participant_model->login($email, $password);
     if ($data === FALSE){
      echo "invalid";
     } else {
      echo $data['token'];
     }
   } else {
    echo "nothing";
   }
 }

 function register()
 {
   $email = $this->input->post('email');
   $password = $this->input->post('password');
   if($email != NULL && $password != NULL){
     if ($this->participant_model->account_exists($email)){
      echo "exists";
     } else {
      $token = random_string('alnum', 64);
      echo $this->participant_model->create_account($email, $password, $token);
     }
   } else {
    echo "nothing";
   }
 }
}
?>
