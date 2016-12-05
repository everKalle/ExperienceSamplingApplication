<?php 
if (!defined('BASEPATH')) exit('No direct script access allowed');

class VerifyLogin extends CI_Controller {

 function __construct()
 {
   parent::__construct();
   $this->load->model('user','',TRUE);
   $this->load->helper('language');
   $currentLang = 'estonian';
    if ($this->session->userdata('language')){
      $currentLang = $this->session->userdata('language');
    }
   $this->lang->load('navigation', $currentLang);
   $this->lang->load('login', $currentLang);
 }

 function index()
 {
   $this->load->library('form_validation');

   $this->form_validation->set_rules('username', 'Username', 'trim|required');
   $this->form_validation->set_rules('password', 'Password', 'trim|required|callback_check_database');

   if($this->form_validation->run() == FALSE)
   {
    //Field validation failed.  User redirected to login page
     $data['title'] = $this->lang->line('log-in');
     $data['active_page'] = "login";
     $data['logged_in'] = FALSE;
     $this->load->view('templates/header.php', $data);
     $this->load->view('login_view');
     $this->load->view('templates/footer.php');
   }
   else
   {
     // Success :) 
     redirect('', 'refresh');
   }

 }

 function check_database($password)
 {
   //Field validation succeeded.  Validate against database
   $username = $this->input->post('username');

   //query the database
   $result = $this->user->login($username, $password);

   if($result)
   {
     $sess_array = array();
     foreach($result as $row)
     {
       $sess_array = array(
         'id' => $row->id,
         'username' => $row->username,
      	 'acc_activated' => $row->acc_activated,
      	 'superuser' => $row->superuser
       );
       $this->session->set_userdata('logged_in', $sess_array);
     }
     return TRUE;
   }
   else
   {
     $this->form_validation->set_message('check_database', 'Invalid username or password');
     return false;
   }
 }
}
?>
