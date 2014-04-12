<%@ page language="java" contentType="text/html; charset=US-ASCII"
pageEncoding="US-ASCII"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta charset="utf-8">
	<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">

	<link href="css/metro-bootstrap.css" rel="stylesheet">
	<link href="css/metro-bootstrap-responsive.css" rel="stylesheet">
	<link href="//netdna.bootstrapcdn.com/font-awesome/4.0.3/css/font-awesome.css" rel="stylesheet">

	<!-- Load JavaScript Libraries at the end for performance reason-->
	<script src="//code.jquery.com/jquery-2.1.0.min.js"></script>
	<script src="lib/jquery/jquery.ui.widget.min.js"></script>
	<script src="lib/jquery/jquery.mousewheel.js"></script>

	<!-- Metro UI CSS JavaScript plugin -->
	<script src="lib/load-metro.js"></script>

	<title>Search result</title>
</head>
<body class="metro">
	<div class="container">
		<div>
			<a href="/profile">Back</a>
		</div>
		<div class="listview small">
			<c:if test="${not empty lists}">
			<c:forEach var="user" items="${lists}">
			<a href="#" class="list">
				<div class="list-content">
					${user.profileName}
					<div>
						<input class="followAccount" name="account" type="hidden" value="${user.accountName}">
						<button class="followUser">${user.isFollowing ? "unfollow" : "follow"}</button>
					</div>
				</div> <!-- list content -->
			</a>
			</c:forEach>
			<c:else>
				Not found.
			</c:if>
		</div>
	</div>
<!--    <c:if test="${not empty lists}">
		<ul style="list-style-type:none">
			<c:forEach var="user" items="${lists}">
				<li >${user.profileName}</li>
				<div>
				<input class="followAccount" name="account" type="hidden" value="${user.accountName}">
				<button class="followUser">${user.isFollowing ? "unfollow" : "follow"}</button>
				</div>
			</c:forEach>
		</ul>
 
	</c:if> -->
	
	<script src="//code.jquery.com/jquery-2.1.0.min.js"></script>
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