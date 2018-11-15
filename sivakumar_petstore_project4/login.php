<?php
session_start();
$servername = "localhost";
$database = "pet";
$username = "root";
$password = "";

// Create connection

$conn = mysqli_connect($servername, $username, $password, $database);

// Check connection

if (!$conn) {
      die("Connection failed: " . mysqli_connect_error());
}
 
echo "<p>Success!</p><br>";

if (isset($_POST['email'])) {
	$email = mysqli_real_escape_string($conn, $_POST['email']);
  } else {
	echo '<p>It looks like you forgot to enter your email.</p>';
  }

  if (isset($_POST['password'])) {
	$password = mysqli_real_escape_string($conn, $_POST['password']);
  } else {
	echo '<p>It looks like you forgot to enter your password.</p>';
  }
$sql = "SELECT * FROM users WHERE email = '$email' AND password = '$password' AND roleid='2'";
$ssql = "SELECT * FROM users WHERE email = '$email' AND password = '$password' AND roleid='1'";
if(mysqli_query($conn, $ssql)->num_rows){

    header("location:servicelogin.html");
}
else if(mysqli_query($conn, $sql)->num_rows){
    header("location:clientlogin.html");
}


else {
    echo "Error: " . $sql . "<br>" . mysqli_error($conn);
}


mysqli_close($conn);

?>