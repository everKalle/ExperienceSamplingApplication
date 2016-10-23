<?php

class Participant_model extends CI_Model {
	function login($username, $password) {
		$this->db->select('token');
		$this->db->from('participants');
		$this->db->where('username', $username);
		$this->db->where('password', $password);
		$this->db->limit(1);

		$query = $this->db->get();
		
		if($query -> num_rows() == 1) {
			return $query->row_array();
		} else {
			return false;
		}
	}
	
	function update_password($token,$password) {
		$this->db->where('token', $token);
		$this->db->update('participants', array('password' => hash('sha256',$password)));
		return true;
	}
}

?>
