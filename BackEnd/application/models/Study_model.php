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
}

?>
