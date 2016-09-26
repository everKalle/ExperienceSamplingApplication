<?php 
if (!defined('BASEPATH')) exit('No direct script access allowed');

class Settings extends CI_Controller {
    private $logged_in;
    function __construct() {
        parent::__construct();
        $this->load->model('user','',TRUE);
        if($this->session->userdata('logged_in')) {
            $this->logged_in = $this->session->userdata('logged_in');   
        }
  		$this->load->library('form_validation');
    }

    function index() {
   		if($this->logged_in) { 

        $this->load->view('templates/header.php',$this->logged_in);
        $this->load->view('settings_view.php');
        $this->load->view('templates/footer.php');
  		} else {	
		} 
    }

	function update_password() {
		if($this->logged_in) {
   		$this->form_validation->set_rules('old_password', 'Vana parool', 'trim|required|callback_oldpassword');
   		$this->form_validation->set_rules('new_password','Uus parool','trim|required');
   		$this->form_validation->set_rules('new_password2','Korda uut parooli','trim|required|matches[new_password]');


   		if($this->form_validation->run() == FALSE) {
	    	//Field validation failed.
      
     		$this->load->view('templates/header.php',$this->logged_in);
		    $this->load->view('settings_view');
		    $this->load->view('templates/footer.php');
   		}
   		else {
     		$new_pw = $this->input->post('new_password');
		    $this->user->update_password($this->logged_in['username'],$new_pw);   
		    //redirect('settings', 'refresh');
   		}
		} else {

		}
 	}
 
 function oldpassword($password)
 {
   //Field validation succeeded.  Validate against database


   //query the database
   $result = $this->user->login($this->logged_in['username'], $password);

   if($result)
   {
     return TRUE;
   }
   else
   {
     $this->form_validation->set_message('check_database', 'Invalid password');
     return false;
   }
 } 

}
?>
