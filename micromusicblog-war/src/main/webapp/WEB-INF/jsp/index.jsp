<%@ page language="java" contentType="text/html; charset=US-ASCII"
pageEncoding="US-ASCII"%>
<!DOCTYPE html>
<html lang="en">
<head>
	<title>MicroMix</title>
	<meta charset="utf-8" />
	<meta name="viewport" content="initial-scale=1, minimal-ui" />

	<link href='//fonts.googleapis.com/css?family=Roboto:400,100,400italic,700italic,700' rel='stylesheet' type='text/css'>

	<link rel="stylesheet" href="css/animate.min.css" />
	<link rel="stylesheet" href="css/style.min.css" />
</head>
<body class="animated fadeInDown">
	<header class="site__header island">
		<div class="wrap">
			<span id="animationSandbox" style="display: block;"><h1 class="site__title mega">MicroMix</h1></span>
			<span class="beta subhead">an ECE1779 Project</span>
		</div>
	</header><!-- /.site__header -->

	<main class="site__content island" role="content">
		<div class="wrap">
			<a href="/profile">Login</a>
		</div>
	</main><!-- /.site__content -->

	<script src="//code.jquery.com/jquery-2.1.0.min.js"></script>
	<script>
		function testAnim(x) {
			$('#animationSandbox').removeClass().addClass(x + ' animated').one('webkitAnimationEnd mozAnimationEnd MSAnimationEnd oanimationend animationend', function(){
				$(this).removeClass();
			});
		};

		$(document).ready(function(){
			$('.js--triggerAnimation').click(function(){
				var anim = $('.js--animations').val();
				testAnim(anim);
			});

			$('.js--animations').change(function(){
				var anim = $(this).val();
				testAnim(anim);
			});
		});

	</script>

</body>
</html>
