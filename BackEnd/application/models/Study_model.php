<?php

class Study_model extends CI_Model {
	function insert_study($data) {
		if($this->db->insert('survey',$data)) {
			return $this->db->insert_id();
		} else {
			return $this->db->error_message();
		}
	}

	function insert_questions($study_id, $question_data) {

		$len = $question_data["study-question-count"];
		for($i = 0; $i < $len; $i++) {
			$question = $question_data[$i];
			$question['survey_id'] = $study_id;
			$type = $question['question-type'];
			if($type == 'multichoice') { // dealing with multi choice question
				$choices = count($question) - 4;
				$temp = array();
				for($j = 0; $j < $choices; $j++) {
					array_push($temp, $question['question-multichoice-'.$j]);
					unset($question['question-multichoice-'.$j]);
				}
				$question['question-multichoices'] = json_encode($temp);
			} 
				
			if($this->db->insert('survey_question',$question)) { // insert question into db
				continue;
			} else {
				return $this->db->error_message();
			}
		}
	}

	function insert_events($study_id, $event_data) {
		$len = $event_data["study-event-count"];
		for($i = 0; $i < $len; $i++) {
			$event = $event_data[$i];
			$event['survey_id'] = $study_id;
			if($this->db->insert('survey_custom_event',$event)) {
				continue;
			} else {
				return $this->db->error_message();
			}
		}
		return true;
	}

	function update_study($study_id, $study_data) {
		$this->db->where('id', $study_id);
		if ($this->db->update('survey', $study_data)){
			return true;
		} else {
			$this->db->error_message();
		}
	}

	function update_questions($question_data) {

		$len = $question_data["study-question-count"];
		for($i = 0; $i < $len; $i++) {
			$question = $question_data[$i];
			$question_id = $question['question-id'];
			unset($question['question-id']);
			$type = $question['question-type'];
			if($type == 'multichoice') { // dealing with multi choice question
				$choices = count($question) - 2;
				$temp = array();
				for($j = 0; $j < $choices; $j++) {
					array_push($temp, $question['question-multichoice-'.$j]);
					unset($question['question-multichoice-'.$j]);
				}
				$question['question-multichoices'] = json_encode($temp);
			} 
			$this->db->where('id', $question_id);
			if($this->db->update('survey_question',$question)) { // insert question into db
				continue;
			} else {
				return $this->db->error_message();
			}
		}
	}

	function update_events($event_data){
		$len = $event_data["study-event-count"];
		for($i = 0; $i < $len; $i++) {
			$event = $event_data[$i];
			$event_id = $event['event-id'];
			unset($event['event-id']);
			$this->db->where('id', $event_id);
			if($this->db->update('survey_custom_event', $event)) {
				continue;
			} else {
				return $this->db->error_message();
			}
		}
		return true;
	}

	function remove_questions($study_id) {
		$this->db->where('survey_id',$study_id);
		$this->db->delete('survey_question');
		return true;
	}

	function remove_events($study_id) {
		$this->db->where('survey_id',$study_id);
		$this->db->delete('survey_custom_event');
		return true;
	}

	function remove_study($study_id) {
		$this->db->where('id',$study_id);
		$this->db->delete('survey');
		return true;
	}

	function get_author_id($username) {
		$this->db->select('id');
		$this->db->from('users');
		$this->db->where('username',$username);
		$this->db->limit(1);
		$query = $this->db->get();

		if($query -> num_rows() == 1) {
			return $query->row(0)->id;
		} else {
			return false;
		}
	}

	function get_participant_id($email) {
		$this->db->select('id');
		$this->db->from('participants');
		$this->db->where('email',$email);
		$this->db->limit(1);
		$query = $this->db->get();

		if($query -> num_rows() == 1) {
			return $query->row(0)->id;
		} else {
			return false;
		}
	}


