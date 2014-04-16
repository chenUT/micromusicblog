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
	<link href="//netdna.bootstrapcdn.com/font-awesome/4.0.3/css/font-awesome.css" rel="stylesheet">

	<!-- Load JavaScript Libraries at the end for performance reason-->
	<script src="//code.jquery.com/jquery-2.1.0.min.js"></script>
	<script src="lib/jquery/jquery.ui.widget.min.js"></script>
	<script src="lib/jquery/jquery.mousewheel.js"></script>

	<!-- JavaScript plugin -->
<!--    <script src="lib/load-metro.js"></script> -->
	<script src="lib/metro.min.js"></script>
	<script src="lib/RecordRTC.min.js"></script> 
	<title>MMB: Recording and remixing for the rest of us!</title>
</head>
<body class="metro">
	<nav class="navigation-bar dark">
		<nav class="navigation-bar-content">
			<item class="element">
				<!-- NOTE: spaces are important for proper layout -->
				<i class="fa fa-music"></i>  <a href="/">MMB</a>
			</item>
			<span class="element-divider"></span>
            <!-- These elements will appear in reverse order using 'place-right' class -->
            <item class="element place-right">
                <i class="fa fa-sign-out"></i> <a href="${logouturl}">Logout</a>
            </item>
            <span class="element-divider place-right"></span>
            <item class="element place-right">
               <form id="searchUserForm" style="top:-5px;">
					<input type="text" id="searchUserName" placeholder="Search User Name">
					<input type="submit" value="Search">     			
   			   </form>
            </item>
            <span class="element-divider place-right"></span>
        </nav>
		<div class="progress-bar small" data-role="progress-bar" id="pb">
		</div>
	</nav>

	<!-- main content -->
	<div id="wrap" class="container">
		<header class="margin20 nrm nlm">
			 <div class="clearfix">
			     <a class="place-left">
			         <h1>{${userProfileName}}</h1>
			     </a>
			     <div class="grid fuild">
				  <div class="row">
				  
	            	<div class=" span2 padding2" style="text-align: center; background-color:#EDEDED">Listening<br/>${followingCount}</div>
	  				<div class=" span2 padding2" style="text-align: center; background-color:#EDEDED">Post<br/>${postCount}</div>
				   	<div class=" span2 padding2" style="text-align: center; background-color:#EDEDED">Listener<br/>${followerCount}</div>
				 </div>
			   	</div>
			 </div>
			  
			 <div class="clearfix">
				 <button class="button" id="startRecord">
				     Start Recording
				 </button>
				 <button class="button inverse" id="stopRecord">
				     Stop Recording
				 </button>
				 <button class="button" id="postRecord">
				     Post Record
				 </button>
			 </div>
		</header>
    
	     <div class="main-content">	     
	             <div class="row">               
		               <div class="span12" id="postContainer">
						<c:forEach var="postInfo" items="${postInfos}">
								<div class="voiceTile tile ol-transparent">
									<div class="tile-content" style="color:white;">
										${postInfo.creater}<br/>
										${postInfo.comment}
										<button onclick="clickToPlay('${postInfo.postKey}')">Play</button><br/><br/>
										<button onclick="clickToMix('${postInfo.postKey}')">Mix</button>
									</div>
								</div>
							</c:forEach>
		               </div>
	               </div><!-- End row -->
	    </div><!-- End of tiles -->
	</div><!-- end main content (div class="container")-->

<!-- js for channel api -->
<script type="text/javascript" src="/_ah/channel/jsapi"></script>

