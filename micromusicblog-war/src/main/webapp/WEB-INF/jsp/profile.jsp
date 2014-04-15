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

	<!-- Metro UI CSS JavaScript plugin -->
	<script src="lib/load-metro.js"></script>

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
				<a href="/profile">Profile</a>
			</item>
			<span class="element-divider place-right"></span>
			<item class="element place-right">
				<form id="searchUserForm">
					<div class="input-control text">
						<input type="text" id="searchUserName" placeholder="Search User"/>
					</div>
					<div class="input-control text">
						<input type="submit" value="Search">
					</div>
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
			</div>
			
			<!-- buttons -->
			<div>
				<button class="button" id="startRecord">
					Start Recording
				</button>
				<button class="button inverse" id="stopRecord">
					Stop Recording
				</button>
				<button class="button inverse" id="postRecord">
					Post Record
				</button>
			</div> <!-- buttons -->
		</header>

		<div class="main-content clearfix">
			<div class="tile-area no-padding clearfix">
				<div class="tile-group no-margin no-padding clearfix" style="width: 100%;">
					<div class="tile double quadro-vertical bg-gray ol-transparent" style="float: right;">
						Ads here...
					</div> <!-- quadro-vertical -->
					<div id="postContainer">
						<c:forEach var="postInfo" items="${postInfos}">
						<div class="tile voiceTile ol-transparent" >
							${postInfo.creater} <br/>
							${postInfo.comment}<br/>
							<button class="button" onclick="clickToPlay('${postInfo.postKey}')"></i>
							</div> <!-- tile -->
						</c:forEach>
					</div> <!-- postContainer -->
				</div> <!-- tile group -->
			</div> <!-- tile area -->
		</div> <!-- main content -->

<%--  <div>
    <c:if test="${not empty lists}">
		<ul style="list-style-type:none">
			<c:forEach var="listValue" items="${lists}">
				<li>${listValue.profileName}</li>
			</c:forEach>
		</ul>
 
	</c:if>
</div> --%>

<!-- js for channel api -->
<script type="text/javascript" src="/_ah/channel/jsapi"></script>
<!-- load at the end so that page does not take forever to load -->
<script type="text/javascript" src="lib/RecordRTC.min.js"></script>

<script type="text/javascript">
	var tout; 		// Timeout variable (for recording)
	var interval;

	function startProgress() {
		var pb = $('#pb').progressbar();
		var progress = 0;
		interval = setInterval(function() {
			pb.progressbar('value', Math.floor((++progress)/2));
			if (progress >= 100) window.clearInterval(interval);
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

	var clickToPlay = function(postKey){
		var safeString = encodeURI(postKey);
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
					audioType = 'audio/wav';
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
    	$('#postRecord').prop("disabled",true);
    	
    	//add random color to our tiles
    	var tileClasses=["bg-lightBlue","bg-orange","bg-red","bg-teal","bg-yellow","bg-green","bg-blue"];
    	$(".voiceTile").each(function(i,obj){
    		var num = Math.floor(Math.random() * 7);
    		$(this).addClass(tileClasses[num]);
    	});
    	
    	 //random between 0 and 6

    	 var audioStream;
    	 var recorder;
    	 var recording = false;
    	 var audioConstraints = {
    	 	audio: true,
    	 	video: false
    	 };

    	 var isFirefox = !!navigator.mozGetUserMedia;
	   // alert(JSON.stringify(g));

	/*     $("#getData").on('click',function(e){
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
}); */

$("#startRecord").on('click',function(){
	clearProgress();
	$("#startRecord").addClass("inverse");
	$("#stopRecord").removeClass("inverse");
	var duration_in_seconds = 20; //we record 20sec of audio max

	if (!audioStream){
		navigator.getUserMedia(audioConstraints, function(stream) {
			if (window.IsChrome) {
				stream = new window.MediaStream(stream.getAudioTracks());
			}
			audioStream = stream;
			recorder = window.RecordRTC(stream, {
				type: 'audio'
			});

			// start recording
			recorder.startRecording();
			startProgress();
			$("#stopRecord").prop('disabled', false);
			recording = true;
			tout = setTimeout(function() {
				if(recording){
					if (recorder){
						// alert("record stopped");
						stopProgress();
						$("#startRecord").prop('disabled', false);
						$("#stopRecord").prop('disabled', true);
						$("#postRecord").prop('disabled', false);
						$("#startRecord").removeClass("inverse");
						$("#stopRecord").addClass("inverse");
						$("#postRecord").removeClass("inverse");
						recorder.stopRecording(function(url) {
							if (isFirefox){
								resultBlob = recorder.getBlob();
							}
						});
						recording =false;
						if (!isFirefox){
							var blob = recorder.getBlob();
							alert("Only Firefox is supported at this time.");
							if(blob!==null)
								resultBlob = blob;
						}

					}
				}
			}, duration_in_seconds*1000);
		}, function(err) {
			alert(err);
		});
}
else {
	if (recorder){
		clearProgress();
		recorder.startRecording();
		startProgress();
		recording = true;
		$("#stopRecord").prop('disabled', false);;
	}
	tout = setTimeout(function() { 
		if (recorder){
			stopProgress();
			$("#startRecord").prop('disabled', false);
			$("#stopRecord").prop('disabled', true);
			$("#postRecord").prop('disabled', false);
			$("#startRecord").removeClass("inverse");
			$("#stopRecord").addClass("inverse");
			$("#postRecord").removeClass("inverse");
			recorder.stopRecording(function(url) {
				if (isFirefox){
					resultBlob = recorder.getBlob();
				}
			});
			recording = false;

			if (!isFirefox){
				var blob = recorder.getBlob();
				resultBlob = blob;
			}
		}
		    }, duration_in_seconds*1000);
}
window.isAudio = true;
$("#startRecord").prop('disabled', true);
}); 

$("#stopRecord").on('click',function(){
	window.clearTimeout(tout);
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
		recording = false;

		if (!isFirefox){
			var blob = recorder.getBlob();
			resultBlob = blob;
		}
		resultBlob = recorder.getBlob();
		          //var blob = recorder.getBlob();
		      }

		  });

$("#postRecord").on('click',function(){
	$("#postRecord").addClass("inverse");
	$("#postRecord").prop('disabled', true);
	if(resultBlob){
		var fd = new FormData();
				 //if(isFireFox)

				 fd.append("format","ogg");
				 var audioFormat = resultBlob.type;
				 fd.append(audioFormat,resultBlob);
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
			                +"' >"+result.creater+"<br/>"+com
			                +"<button class='button' onclick=\"clickToPlay('"+result.postKey+"')\">Play</button></div></div>";

			                /*
			                newPostDiv="<div class='voiceTile tile ol-transparent "+tileClasses[num]
			                +"' >"+result.creater+"<br/>"+com
			                +"<button onclick=clickToPlay('"+result.postKey+"')>Play</button></div></div>";
			                */

			                var currHtml = $("#postContainer").html();
			                currHtml = newPostDiv + currHtml;
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
			    	window.location.href = newUrl;
			    }
			}); 
	});//end of document.ready


function onOpened(){
		//alert("opened");
	}
	
	function onMessage(data){
		alert("onMessage! "+JSON.stringify(data));
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