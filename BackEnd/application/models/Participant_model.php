<?php

class Participant_model extends CI_Model {
	function login($email, $password) {
		$this->db->select('token');
		$this->db->from('participants');
		$this->db->where('email', $email);
		$this->db->where('password', $password);
		$this->db->limit(1);

		$query = $this->db->get();
		
		if($query -> num_rows() == 1) {
			return $query->row_array();
		} else {
			return false;
		}
	}

	function validate_change($token, $password) {
		$this->db->select('id');
		$this->db->from('participants');
		$this->db->where('token', $token);
		$this->db->where('password', $password);
		$this->db->limit(1);

		$query = $this->db->get();
		
		if($query -> num_rows() == 1) {
			return true;
		} else {
			return false;
		}
	}

	function change_password($token, $password) {
		$this->db->where('token', $token);
		$this->db->update('participants', array('password' => $password));
		return true;
	}

	function create_account($email, $password, $token) {
		$data = array(
		   'email' => $email ,
		   'password' => $password ,
		   'token' => $token ,
		   'logged_in' => '0'
		);
		if($this->db->insert('participants',$data)) {
			return "success";
		} else {
			return $this->db->error_message();
		}
	}

	function account_exists($email) {
		$this->db->select('token');
		$this->db->from('participants');
		$this->db->where('email', $email);
		$this->db->limit(1);

		$query = $this->db->get();
		
		if($query -> num_rows() >= 1) {
			return true;
		} else {
			return false;
		}
	}

	function get_id_via_token($token)
	{
		$this->db->select('id');
		$this->db->from('participants');
		$this->db->where('token', $token);
		$this->db->limit(1);

		$query = $this->db->get();
		
		if($query -> num_rows() >= 1) {
			return $query->row(0)->id;
		} else {
			return false;
		}
	}
	
	function update_password($token,$password) {
		$this->db->where('token', $token);
		$this->db->update('participants', array('password' => hash('sha256',$password)));
		return true;
	}

	function get_all_participants() {
    $this->db->select('email');
    $this->db->from('participants');
    $query = $this->db->get();

    return $query->result_array();
  }
}

?>
