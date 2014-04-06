<%@ page language="java" contentType="text/html; charset=US-ASCII"
    pageEncoding="US-ASCII"%>
 <%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=US-ASCII">
<title>Insert title here</title>
</head>
<body>	
   <h2>${message}</h2>
   <a href="${logouturl}">logout</a>
   
   <button id="getData">getData</button>   
 
   <form method="GET" action="">
   
   </form>
 
    <c:if test="${not empty lists}">
		<ul>
			<c:forEach var="listValue" items="${lists}">
				<li>${listValue}</li>
			</c:forEach>
		</ul>
 
	</c:if>
 
   <script src="//ajax.googleapis.com/ajax/libs/jquery/1.11.0/jquery.min.js"></script>
   <script type="text/javascript" src="/_ah/channel/jsapi"></script>
   <script type="text/javascript">
	$(document).ready(function(){
		var channel = new goog.appengine.Channel('${token}');
		var socket = channel.open();
	    socket.onopen = onOpened;
	    socket.onmessage = onMessage;
	    socket.onerror = onError;
	    socket.onclose = onClose;
	    
	    var g = {"id":"1"
	    		,"content":"my content"};
	   // alert(JSON.stringify(g));
	    
	    $("#getData").on('click',function(e){
	    	e.preventDefault();
	    	$.ajax({
	    		type:"POST",
	    		url:"/profile",
	    		contentType: "application/json; charset=utf-8",
	    		dataType:"json",
	    		data:JSON.stringify(g),
	    		success:function(data){
	    //			alert(JSON.stringify(data));
	    		}
	    	});
	    });
	    
	});
	
  	function onOpened(){
		//alert("opened");
	}
	
	function onMessage(data){
		alert(data['data']);
	}
	
	function onError(){
		alert("onError");
	}
	
	function onClose(){
		alert("onClose");
	}
</script> 
   
</body>
</html>