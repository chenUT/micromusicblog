<%@ page language="java" contentType="text/html; charset=US-ASCII"
    pageEncoding="US-ASCII"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">

    <link href="css/metro-bootstrap.css" rel="stylesheet">
    <link href="css/metro-bootstrap-responsive.css" rel="stylesheet">
    <link href="http://netdna.bootstrapcdn.com/font-awesome/4.0.3/css/font-awesome.css" rel="stylesheet">

<!-- Load JavaScript Libraries at the end for performance reason-->
<script src="http://code.jquery.com/jquery-2.1.0.min.js"></script>
<script src="lib/jquery/jquery.ui.widget.min.js"></script>
<script src="lib/jquery/jquery.mousewheel.js"></script>

<!-- Metro UI CSS JavaScript plugin -->
<script src="lib/load-metro.js"></script>
 
 
<style>
html,
body {
  height: 100%;
  /* The html and body elements cannot have any padding or margin. */
}
	.footer {
	  position: relative;
	  margin-top: -150px; /* negative value of footer height */
	  height: 150px;
	  clear:both;
	  padding-top:20px;
	} 
	#wrap{
	  min-height: 100%;
  	height: auto;
 	/* Negative indent footer by its height */
  	margin: 0 auto -60px;
  	/* Pad bottom by footer height */
  	padding: 0 0 60px;
  }
</style> 


 <!-- record rtc JavaScript plugin -->
<!--   <script src="../lib/RecordRTC.js"></script> -->

    <title>MMB: Recording and remixing for the rest of us!</title>
</head>
<body class="metro">	
    <!-- Navbar -->
    <!-- TODO: change href in this section -->
    <nav class="navigation-bar dark">
        <nav class="navigation-bar-content">
            <item class="element">
                <!-- NOTE: spaces are important for proper layout -->
                <i class="fa fa-music"></i>  <a href="#home">MMB</a>
            </item>
            <span class="element-divider"></span>

            <!-- These elements will appear in reverse order using 'place-right' class -->
            <item class="element place-right">
                <i class="fa fa-sign-out"></i> <a href="${logouturl}">Logout</a>
            </item>
            <span class="element-divider place-right"></span>
            <item class="element place-right">
                <a href="/profile">Profile</a>
            </item>
            <span class="element-divider place-right"></span>
            <item class="element place-right">
               <form id="searchUserForm" style="top:-10px;">
					<input type="text" id="searchUserName" placeholder="Search User Name">
					<input type="submit" value="Search">     			
   			   </form>
            </item>
            <span class="element-divider place-right"></span>
        </nav>
    </nav>

    <!-- main content -->
    <div id="wrap"class="container">
		<header class="margin20 nrm nlm">
			 <div class="clearfix">
			     <a class="place-left">
			         <!-- TODO generate user name here -->
			         <h1>{${userProfileName}}</h1>
			     </a>
			 </div>
			 <button class="button" id="startRecord">
			     Start Recording
			 </button>
			 <button class="button inverse" id="stopRecord">
			     Stop Recording
			 </button>
			 <button class="button" id="postRecord">
			     Post Record
			 </button>
		</header>
    
	     <div class="main-content">	     
	             <div class="row">               
		               <div class="span8" id="postContainer">
						<c:forEach var="postInfo" items="${postInfos}">
								<div class="voiceTile tile ol-transparent">
									<div class="tile-content" style="color:white;">
										${postInfo.creater}<br/>
										${postInfo.comment}
										<button onclick="clickToPlay('${postInfo.postKey}')">Play</button>
									</div>
								</div>
							</c:forEach>
		               </div>
		               <div class="span4 pull-right">
			                <div class="tile double quadro-vertical bg-gray ol-transparent">
			                    <div class="tile-content">
			                        <div class="brand">
			                            <span class="label fg-white">ads here...</span>
			                        </div>
			                    </div>
			                </div>
		                </div>
	               </div><!-- End row -->
	    </div><!-- End of tiles -->
	</div><!-- end main content (div class="container")-->

<!-- <button id="getData">getData</button>  -->  
 


 
<%--  <div>
    <c:if test="${not empty lists}">
		<ul style="list-style-type:none">
			<c:forEach var="listValue" items="${lists}">
				<li>${listValue.profileName}</li>
			</c:forEach>
		</ul>
 
	</c:if>
 </div> --%>

<!-- 
 <div id="createPost">
	<button id="startRecord">start</button>
	<button id="stopRecord">stop</button>
	<button id="postRecord">post</button>
	<div id="audio-url-preview"></div>
 </div>
  -->

<footer class="footer">
      <div class="container">
        <p>Powered by bootstrap</p>
      </div>
