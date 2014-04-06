<%@ page language="java" contentType="text/html; charset=US-ASCII"
    pageEncoding="US-ASCII"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=US-ASCII">
<title>Create your profile</title>
</head>
<body>
	
	<form id="nameForm" method="POST" action="?">
		<table>
    	 <tr>
        	<td><label for="name">Profile Name</label></td>
        	<td><input id = "name" type="text"/></td>
    	 </tr>
    	 <tr>
        <td colspan="2">
            <input type="submit" value="Submit"/>
        </td>
    </tr>
    	</table>
    </form>
	<script src="//ajax.googleapis.com/ajax/libs/jquery/1.11.0/jquery.min.js"></script>
    <script type="text/javascript">
   		$(document).ready(function(){
   			$("#nameForm").submit(function(e){
   				e.preventDefault();
   				var userName = $("#name").val();
   				var userObj = {
   						"profileName":userName
   				}
   				$.ajax({
   		    		type:"POST",
   		    		url:"/api/userData",
   		    		contentType: "application/json; charset=utf-8",
   		    		dataType:"json",
   		    		data:JSON.stringify(userObj),
   		    		success:function(data){
   		    			window.location.href = "/profile";
   		    		},
   		    		error:function(jqXHR,textStatus,errorThrown){
   		    			alert("error");
   		    		}
   		    	});
   			});
   			
   			
   		});
    
    </script>
</body>

</html>