	function get_active_studies($user_id) {
		$this->db->select('id, study-title, study-start-date, study-end-date, study-is-public');
		$this->db->from('survey');
		$this->db->where('author', $user_id);
		$this->db->where('study-end-date >=', date("y-m-d H:i:s"));

		$query = $this->db->get();
		
		return $query->result_array();
	}

	function get_ended_studies($user_id) {
		$this->db->select('id, study-title, study-start-date, study-end-date, study-is-public');
		$this->db->from('survey');
		$this->db->where('author', $user_id);
		$this->db->where('study-end-date <', date("y-m-d H:i:s"));

		$query = $this->db->get();
		
		return $query->result_array();
	}

	function get_active_shared_studies($user_id) {
		$this->db->select('survey_id, study-title, study-start-date, study-end-date, study-is-public');
		$this->db->from('view_survey_shared');
		$this->db->where('users_id', $user_id);
		$this->db->where('study-end-date >=', date("y-m-d H:i:s"));

		$query = $this->db->get();
		
		return $query->result_array();
	}

	function get_ended_shared_studies($user_id) {
		$this->db->select('survey_id, study-title, study-start-date, study-end-date, study-is-public');
		$this->db->from('view_survey_shared');
		$this->db->where('users_id', $user_id);
		$this->db->where('study-end-date <', date("y-m-d H:i:s"));

		$query = $this->db->get();
		
		return $query->result_array();
	}

	function get_study_title($id) {
		$this->db->select('study-title');
		$this->db->from('survey');
		$this->db->where('id', $id);

		$query = $this->db->get();
		if($query -> num_rows() == 1) {
			return $query->row_array();
		} else {
			return false;
		}
	}

	function get_study_data($id) {
		$this->db->select('*');
		$this->db->from('survey');
		$this->db->where('id', $id);

		$query = $this->db->get();
		if($query -> num_rows() == 1) {
			return $query->row_array();
		} else {
			return false;
		}
	}

	function get_study_shares($id) {
		$this->db->select('users_id, username');
		$this->db->from('view_survey_share_names');
		$this->db->where('survey_id', $id);

		$query = $this->db->get();
		return $query->result_array();
	}

	function get_study_participants($id) {
		$this->db->select('participant_id, email');
		$this->db->from('view_survey_participant');
		$this->db->where('survey_id', $id);

		$query = $this->db->get();
		return $query->result_array();
	}

	function get_admin_is_owner_of_study($id, $user_id) {
		$this->db->select('id');
		$this->db->from('survey');
		$this->db->where('author', $user_id);
		$this->db->where('id', $id);

		$query = $this->db->get();
		if($query -> num_rows() == 1) {
			return true;
		} else {
			return false;
		}
	}

	function get_user_has_access_to_study($id, $user_id) {
		$this->db->select('*');
		$this->db->from('user_survey_access');
		$this->db->where('users_id', $user_id);
		$this->db->where('survey_id', $id);

		$query = $this->db->get();
		if($query -> num_rows() == 1) {
			return true;
		} else {
			return false;
		}
	}

	function get_study_android($id) {
		$this->db->select('id, study-title, study-start-date, study-end-date, study-duration-for-user, study-beeps-per-day, study-min-time-between-beeps, study-postpone-time, study-allow-postpone, study-language, study-is-public, study-beep-start-time, study-beep-end-time, study-duration-time');
		$this->db->from('survey');
		$this->db->where('id', $id);

		$query = $this->db->get();
		if($query -> num_rows() == 1) {
			return $query->row_array();
		} else {
			return false;
		}
	}

	function get_event_exists($id) {
		$this->db->select('event-title');
		$this->db->from('survey_custom_event');
		$this->db->where('id', $id);
		$this->db->limit(1);
		$query = $this->db->get();

		if($query -> num_rows() == 1) {
			return true;
		} else {
			return false;
		}
	}