</footer>

 
  <!-- js for channel api -->
 <script type="text/javascript" src="/_ah/channel/jsapi"></script>
 
 <script type="text/javascript">

 	var clickToPlay = function(postKey){
 		var safeString = encodeURI(postKey);
 		//alert(safeString);
 		 //var isFirefox = !!navigator.mozGetUserMedia;
 		$.ajax({
 			type:"GET",
 			url:"/api/postData/"+safeString,
 			success:function(result){
 				var byteCharacters = atob(result.data);
 		        
 		        var byteNumbers = new Array(byteCharacters.length);
 		        for (var i = 0; i < byteCharacters.length; i++) {
 		            byteNumbers[i] = byteCharacters.charCodeAt(i);
 		        }
 		        var byteArray = new Uint8Array(byteNumbers);
 		        var audioType;
 		        if(true){
 		        	audioType='audio/ogg';
 		        }
 		        else{
 		        	audioType = 'audio/wav'
 		        }
 		       var receiveBlob = new Blob([byteArray],{type:audioType});
 		       
 		       //alert(receiveBlob.size);

 		 	   var audio = document.createElement('audio'); 
 		        audio.src = window.URL.createObjectURL(receiveBlob);
 		        audio.play();
 		       //alert("playing");
 			},
 			error:function(a,b,c){
 				alert(a+" "+b+" "+c);
 			}
 		});
 		
 		
 	    /*  var byteCharacters = atob(result.data);
        
        var byteNumbers = new Array(byteCharacters.length);
        for (var i = 0; i < byteCharacters.length; i++) {
            byteNumbers[i] = byteCharacters.charCodeAt(i);
        }
        var byteArray = new Uint8Array(byteNumbers);
        var audioType;
        if(isFirefox){
        	audioType='audio/ogg';
        }
        else{
        	audioType = 'audio/wav'
        }
       var receiveBlob = new Blob([byteArray],{type:audioType});
       
       alert(receiveBlob.size);

 	   var audio = document.createElement('audio'); 
        audio.src = window.URL.createObjectURL(resultBlob);
        audio.play();
       alert("playing"); */
         
 	}
 
	$(document).ready(function(){
		var channel = new goog.appengine.Channel('${token}');
		var socket = channel.open();
		var resultBlob = null;
	    socket.onopen = onOpened;
	    socket.onmessage = onMessage;
	    socket.onerror = onError;
	    socket.onclose = onClose;
	    function hasGetUserMedia() {
    	  return !!(navigator.getUserMedia || navigator.webkitGetUserMedia ||
    	            navigator.mozGetUserMedia || navigator.msGetUserMedia);
    	}

    	if (hasGetUserMedia()) {
    	  // Good to go!
    	    //alert("good");
    	} else {
    	  alert('getUserMedia() is not supported in your browser');
    	}

    	$("#stopRecord").prop("disabled",true);
    	
    	//add random color to our tiles
    	var tileClasses=["bg-lightBlue","bg-orange","bg-red","bg-teal","bg-yellow","bg-green","bg-blue"];
    	$(".voiceTile").each(function(i,obj){
    		var num = Math.floor(Math.random() * 7);
    		$(this).addClass(tileClasses[num]);
    	});
    	
    	 //random between 0 and 6
    	
    	var audioStream;
    	var recorder;
    	var recording=false;
    	var audioConstraints = {
    	                    audio: true,
    	                    video: false
    	                };

    	var isFirefox = !!navigator.mozGetUserMedia;
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

		 $("#startRecord").on('click',function(){
			 $("#stopRecord").removeClass("inverse");
	            $("#startRecord").addClass("inverse");
			 if (!audioStream){
			       navigator.getUserMedia(audioConstraints, function(stream) {
			           if (window.IsChrome) {
			           		stream = new window.MediaStream(stream.getAudioTracks());
			           }
			           audioStream = stream;
			           recorder = window.RecordRTC(stream, {
			               type: 'audio'
			           });
			           recorder.startRecording();
			           $("#stopRecord").prop('disabled', false);
			           recording=true;
					   setTimeout(function() { 
						    if(recording){
						       	if (recorder){
						       		alert("record stopped");
						       		$("#startRecord").prop('disabled', false);
							          $("#stopRecord").prop('disabled', true);
							          $("#startRecord").removeClass("inverse");
							            $("#stopRecord").addClass("inverse");
							          recorder.stopRecording(function(url) {
							        	  
							              //document.getElementById('audio-url-preview').innerHTML = '<a href="' + url + '" target="_blank">Recorded Audio URL</a>';
							         	if (isFirefox){
							         		resultBlob = recorder.getBlob();
							         	
							         		
							         		}
							         	
							    });
						          recording =false;
						          
						          if (!isFirefox){
						          	var blob = recorder.getBlob();
						          	alert("no ff");
						          	if(blob!==null)
						          		resultBlob = blob;
						          }
						          
						       }
						    }
					    }, 5000);//we record 20sec of audio max
			       }, function(err) {
			       	alert(err);
			       });
			    }
			    else {
			        if (recorder){
			        	recorder.startRecording();
			        	recording=true;
			        	$("#stopRecord").prop('disabled', false);;
			        }
			        setTimeout(function() { 
			          
				      if (recorder){
				    		alert("record stopped");
				    	  $("#startRecord").prop('disabled', false);
				          $("#stopRecord").prop('disabled', true);
				          $("#startRecord").removeClass("inverse");
				            $("#stopRecord").addClass("inverse");
				          recorder.stopRecording(function(url) {
				        	  if (isFirefox){
					         		resultBlob = recorder.getBlob();
					         	}
					          });
					          recording =false;
					         
					          if (!isFirefox){
					          	var blob = recorder.getBlob();
					          	resultBlob = blob;
					          }
				         
				          
				          
				          //var blob = recorder.getBlob();
				          
				      }
			    }, 5000);//we record 20sec of audio max
			    }
			    window.isAudio = true;
			    $("#startRecord").prop('disabled', true);
		 }); 
		    
		 $("#stopRecord").on('click',function(){
			   $("#startRecord").removeClass("inverse");
	           $("#stopRecord").addClass("inverse");
	           $("#startRecord").prop('disabled', false);
		          $("#stopRecord").prop('disabled', true);
	           alert("record stopped");
			 if (recorder){
					
		          recorder.stopRecording(function(url) {
		        	  if (isFirefox){
			         		resultBlob = recorder.getBlob();
			         	}
			          });
			          recording =false;
			         
			          if (!isFirefox){
			          	var blob = recorder.getBlob();
			          	resultBlob = blob;
			          }
		          resultBlob = recorder.getBlob();
		          //var blob = recorder.getBlob();
		      }
			 
		 });
		 
		 $("#postRecord").on('click',function(){
			 if(resultBlob){
				 var fd = new FormData();
				 //if(isFireFox)
						
				 fd.append("format","ogg");
				 fd.append('recordData',resultBlob);
				 //alert(resultBlob.size);
				 $.ajax({
					    type: 'POST',
					    url: '/api/postData',
					    data: fd,
					    processData: false,
					    contentType: false,
					    success: function(result) {
					    	
			                /* $(response).html(data); */
			               //alert("data back! "+JSON.stringify(result));
			                resultJson =  JSON.stringify(result);
			               var num = Math.floor(Math.random() * 7);
			               
			                var com="";
						if(result.comment === null){
							com = "no comment";
						}
						else{
							com = result.comment;
						}
			                //create a new div
			                newPostDiv="<div class='voiceTile tile ol-transparent "+tileClasses[num]
			                				+"'><div class='tile-content' style='color:white;'>"
			                				+result.creater+"<br/>"+com
			                				+"<button onclick=clickToPlay('"+result.postKey+"')>Play</button></div></div>";
			                
			               var currHtml = $("#postContainer").html();
			               currHtml = newPostDiv+currHtml;
			               $("#postContainer").html(currHtml);
			               /*  var byteCharacters = atob(result.data);
			                
			                var byteNumbers = new Array(byteCharacters.length);
			                for (var i = 0; i < byteCharacters.length; i++) {
			                    byteNumbers[i] = byteCharacters.charCodeAt(i);
			                }
			                var byteArray = new Uint8Array(byteNumbers);
			                var audioType;
			                if(isFirefox){
			                	audioType='audio/ogg';
			                }
			                else{
			                	audioType = 'audio/wav'
			                }
			               var receiveBlob = new Blob([byteArray],{type:audioType});
			               
			               alert(receiveBlob.size);

			         	   var audio = document.createElement('audio'); 
			                audio.src = window.URL.createObjectURL(resultBlob);
			                audio.play();
			               alert("playing"); */
			                 
			        	},
			        	error: function (xhr, ajaxOptions, thrownError) {
			                if (xhr.readyState == 0 || xhr.status == 0) {
			                    // not really an error
			                    return;
			                } else {
			                    alert("XHR Status = "+xhr.status);
			                    alert("Thrown Error = "+thrownError);
			                    alert("AjaxOptions = "+ajaxOptions)
			                }
			          }
					}).done(function(data) {
					       console.log(data);
					});
			 }
			 else{
				 alert("please record something first :)");
			 }
		 });
		 
		 
		 
	     $("#searchUserForm").submit(function(e){
		    	e.preventDefault();
		    	var searchName = $("#searchUserName").val();
		    	if(searchName === null || searchName === ""){
		    		alert("please enter a name");
		    	}
		    	else{
		    		/* $.ajax({
			    		type:"GET",
			    		url:"/api/userData/name/"+searchName,
			    		contentType: "application/json; charset=utf-8",
			    		success:function(data){
			    			
			    		}
			    	}); */
			    	//redirect to search result page
			    	var newUrl = "/searchuser/"+searchName;
			    	window.location.href=newUrl;
		    	}
		    }); 
	});//end of document.ready
	
	
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
	
	function postBlob(b){
		
	}
</script> 
   
</body>
</html>