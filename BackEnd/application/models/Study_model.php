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

	function get_study_data($id, $user_id) {
		$this->db->select('*');
		$this->db->from('survey');
		$this->db->where('author', $user_id);
		$this->db->where('id', $id);

		$query = $this->db->get();
		if($query -> num_rows() == 1) {
			return $query->row_array();
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

	function get_all_public_studies() {
		$this->db->select('id, study-title, study-start-date, study-end-date, study-duration-for-user, study-beeps-per-day, study-min-time-between-beeps, study-postpone-time, study-allow-postpone, study-language, study-is-public, study-beep-start-time, study-beep-end-time, study-duration-time');
		$this->db->from('survey');
		$this->db->where('study-is-public', 1);

		$query = $this->db->get();
		
		return $query->result_array();
	}

  function share_study($author,$study_id,$target_user) {
		// author - kontrollida, kas on oigused study jagamiseks 
		// TO-DO
		return True;
	}

}

?>
