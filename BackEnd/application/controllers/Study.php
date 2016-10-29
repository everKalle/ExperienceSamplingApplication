<?php 
if (!defined('BASEPATH')) exit('No direct script access allowed');

class Study extends CI_Controller {
	private $logged_in;

 function __construct()
 {
   parent::__construct();
   $this->load->model('user','',TRUE);
   $this->load->model('participant_model','',TRUE);
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

 function shared()
 {

   if($this->logged_in)  //check if logged in
   {
     $data['active_studies'] = $this->study_model->get_active_shared_studies($this->study_model->get_author_id($this->logged_in['username']));
     $data['ended_studies'] = $this->study_model->get_ended_shared_studies($this->study_model->get_author_id($this->logged_in['username']));
     $data['title'] = "Minuga jagatud uuringud";
     $data['active_page'] = "shared_studies";
     $data['logged_in'] = $this->logged_in;
     $this->load->view('templates/header', $data);
     $this->load->view('study/list_shared', $data);
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
      if ($this->study_model->get_admin_is_owner_of_study($id,$this->study_model->get_author_id($this->logged_in['username']))){
        $data['study_details'] = $this->study_model->get_study_data($id);
        $data['questions'] = $this->study_model->get_study_questions($id);
        $data['events'] = $this->study_model->get_study_events($id);
        $data['title'] = $data['study_details']['study-title'];
        $data['other_users'] = $this->user->get_other_usernames($this->logged_in['username']);
        $data['shared_with'] = $this->study_model->get_study_shares($id);
        $data['participants'] = $this->study_model->get_study_participants($id);
        $data['all_participants'] = $this->participant_model->get_all_participants();
        $data['active_page'] = "own_studies";
        $data['logged_in'] = $this->logged_in;

        $this->load->helper('form');
        $this->load->library('form_validation');

        $this->load->view('templates/header', $data);
        $this->load->view('study/details_own', $data);
        $this->load->view('templates/footer');
      } else {
        redirect('/', 'location');
      }
    }
    else
    {
      $this->login_required();
    }
 }

 function view_shared($id)
 {
    if($this->logged_in)
    {
      if ($this->study_model->get_user_has_access_to_study($id, $this->study_model->get_author_id($this->logged_in['username']))){
        $data['study_details'] = $this->study_model->get_study_data($id);
        $data['questions'] = $this->study_model->get_study_questions($id);
        $data['events'] = $this->study_model->get_study_events($id);
        $data['title'] = $data['study_details']['study-title'];
        $data['owner_name'] = $this->user->get_username_via_id($data['study_details']['author'])['username'];
        $data['other_users'] = $this->user->get_other_usernames($this->logged_in['username']);
        $data['shared_with'] = $this->study_model->get_study_shares($id);
        $data['active_page'] = "shared_studies";
        $data['logged_in'] = $this->logged_in;

        $this->load->helper('form');
        $this->load->library('form_validation');

        $this->load->view('templates/header', $data);
        $this->load->view('study/details_shared', $data);
        $this->load->view('templates/footer');
      } else {
        redirect('/study/shared/', 'location');
      }
    }
    else
    {
      $this->login_required();
    }
 }

 function share($study_id) {
    if($this->logged_in)
    {
  		$this->load->helper('form');
      $this->load->library('form_validation');
      $this->form_validation->set_rules('share-study-username', 'User', 'required');
      if ($this->form_validation->run() === FALSE){
       	redirect('/study/view/' . $study_id, 'location');
       } else {
  			 $user = $_POST['share-study-username'];
  			 if($this->study_model->get_admin_is_owner_of_study($study_id,$this->study_model->get_author_id($this->logged_in['username'])) && $user != $this->logged_in['username']) {
          if (!$this->study_model->get_user_has_access_to_study($study_id,$this->study_model->get_author_id($user))){
    				$this->study_model->share_study($study_id, $this->study_model->get_author_id($user));
          }
          redirect('/study/view/' . $study_id, 'location');
  			 } else {
  				redirect('/study/view/' . $study_id, 'location');
  			 }			 
  		}
    }
    else
    {
      $this->login_required();
    }
 }

  function remove_share($study_id,$user_id) {
    if($this->logged_in)
    {
      if($this->study_model->get_admin_is_owner_of_study($study_id,$this->study_model->get_author_id($this->logged_in['username']))) {
        if ($this->study_model->get_user_has_access_to_study($study_id,$user_id)){
          $this->study_model->remove_sharing($study_id, $user_id);
        }
      }
      redirect('/study/view/' . $study_id, 'location');
    }
    else
    {
      $this->login_required();
    }
 }


 function add_participant($study_id) {
    if($this->logged_in)
    {
      $this->load->helper('form');
      $this->load->library('form_validation');
      $this->form_validation->set_rules('add-participant-username', 'User', 'required');
      if ($this->form_validation->run() === FALSE){
        redirect('/study/view/' . $study_id, 'location');
       } else {
         $user = $_POST['add-participant-username'];
         if($this->study_model->get_admin_is_owner_of_study($study_id,$this->study_model->get_author_id($this->logged_in['username'])) || $this->study_model->get_user_has_access_to_study($study_id,$this->study_model->get_author_id($this->logged_in['username']))) {
            $this->study_model->add_participant($study_id, $this->study_model->get_participant_id($user));
          redirect('/study/view/' . $study_id, 'location');  
        } else {
          redirect('/study/view/' . $study_id, 'location');  
        }
      }
    }
    else
    {
      $this->login_required();
    }
 }

  function remove_participant($study_id,$user_id) {
    if($this->logged_in)
    {
      if($this->study_model->get_admin_is_owner_of_study($study_id,$this->study_model->get_author_id($this->logged_in['username'])) || $this->study_model->get_user_has_access_to_study($study_id,$this->study_model->get_author_id($this->logged_in['username']))) {
        $this->study_model->remove_participant($study_id, $user_id);
      }
      redirect('/study/view/' . $study_id, 'location');
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

 function get_public_studies(){
    $public_studies = $this->study_model->get_all_public_studies();
    $study_array = array();
    foreach($public_studies as $study):
      $study['questions'] = $this->study_model->get_study_questions($study['id']);
      $study['events'] = $this->study_model->get_study_events($study['id']);
      $study_array[] = $study;
    endforeach;
    $this->output->set_content_type('application/json')->set_output(json_encode($study_array));
 }

 function get_participant_studies(){
    $token = $this->input->post('token');
    if ($token != NULL){
      $p_id = $this->participant_model->get_id_via_token($token);
      if ($p_id === false) {
        echo "invalid_token";
      } else {
        $participant_studies = $this->study_model->get_participant_studies($p_id);
        $study_array = array();
        foreach($participant_studies as $study):
          $study['questions'] = $this->study_model->get_study_questions($study['id']);
          $study['events'] = $this->study_model->get_study_events($study['id']);
          $study_array[] = $study;
        endforeach;
        $this->output->set_content_type('application/json')->set_output(json_encode($study_array));
      }
    } else {
      echo "nothing";
    }
 }

 function participant_join_study() {
    $token = $this->input->post('token');
    $study_id = $this->input->post('study_id');
    if ($token != NULL && $study_id != NULL){
      $p_id = $this->participant_model->get_id_via_token($token);
      if ($p_id === false) {
        echo "invalid_token";
      } else {
        if ($this->study_model->get_study_data($study_id) != false){
          $success = $this->study_model->add_participant($study_id, $p_id);
          if ($success === TRUE){
            echo "success";
          } else {
            echo $success;
          }
        } else {
          echo "invalid_study";
        }
      }
    } else {
      echo "nothing";
    }
 }

 function store_study_results() {
    $token = $this->input->post('token');
    $study_id = $this->input->post('study_id');
    $answers = $this->input->post('answers');
    if ($token != NULL && $study_id != NULL && $answers != NULL){
      $p_id = $this->participant_model->get_id_via_token($token);
      if ($p_id === false) {
        echo "invalid_token";
      } else {
        if ($this->study_model->get_study_data($study_id) != false){
          $success = $this->study_model->save_answers($study_id, $p_id, $answers);
          if ($success === TRUE){
            echo "success";
          } else {
            echo $success;
          }
        } else {
          echo "invalid_study";
        }
      }
    } else {
      echo "nothing";
    }
 }

 function store_event_results() {
    $token = $this->input->post('token');
    $event_id = $this->input->post('event_id');
    $time = $this->input->post('time');
    if ($token != NULL && $event_id != NULL && $time != NULL){
      $p_id = $this->participant_model->get_id_via_token($token);
      if ($p_id === false) {
        echo "invalid_token";
      } else {
        if ($this->study_model->get_event_exists($event_id)){
          if (intval($time)>0){
            $success = $this->study_model->save_event_time($event_id, $p_id, $time);
            if ($success === TRUE){
              echo "success";
            } else {
              echo $success;
            }
          } else {
            echo "invalid_time";
          }
        } else {
          echo "invalid_event";
        }
      }
    } else {
      echo "nothing";
    }
 }

 function get_study_details($id){
    $study = $this->study_model->get_study_android($id);
    if ($study != FALSE){
      $study['questions'] = $this->study_model->get_study_questions($study['id']);
      $study['events'] = $this->study_model->get_study_events($study['id']);
      $this->output->set_content_type('application/json')->set_output(json_encode($study));
    } else {
      $this->output->set_content_type('application/json')->set_output(json_encode(array()));
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

 function modify($id)
 {
   if($this->logged_in)  //check if logged in
   {
     if ($this->study_model->get_admin_is_owner_of_study($id,$this->study_model->get_author_id($this->logged_in['username']))){
     $this->load->helper('form');
     $this->load->library('form_validation');
      $this->form_validation->set_rules('gen[study-title]', 'Pealkiri', 'required');
       if ($this->form_validation->run() === FALSE){
        $data['title'] = "Uuringu muutmine";
        $data['active_page'] = "own_studies";
        $data['logged_in'] = $this->logged_in;
        $data['study_details'] = $this->study_model->get_study_data($id);
        $data['questions'] = $this->study_model->get_study_questions_for_modification($id);
        $data['events'] = $this->study_model->get_study_events_for_modification($id);

        $this->load->view('templates/header', $data);
        $this->load->view('study/modify_study', $data);
        $this->load->view('study/modify_question', $data);
        $this->load->view('study/modify_event', $data);
        $this->load->view('templates/footer');
       } else {
        $study_id = $_POST['study-id'];
        $general_study_data = $_POST['gen'];
        $study_questions = $_POST["question"];
        $study_events = $_POST["event"];

        $general_study_data['study-min-time-between-beeps'] *= 60; // hours to minutes
        $general_study_data['study-duration-time'] *= $this->input->post('study-duration-time-unit'); // convert weeks to minutes
      
        $author = $this->study_model->get_author_id($this->logged_in['username']); // get authors id
        $general_study_data['author'] = $author;

        if($id = $this->study_model->update_study($study_id, $general_study_data)) { // save study to db
        $this->study_model->update_questions($study_questions); // save study questions to db 
        $this->study_model->update_events($study_events); // ... events to db
        redirect('/study/view/' . $study_id, 'location');

      } else {

      echo $id; // error

      }
     }
    } else {
      redirect('/', 'location');
    }
   }
   else
   {
    $this->login_required();
   }

 }

 function study_results($study_id){
    if($this->logged_in)  //check if logged in
    {
      $admin_id = $this->study_model->get_author_id($this->logged_in['username']);
      if ($this->study_model->get_admin_is_owner_of_study($study_id, $admin_id) || $this->study_model->get_user_has_access_to_study($study_id, $admin_id)){
        $output_csv = '"Osaleja"';
        $study_questions = $this->study_model->get_study_questions($study_id);
        for($i = 0; $i < count($study_questions); $i++) {
          $output_csv .= ';"' . $study_questions[$i]['question-title'] . '"';
        }
        $output_csv .= "\r\n";
        $study_answers = $this->study_model->get_study_results($study_id);
        for($i = 0; $i < count($study_answers); $i++) {
          $output_csv .= '"' . $study_answers[$i]['email'] . '";' . $study_answers[$i]['answers'] . "\r\n";
        }
        header('Content-type: text/csv; charset=utf-8');
        header('Content-Disposition: attachment; filename="' . $this->study_model->get_study_title($study_id)['study-title'] . '.csv"');
        echo $output_csv;
      } else {
      redirect('/', 'location');
      }
    } else {
      $this->login_required();
    }
 }

 function event_results($study_id){
    if($this->logged_in)  //check if logged in
    {
      $admin_id = $this->study_model->get_author_id($this->logged_in['username']);
      if ($this->study_model->get_admin_is_owner_of_study($study_id, $admin_id) || $this->study_model->get_user_has_access_to_study($study_id, $admin_id)){
        $output_csv = '"Osaleja";"Sündmus";"Aeg"';
        $output_csv .= "\r\n";
        $event_results = $this->study_model->get_event_results($study_id);
        for($i = 0; $i < count($event_results); $i++) {
          $output_csv .= '"' . $event_results[$i]['email'] . '";"' . $event_results[$i]['event-title'] .'";"' . $event_results[$i]['event_time'] .'"' . "\r\n";
        }
        header('Content-type: text/csv; charset=utf-8');
        header('Content-Disposition: attachment; filename="' . $this->study_model->get_study_title($study_id)['study-title'] . '_events.csv"');
        echo $output_csv;
      } else {
      redirect('/', 'location');
      }
    } else {
      $this->login_required();
    }
 }

 private function login_required() {
	redirect('/login');
 }
}
?>
