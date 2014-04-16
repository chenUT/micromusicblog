<%@ page language="java" contentType="text/html; charset=US-ASCII"
pageEncoding="US-ASCII"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
  <meta http-equiv="Content-Type" content="text/html; charset=US-ASCII">

  <link href="css/metro-bootstrap.css" rel="stylesheet">
  <link href="css/metro-bootstrap-responsive.css" rel="stylesheet">
  <link href="//netdna.bootstrapcdn.com/font-awesome/4.0.3/css/font-awesome.css" rel="stylesheet">

  <!-- Load JavaScript Libraries at the end for performance reason-->
  <script src="//code.jquery.com/jquery-2.1.0.min.js"></script>

  <title>MMB: Recording and remixing for the rest of us!</title>
</head>
<body class="metro">
	<div class="container">
    <div class="grid fluid">
      <div class="row">
        <div class="span3"></div>
        <div class="span6">
          <form id="nameForm" method="POST" action="?">
            <fieldset>
              <legend>New Profile</legend>
              <label>Name:</label>
              <div class="input-control text" data-role="input-control">
                <input id="name" type="text" placeholder="Name">
              </div>
              <input type="submit" value="Submit">
            </fieldset>
          </form>
        </div>
        <div class="span3"></div>
      </div> <!-- row -->
    </div> <!-- grid -->
  </div> <!-- container -->

  <script type="text/javascript">
   $(document).ready(function(){
    $("#nameForm").submit(function(e){
     e.preventDefault();
     var userName = $("#name").val() || "Anonymous";
     $.ajax({
       type:"POST",
       url:"/api/userData/profile/"+userName,
       contentType: "application/json; charset=utf-8",
       success:function(data){
        window.location.href = "/profile";
      },
      error:function(jqXHR,textStatus,errorThrown){
	   			//alert("error "+jqXHR+" "+textStatus+" "+errorThrown);
	   			//right now no need to send json profile, so server may thrown a error we just omit it.
	   			window.location.href = "/profile";
	   		}
	   	});
   });


  });


 </script>
 <!-- preload some stuff into browser's cache -->
 <script src="lib/jquery/jquery.ui.widget.min.js"></script>
 <script src="lib/jquery/jquery.mousewheel.js"></script>

 <!-- Metro UI CSS JavaScript plugin -->
 <script src="lib/load-metro.js"></script>

</body>

</html>