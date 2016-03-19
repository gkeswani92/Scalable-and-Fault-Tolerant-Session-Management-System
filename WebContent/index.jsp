<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>Gaurav Keswani - gk368</title>
	</head>
	<body>
		<!--  Calls the servlet on page load -->
		
		<div>
			NetID: gk368
			Session: ${session} 
			Version: ${version}
			Data: ${data}
		</div>
	
		<h1> ${message} </h1>
	
		<form method="POST" action="/index">
			<div class="container">
				<div class = "row">
					<input type="submit" name="replace" value="Replace" /> 
					<input type="text" name="message" /> 
				</div>
				<div class = "row">
					<input type="submit" name="btn-refresh" value="Refresh" /> 
				</div>
				<div class = "row">
					<input type="submit" name="btn-logout" value="Logout" />
				</div>
			</div>
			
			<br/><br/>
			<div class = "row">
				Session id: ${sessionId} 
				Version: ${version} 
				Expire At: ${expireAt}
			</div>
		</form>		
	</body>
</html>