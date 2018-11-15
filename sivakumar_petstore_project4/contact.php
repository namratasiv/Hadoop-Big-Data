<?php
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

if (isset($_POST['fname'])) {
	$fname = mysqli_real_escape_string($conn, $_POST['fname']);
  } else {
	echo '<p>It looks like you forgot to enter your first name.</p>';
  }
  if (isset($_POST['lname'])) {
	$lname = mysqli_real_escape_string($conn, $_POST['lname']);
  } else {
	echo '<p>It looks like you forgot to enter your last name.</p>';
  }
  if (isset($_POST['email'])) {
	$email = mysqli_real_escape_string($conn, $_POST['email']);
  } else {
	echo '<p>It looks like you forgot to enter your email.</p>';
  }
  if (isset($_POST['phone'])) {
	$phone = mysqli_real_escape_string($conn, $_POST['phone']);
  } else {
	echo '<p>It looks like you forgot to enter your phone number.</p>';
  }if (isset($_POST['comments'])) {
	$comments = mysqli_real_escape_string($conn, $_POST['comments']);
  } else {
	echo '<p>It looks like you forgot to enter your comments.!</p>';
  }
 
$sql = "INSERT INTO contact (fname, lname, email, phone, comments) VALUES ('$fname', '$lname', '$email', '$phone', '$comments')";
if (mysqli_query($conn, $sql)) {
      echo "<p>We will get in touch with you soon!</p><br>";
} else {
      echo "Error: " . $sql . "<br>" . mysqli_error($conn);
}
echo '<a href="index.html"> Get Back Home!</a>';
mysqli_close($conn);

?>