<%@ page language="java" contentType="text/html; charset=US-ASCII"
    pageEncoding="US-ASCII"%>
     <%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=US-ASCII">
<title>Search result</title>
</head>
<body>

	<div>
		<a href="/profile">Go back</a>
	</div>
   
    <c:if test="${not empty lists}">
		<ul style="list-style-type:none">
			<c:forEach var="user" items="${lists}">
				<li >${user.profileName}</li>
				<div>
				<input class="followAccount" name="account" type="hidden" value="${user.accountName}">
				<button class="followUser">${user.isFollowing ? "unfollow" : "follow"}</button>
				</div>
			</c:forEach>
		</ul>
 
	</c:if>
	
 <script src="//ajax.googleapis.com/ajax/libs/jquery/1.11.0/jquery.min.js"></script>
 <script type="text/javascript">
 	$(document).ready(function(){
 		$(".followUser").on('click',function(e){
 			e.preventDefault();
 			var followButton = $(this);
            var targetAccount = $(this).parent().find("input[name='account']").val();
            var userDetail={
 				"accountName":targetAccount	
 			};
 			if(followButton.text() === "follow"){
 				
 				//send out ajax request
 				$.ajax({
 					type:"PUT",
 					url:"/api/userData/follow",
 					contentType: "application/json; charset=utf-8",
 					dataType:"json",
 					data:JSON.stringify(userDetail),
 					success:function(){
 						followButton.text("unfollow");
 					},
   		    		error:function(jqXHR,textStatus,errorThrown){
   		    			alert("error");
   		    		}
 					
 				});
 			}
 			else if(followButton.text() === "unfollow"){
 				$.ajax({
 					type:"PUT",
 					url:"/api/userData/unfollow",
 					contentType: "application/json; charset=utf-8",
 					dataType:"json",
 					data:JSON.stringify(userDetail),
 					success:function(){
 						followButton.text("follow");
 					},
   		    		error:function(jqXHR,textStatus,errorThrown){
   		    			alert("error");
   		    		}
 					
 				});
 			}
 		});
 	});
 </script>
</body>
</html>