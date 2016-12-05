<?php 
if (!defined('BASEPATH')) exit('No direct script access allowed');

class Superuser extends CI_Controller {

 function __construct()
 {
   parent::__construct();
   $this->load->model('user','',TRUE);
   $this->load->helper('language');
   if($this->session->userdata('logged_in')) {
      $this->logged_in = $this->session->userdata('logged_in');
    }
   $currentLang = 'estonian';
    if ($this->session->userdata('language')){
      $currentLang = $this->session->userdata('language');
    }
   $this->lang->load('account', $currentLang);
   $this->lang->load('navigation', $currentLang);
   $this->lang->load('login', $currentLang);
   require_once 'application/vendor/autoload.php';
 }

 function index()
 {

  if($this->logged_in )  //check if logged in
  {
    if($this->logged_in['superuser'] == 1){
       $this->load->library('form_validation');

       $this->form_validation->set_rules('username', 'Username', 'trim|required');
       $this->form_validation->set_rules('password', 'Password', 'trim|required');

       if($this->form_validation->run() == FALSE)
       {
        //Field validation failed.  User redirected to login page
         $data['title'] = $this->lang->line('create-account');
         $data['active_page'] = "account_creation";
         $data['logged_in'] = $this->logged_in;
         $data['success_msg'] = "";
         $data['error_msg'] = "";
         $this->load->view('templates/header.php', $data);
         $this->load->view('account_creation');
         $this->load->view('templates/footer.php');
       }
       else
       {
         $id = $this->user->create_account($this->input->post('username'), $this->logged_in['username'], $this->input->post('password'));
         if ($id == "wrong-password" || $id == "exists"){
            $data['title'] = $this->lang->line('create-account');
           $data['active_page'] = "account_creation";
           $data['logged_in'] = $this->logged_in;
            $data['success_msg'] = "";
           if ($id == "wrong-password"){
            $data['error_msg'] = $this->lang->line('fail-wrong-pass');
           } else if ($id == "exists") {
            $data['error_msg'] = $this->lang->line('fail-exists');
           }
           $this->load->view('templates/header.php', $data);
           $this->load->view('account_creation');
           $this->load->view('templates/footer.php');
         } else {
            $request_body = json_decode('{
            "personalizations": [
              {
                "to": [
                  {
                    "email": "' . $this->input->post('username') . '"
                  }
                ],
                "subject": "Experience Sampling - Account activation"
              }
            ],
            "from": {
              "email": "no-reply@experiencesampling.herokuapp.com"
            },
            "content": [
              {
                "type": "text/plain",
                "value": "Hi, an administrative account for Experience Sampling application has been created for you.\nUse the following link to activate your account:\n\n' . base_url() . 'index.php/login/activate/' . $id . '/' . hash('sha256', $this->input->post('username')) .'/\n\n\nExperience Sampling automated."
              }
            ]
          }');

          $apiKey = getenv('SENDGRID_API_KEY');
          $sg = new \SendGrid($apiKey);
          $response = $sg->client->mail()->send()->post($request_body);

            $data['title'] = $this->lang->line('create-account');
           $data['active_page'] = "account_creation";
           $data['logged_in'] = $this->logged_in;
            $data['success_msg'] = $this->lang->line('creation-success');
            $data['error_msg'] = "";
           $this->load->view('templates/header.php', $data);
           $this->load->view('account_creation');
           $this->load->view('templates/footer.php');
         }
       }
     } 
      else
     {
      redirect('/', 'location');
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
