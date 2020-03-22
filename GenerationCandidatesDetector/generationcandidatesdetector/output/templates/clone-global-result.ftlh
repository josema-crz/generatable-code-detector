<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Clone detection result</title>
<link rel="stylesheet"
	href="https://cdn.datatables.net/1.10.11/css/jquery.dataTables.min.css">
<link rel="stylesheet"
	href="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css">
<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
<script
	src="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/js/bootstrap.min.js"></script>
<script
	src="https://cdn.datatables.net/1.10.11/js/jquery.dataTables.min.js"></script>
<script src="scripts/functions.js"></script>

<link rel="stylesheet" href="css/styles.css?v=1.0">

<script>
	$(document).ready(function() {
		$('#sets-table').DataTable({
			"columnDefs" : [ {
				"type" : "num",
				"targets" : [1 , 2, 3]
			},
    		{ 
    			"type": "num_clone_set", 
    			"targets": 0
    		} ],
  			"order": [[ 0, "asc" ]]
		});
	});
</script>

</head>
<body>
	<nav>
	<#if parentFile??>
		<a href="${parentFile.name}">Go back</a>			
	</#if>
	</nav>

	<section id="clone-detection-result">
		<header>
			<h1>Clone detection result</h1>
		</header>
		<article id="sets">
			<h2>Clone sets</h2>
			<table id="sets-table">
				<caption>Clone sets</caption>
				<thead>
					<tr>
						<th>Name</th>
						<th>Number of blocks</th>
						<th>Line count</th>
						<th>Similarity</th>
					</tr>
				</thead>
				<tbody>
					<#assign index = 1>
					<#list setFiles as setFile>
						<#assign set = sets?api.get(setFile)>						
						<tr class='clickableRow' data-href='${setFile.name}'>
							<td>Clone set ${index}</td>
							<td>${set.size}</td>
							<td><#if set.lineCount = -1>-<#else>${set.lineCount}</#if></td>
							<td><#if set.similarity = -1>-<#else>${set.similarity}</#if></td>
						</tr>
						<#assign index = index + 1>
					</#list>
				</tbody>
			</table>
		</article>
	</section>
	<footer>
		<p>Generation candidates detector. 2016.</p>
		<p>Jose Manuel Cruz Zapata</p>
	</footer>
</body>
</html>