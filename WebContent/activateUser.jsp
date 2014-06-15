<html>
<head>
	<script>

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

		function sendRequest() {
			var uri = document.getElementById('uri').value;
			var value = document.getElementById('value').value;
			var method = getSelectedItem('method');
			var http = getHttpRequest();
			http.onreadystatechange = function() {
				if(http.readyState==4){
					try {
						var div = document.getElementById("result");
						var text = '<TEXTAREA NAME="naam" ROWS="13" COLS="100">'+http.responseText+'</TEXTAREA>';	
						div.innerHTML = text;	
					} catch(e) {
						alert(e);
					}
				}
			}
			try {
				http.open(method, uri);
				http.setRequestHeader('Content-Type', 'text/xml');
				http.send('hash=' + value);		
				} catch(e) {
				alert(e);
			}
		}
	</script>
</head>
<body>

<div style="position:absolute; top:60px; width:800px; height:600px; border: 1px solid #000000;">
	<table>
		<tr>
			<td>
				<strong>Method</strong>
			</td>
			<td>
				<select id="method">
					<option value="POST">POST</option>
					<option value="PUT">PUT</option>
					<option value="GET">GET</option>
					<option value="DELETE">DELETE</option>
				</select>
			</td>
		</tr>
		<tr>
			<td>
				<strong>URI</strong>
			</td>
			<td>
				<input style="width:700px;" type="text" id="uri" value="http://localhost:8080/barney/restlet/domain/springfield/user/admin/activate" ></input>
			</td>
		</tr>
		<tr>
			<td>
				<strong>Value</strong>
			</td>
			<td>
				<input style="width:700px;" type="text" id="value" ></input>
			</td>
		</tr>
		<tr>
			<td>
				&nbsp;
			</td>
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