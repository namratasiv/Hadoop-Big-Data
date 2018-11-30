<?php
class Client extends CI_Model{
function __construct() {
parent::__construct();
}
function form_insert($data){

$this->db->insert('client', $data);

}
function form_insertt($data1){

    $this->db->insert('users', $data1);
    
    }

}
?>