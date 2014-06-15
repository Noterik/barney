<html>
<head>
	<script>
		function showPage(page){

				document.getElementById('page1').style.display = 'block';
				document.getElementById('page2').style.display = 'none';

		}

		function getHttpRequest(){
			var http;
			if (window.ActiveXObject){
			    http = new ActiveXObject("Microsoft.XMLHTTP");
			}
			else if (window.XMLHttpRequest){
			    http = new XMLHttpRequest();
			}
			return http;
		}

		function getSelectedItem(selectBox){
			var box = document.getElementById(selectBox);
			var item = '';
			for(var i=0;i<box.options.length;i++){
				if(box.options[i].selected == true){
					item = box.options[i].value;
				}
			}
			return item;
		}

		function sendRequest() 
		{
			var uri = document.getElementById('uri').value;
			var password = document.getElementById('password').value;
			var method = getSelectedItem('method');
			var http = getHttpRequest();
			http.onreadystatechange = function() 
			{
				if(http.readyState==4)
				{
					try 
					{
						var div = document.getElementById("result");
						var text = '<TEXTAREA NAME="naam" ROWS="13" COLS="100">'+http.responseText+'</TEXTAREA>';	
						div.innerHTML = text;								
					} 
					catch(e) 
					{
						alert(e);
					}
				}
			}
			try 
			{
				http.open(method, uri);
				http.setRequestHeader('Content-Type', 'text/xml');
				http.send('password=' + password);			
				} catch(e) {
				alert(e);
			}
		}
	</script>
</head>
<body>

<div id="page1" style="position:absolute; top:60px; width:800px; height:600px;display:none; border: 1px solid #000000;">

</div>

<div id="page2" style="position:absolute; top:60px; width:800px; height:600px; border: 1px solid #000000;">
	<table>
		<tr>
			<td>
				<strong>Method</strong>
			</td>
			<td>
				<select id="method">
					<option value="POST">POST</option>
				</select>
			</td>
		</tr>
		<tr>
			<td>
				<strong>URI</strong>
			</td>
			<td>
				<input style="width:700px;" type="text" id="uri" value="http://localhost:8080/barney/restlet/domain/springfield/user/admin/login" ></input>
			</td>
		</tr>
		<tr>
			<td>
				<strong>Password</strong>
			</td>
			<td>
				<input style="width:700px;" type="password" id="password" value="ntk12"></input>
			</td>
		</tr>
		<tr>
		
			<td style="float:right;">
				<input type="button" onClick="sendRequest()" value="Send request"/>
			</td>
		</tr>
	</table>
	<div id="result"></div>
	<br><br><br><a href=index.html><b>Back..</b></a>
</div>
</body>
</html>