<?php

class Study_model extends CI_Model {
	function insert_study($data) {
		if($this->db->insert('survey',$data)) {
			return true;
		} else {
			return $this->db->error_message();
		}
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
