/**
 * 
 */

var xmlhttp;
function loadXMLDoc(url,cfunc)
{
	if (window.XMLHttpRequest)
	  {// code for IE7+, Firefox, Chrome, Opera, Safari
	  	xmlhttp=new XMLHttpRequest();
	  }
	else
	  {// code for IE6, IE5
	  	xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
	  }
	xmlhttp.onreadystatechange=cfunc;
	xmlhttp.open("GET",url,true);
	xmlhttp.send();
}
function myFunction()
{
loadXMLDoc("/app/projects/solum-team-meeting/",function()
  {
  if (xmlhttp.readyState==4 && xmlhttp.status==200)
    {
	    var count = 1;
		var table=document.getElementById("dataTable");
    		var results=xmlhttp.responseText.split(",");
    		console.log(results);
    		for (var i = 0; i < results.length; i++){
    			console.log(results[i]);
    			var spl = results[i].split(" ");
    			console.log(spl);
    			var row = table.insertRow(count);
    			var cell1 = row.insertCell(0);
    			var cell2 = row.insertCell(1);
    			cell1.innerHTML = spl[0];
    			cell2.innerHTML = spl[1];
    			count++;
    		}
    }
  });
}