<script type="text/javascript">
	var tout; 		// Timeout variable (for recording)
	var interval;
 	var isPlaying=false;
 	var isFirefox = !!navigator.mozGetUserMedia;
 	
   	var audioStream;
	var recorder=null;
	var resultBlob = null;
	var backgroundKey = null;
	var audioConstraints = {
	                    audio: true,
	                    video: false
	                };
	
 	
 	
	function startProgress() {
		var pb = $('#pb').progressbar();
		var progress = 0;
		interval = setInterval(function() {
			pb.progressbar('value', Math.floor((++progress)/2));
			if (progress >= 201){ 
				window.clearInterval(interval);
			}
		}, 100);
		console.log('starting progress '+interval);
	};

	function stopProgress() {
		console.log('stopping progress '+interval);
		window.clearInterval(interval);
	};

	function clearProgress() {
		var pb = $('#pb').progressbar();
		progress = 0;
		pb.progressbar('value', 0);
		console.log('clearing progress ');
	};
	

	
	var clickToMix = function(backgroundKey1){
		var safeStringBackground = encodeURI(backgroundKey1);
		 backgroundKey = safeStringBackground;
		if(!isPlaying){
	        $.ajax({
	                type:"GET",
	                url:"/api/postData/"+safeStringBackground,
	                success:function(result){
	                        var byteCharacters = atob(result.data[0]);
	
	                        var byteNumbers = new Array(byteCharacters.length);
	                        for (var i = 0; i < byteCharacters.length; i++) {
	                                byteNumbers[i] = byteCharacters.charCodeAt(i);
	                        }
	                        var byteArray = new Uint8Array(byteNumbers);
	                        var audioType = result.format[0];
	                        var receiveBlob = new Blob([byteArray],{type:audioType});
	                //alert(receiveBlob.size);
	                var audio = document.createElement('audio'); 
	                audio.src = window.URL.createObjectURL(receiveBlob);
	                audio.play();
	                //at same time we start recording
	                clearProgress();
					 //$("#stopRecord").removeClass("inverse");
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
					           startProgress();
					           //$("#stopRecord").prop('disabled', false);
					       }, function(err) {
					       	alert(err);
					       });
				    }
				    else {
				        if (recorder){
				        	recorder.startRecording();
				        	startProgress();
				        	//$("#stopRecord").prop('disabled', false);;
				        }				        
					    }
					    window.isAudio = true;
					    $("#startRecord").prop('disabled', true);
					     
	               
	                $(audio).bind("ended",function(){
	                	isPlaying=false;
        				$("#startRecord").removeClass("inverse");
        				//$("#stopRecord").addClass("inverse");
        				$("#startRecord").prop('disabled', false);
        				$("#stopRecord").prop('disabled', true);
        				$("#postRecord").prop('disabled', false);
        				$("#postRecord").removeClass("inverse");
	        			 if (recorder){
	        				  stopProgress();
	        		          recorder.stopRecording(function(url) {
	        		        	  if (isFirefox){
	        			         		resultBlob = recorder.getBlob();
	        			         	}
	        			          });
	        			          if (!isFirefox){
	        			          	var blob = recorder.getBlob();
	        			          	resultBlob = blob;
	        			          }
	        		      }
	                });
	                isPlaying=true;
	                //alert("playing");
	            },
	            error:function(a,b,c){
	                    alert(a+" "+b+" "+c);
	            }
         });
		};
		
		
	};
	
	var clickToPlay = function(postKey){
		var safeString = encodeURI(postKey);
		if(!isPlaying){
	        $.ajax({
	                type:"GET",
	                url:"/api/postData/"+safeString,
	                success:function(result){
	                	var audio=[audio1 ,audio2];
	                		var resultLength = result.data.length;
	                		for(var i=0; i<resultLength ; i++){
	                			 var byteCharacters = atob(result.data[i]);
	                			 var audioType = result.format[i];	
	 	                        var byteNumbers = new Array(byteCharacters.length);
	 	                        for (var i = 0; i < byteCharacters.length; i++) {
	 	                                byteNumbers[i] = byteCharacters.charCodeAt(i);
	 	                        }
	 	                        var byteArray = new Uint8Array(byteNumbers);
	 	                        
	 	                        var receiveBlob = new Blob([byteArray],{type:audioType});
	 	                		//alert(receiveBlob.size);
	 	               		    audio[i] = document.createElement('audio'); 
	 	                		audio[i].src = window.URL.createObjectURL(receiveBlob);
	 	                		audio[i].mediaGroup = "mixGroup";
	 	                		audio[i].controller.play();
	                		}	
	                       
	               
	                audio[0].controller.play();
	                $(audio[0]).bind("ended",function(){
	                	isPlaying=false;
	                });
	                isPlaying=true;
	                //alert("playing");
	            },
	            error:function(a,b,c){
	                    alert(a+" "+b+" "+c);
	            }
         });
		};
 	};
   
 	
	var tileClasses=["bg-lightBlue","bg-orange","bg-red","bg-teal","bg-yellow","bg-green","bg-blue"];
	var durationInSeconds = 20; //we record 20sec of audio max
	
	$(document).ready(function(){
		var channel = new goog.appengine.Channel('${token}');
		var socket = channel.open();
	
	    socket.onopen = onOpened;
	    socket.onmessage = onMessage;
	    socket.onerror = onError;
	    socket.onclose = onClose;

	    function hasGetUserMedia() {
    	  return !!(navigator.getUserMedia || navigator.webkitGetUserMedia ||
    	            navigator.mozGetUserMedia || navigator.msGetUserMedia);

    	}

    	if (hasGetUserMedia()) {
    		//good to go
    	} else {
    		alert('getUserMedia() is not supported in your browser');
    	}

    	$("#stopRecord").prop("disabled",true);
    	$('#postRecord').prop("disabled",true);
    
    	$(".voiceTile").each(function(i,obj){
    		var num = Math.floor(Math.random() * 7);
    		$(this).addClass(tileClasses[num]);
    	});

   	 var timeOutFunction = function() { 
	       	if (recorder){
	       		stopProgress();
				$("#startRecord").prop('disabled', false);
				$("#stopRecord").prop('disabled', true);
				$("#postRecord").prop('disabled', false);
				$("#startRecord").removeClass("inverse");
				$("#stopRecord").addClass("inverse");
				$("#postRecord").removeClass("inverse");
		          recorder.stopRecording(function(url) {
		              //document.getElementById('audio-url-preview').innerHTML = '<a href="' + url + '" target="_blank">Recorded Audio URL</a>';
		         	if (isFirefox){
		         		resultBlob = recorder.getBlob();
		         	}
		         	
		    });
	          if (!isFirefox){
	          	var blob = recorder.getBlob();
	          	if(blob!==null)
	          		resultBlob = blob;
	          }
	       }
 };
    	

		 $("#startRecord").on('click', function(){
			 clearProgress();
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
			           startProgress();
			           $("#stopRecord").prop('disabled', false);
			           
					   tout = setTimeout(timeOutFunction, durationInSeconds*1000);//we record 15sec of audio max
			       }, function(err) {
			       	alert(err);
			       });
		    }
		    else {
		        if (recorder){
		        	recorder.startRecording();
		        	startProgress();
		        	$("#stopRecord").prop('disabled', false);;
		        }
		        tout = setTimeout(timeOutFunction, durationInSeconds*1000);//we record 15sec of audio max
			    }
			    window.isAudio = true;
			    $("#startRecord").prop('disabled', true);
		 }); 
		    
		 $("#stopRecord").on('click',function(){
			 	clearTimeout(tout);
				$("#startRecord").removeClass("inverse");
				$("#stopRecord").addClass("inverse");
				$("#startRecord").prop('disabled', false);
				$("#stopRecord").prop('disabled', true);
				$("#postRecord").prop('disabled', false);
				$("#postRecord").removeClass("inverse");
			 if (recorder){
				  stopProgress();
		          recorder.stopRecording(function(url) {
		        	  if (isFirefox){
			         		resultBlob = recorder.getBlob();
			         	}
			          });
			          if (!isFirefox){
			          	var blob = recorder.getBlob();
			          	resultBlob = blob;
			          }
		          resultBlob = recorder.getBlob();
		      }
		 });
		 
		 $("#postRecord").on('click',function(){
			 var postType;

			 if(backgroundKey === null){
				 postType = "new";
			 }
			 else{
				 postType = backgroundKey;
			 }
			 if(resultBlob){
				 var fd = new FormData();
				 var audioFormat = resultBlob.type;
                 fd.append("format",audioFormat);
				 fd.append(audioFormat,resultBlob);
				 //alert(resultBlob.size);
				 $.ajax({
					    type: 'POST',
					    url: '/api/postData/'+postType,
					    data: fd,
					    processData: false,
					    contentType: false,
					    success: function(result) {
					    	alert("Success");
					    	resultBlob = null;
					    	backgroundKey=null;
					    	$("#postRecord").prop('disabled', true);
			        	},
			        	error: function (xhr, ajaxOptions, thrownError) {
			                if (xhr.readyState == 0 || xhr.status == 0) {
			                    // not really an error
			                    $("#postRecord").prop('disabled', true);
			                    resultBlob = null;
			                    backgroundKey=null;
			                    return;
			                } else {
			                	alert("XHR Status = "+xhr.status);
			                	alert("Thrown Error = "+thrownError);
			                	alert("AjaxOptions = "+ajaxOptions);
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
			    	window.location.href = newUrl;
			    }
			}); 
	});//end of document.ready


	function onOpened(){
			//alert("opened");
	}
		
	function onMessage(data){
		var jsonData = JSON.parse(data.data);

		if(jsonData.comment === null){
			com = "no comment";
		}
		else{
			com = jsonData.comment;
		}
		var newPostDiv="<div class='voiceTile tile ol-transparent "+tileClasses[Math.floor(Math.random() * 7)]
			+"'><div class='tile-content' style='color:white;'>"
			+jsonData.creater+"<br/>"+com
			+"<button onclick=clickToPlay('"+jsonData.postKey+"')>Play</button><br/><br/><button onclick=clickToMix('"+jsonData.postKey+"')>Mix</button></div></div>";

		var currHtml = $("#postContainer").html();
		currHtml = newPostDiv+currHtml;
        $("#postContainer").html(currHtml); 
	}

	function onError(){
		//alert("onError");
	}

	function onClose(){
		//alert("onClose");
	}

</script> 
</body>
</html>
