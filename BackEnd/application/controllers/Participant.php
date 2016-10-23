<?php 
if (!defined('BASEPATH')) exit('No direct script access allowed');

class Participant extends CI_Controller {

 function __construct()
 {
   parent::__construct();
   $this->load->model('participant_model','',TRUE);
 }

 function login()
 {
   $username = $this->input->post('username');
   $password = $this->input->post('password');
   if($username != NULL && $password != NULL){
     $data = $this->participant_model->login($username, $password);
     if ($data === FALSE){
      echo "invalid";
     } else {
      echo $data['token'];
     }
   } else {
    echo "nothing";
   }
 }
}
?>
