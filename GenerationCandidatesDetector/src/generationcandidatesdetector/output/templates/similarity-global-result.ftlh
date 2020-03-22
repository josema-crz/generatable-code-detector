<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Similarity results</title>
<link rel="stylesheet"
	href="https://cdn.datatables.net/1.10.11/css/jquery.dataTables.min.css">
<link rel="stylesheet"
	href="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css">
<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
<script
	src="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/js/bootstrap.min.js"></script>
<script src="https://cdn.datatables.net/1.10.11/js/jquery.dataTables.min.js"></script>
<script src="scripts/functions.js"></script>

<link rel="stylesheet" href="css/styles.css?v=1.0">

<script>
$(document).ready( function () {
    $('#results-table').DataTable({
  		"columnDefs": [
    		{ "type": "num", "targets": 1 },
    		{ "type": "num-fmt", "targets": 2 },
    		{ "type": "num-fmt", "targets": 3 }
  		],
  		"order": [[ 1, "desc" ]]
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

	<section id="similarity">
		<header>
			<h1>Similarity results</h1>
		</header>
		<article id="results">
			<table id="results-table">
				<caption>Similarity results</caption>
				<thead>
					<tr>
						<th>Code units</th>
						<th>Total similarity <a data-toggle="modal"
								data-target="#modal-total"> <img src="img/help.png"
									width="17px" height="17px" />
							</a> <!-- Modal -->
								<div id="modal-total" class="modal fade" role="dialog">
									<div class="modal-dialog">
										<!-- Modal content-->
										<div class="modal-content">
											<div class="modal-header">
												<button type="button" class="close" data-dismiss="modal">&times;</button>
												<h4 class="modal-title">Total similarity</h4>
											</div>
											<div class="modal-body">
												<p>The total measures are calculated from the individual results of each step, according to the weight assigned to them.</p>
											</div>
											<div class="modal-footer">
												<button type="button" class="btn btn-default"
													data-dismiss="modal">Close</button>
											</div>
										</div>
									</div>
								</div>
						</th>
						<th>% Code unit A</th>
						<th>% Code unit B</th>
					</tr>
				</thead>
				<tbody>
					<#list resultFiles as resultFile>
						<#assign result = results?api.get(resultFile)>
						<tr class='clickableRow' data-href='${resultFile.name}'>
							<td>${result.codeUnitA.name} / ${result.codeUnitB.name}</td>
							<#assign finalResult = result.finalResult>
							<td>${finalResult.similarity?string["0.##"]}</td>
							<td>${(finalResult.percentageCodeUnitA * 100)?string["0.##"]}%</td>
							<td>${(finalResult.percentageCodeUnitB * 100)?string["0.##"]}%</td>
						</tr>
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