	function get_study_questions($id) {
		$this->db->select('question-title, question-type, question-multichoices, question-multichoice-single-choice');
		$this->db->from('survey_question');
		$this->db->where('survey_id', $id);

		$query = $this->db->get();

		return $query->result_array();
	}

	function get_study_events($id) {
		$this->db->select('event-title, event-control-time, event-control-time-unit');
		$this->db->from('survey_custom_event');
		$this->db->where('survey_id', $id);

		$query = $this->db->get();
		
		return $query->result_array();
	}

	function get_study_questions_for_modification($id) {
		$this->db->select('*');
		$this->db->from('survey_question');
		$this->db->where('survey_id', $id);

		$query = $this->db->get();

		return $query->result_array();
	}

	function get_study_events_for_modification($id) {
		$this->db->select('*');
		$this->db->from('survey_custom_event');
		$this->db->where('survey_id', $id);

		$query = $this->db->get();
		
		return $query->result_array();
	}

	function get_all_public_studies() {
		$this->db->select('id, study-title, study-start-date, study-end-date, study-duration-for-user, study-beeps-per-day, study-min-time-between-beeps, study-postpone-time, study-allow-postpone, study-language, study-is-public, study-beep-start-time, study-beep-end-time, study-duration-time');
		$this->db->from('survey');
		$this->db->where('study-is-public', 1);

		$query = $this->db->get();
		
		return $query->result_array();
	}

 	function share_study($study_id, $target_user) {
		$data = array(
		   'users_id' => $target_user ,
		   'survey_id' => $study_id
		);

		if ($this->db->insert('user_survey_access', $data)){
			return True;
		} else {
			return $this->db->error_message();
		}
	}

	function remove_sharing($study_id,$user_id) {
		$this->db->where('survey_id',$study_id);
		$this->db->where('users_id',$user_id);
		$this->db->delete('user_survey_access');
		return true;
	}

	function add_participant($study_id, $target_user) {
		$data = array(
		   'participant_id' => $target_user ,
		   'survey_id' => $study_id ,
		   'join_date' => date('Y-m-d H:i:s')
		);

		if ($this->db->insert('partipant_to_study', $data)){
			return True;
		} else {
			return $this->db->error_message();
		}
	}

	function remove_participant($study_id,$user_id) {
		$this->db->where('survey_id',$study_id);
		$this->db->where('participant_id',$user_id);
		$this->db->delete('partipant_to_study');
		return true;
	}

	function get_participant_studies($p_id) {
		$this->db->select('id, study-title, study-start-date, study-end-date, study-duration-for-user, study-beeps-per-day, study-min-time-between-beeps, study-postpone-time, study-allow-postpone, study-language, study-is-public, study-beep-start-time, study-beep-end-time, study-duration-time, join_date');
		$this->db->from('view_participant_surveys');
		$this->db->where('p_id', $p_id);

		$query = $this->db->get();
		
		return $query->result_array();
	}

	function get_study_results($study_id){
		$this->db->select('email, answers');
		$this->db->where('survey_id',$study_id);
		$this->db->from('view_survey_answers');

		$query = $this->db->get();

		return $query->result_array();
	}

	function get_event_results($study_id){
		$this->db->select('email, event-title, event_time');
		$this->db->where('survey_id',$study_id);
		$this->db->from('view_event_results');

		$query = $this->db->get();

		return $query->result_array();
	}

	function save_answers($study_id, $target_user, $answers) {
		$data = array(
		   'participant_id' => $target_user ,
		   'survey_id' => $study_id ,
		   'answers' => $answers
		);

		if ($this->db->insert('survey_answers', $data)){
			return True;
		} else {
			return $this->db->error_message();
		}
	}

	function save_event_time($event_id, $target_user, $time) {
		$data = array(
		   'participant_id' => $target_user ,
		   'event_id' => $event_id ,
		   'event_time' => $time
		);

		if ($this->db->insert('event_results', $data)){
			return True;
		} else {
			return $this->db->error_message();
		}
	}
}

?>
