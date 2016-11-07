<?php

class User extends CI_Model {
	function login($username, $password) {
		$this->db->select('id, username, password, acc_activated, superuser');
		$this->db->from('users');
		$this->db->where('username', $username);
		$this->db->where('password', hash('sha256',$password));
		$this->db->limit(1);

		$query = $this->db->get();
		
		if($query -> num_rows() == 1) {
			return $query->result();
		} else {
			return false;
		}
	}
	
	function update_password($username,$password) {
		$this->db->where('username', $username);
		$this->db->update('users', array('password' => hash('sha256',$password)));
		return true;
	}

	function get_other_usernames($self_username) {
		$this->db->select('username');
		$this->db->from('users');
		$this->db->where('username !=', $self_username);
		$query = $this->db->get();

		return $query->result_array();
	}

	function get_username_via_id($id) {
		$this->db->select('username');
		$this->db->from('users');
		$this->db->where('id', $id);
		$this->db->limit(1);
		$query = $this->db->get();
		if($query -> num_rows() == 1) {
			return $query->row_array();
		} else {
			return false;
		}
	}
}

?>
