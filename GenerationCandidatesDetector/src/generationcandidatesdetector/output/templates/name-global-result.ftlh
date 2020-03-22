<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Name similarity results</title>
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
    		{ "type": "num_ignore_text", "targets": 2 }
  		],
  		"order": [[ 2, "desc" ]]
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
			<h1>Name similarity results</h1>
		</header>
		<article id="results">
			<table id="results-table">
				<caption>Name similarity results</caption>
				<thead>
					<tr>
						<th>Name <a data-toggle="modal" data-target="#modal-Name">
									<img src="img/help.png" width="17px" height="17px" />
							</a> <!-- Modal -->
								<div id="modal-Name" class="modal fade" role="dialog">
									<div class="modal-dialog">
										<!-- Modal content-->
										<div class="modal-content">
											<div class="modal-header">
												<button type="button" class="close" data-dismiss="modal">&times;</button>
												<h4 class="modal-title">Name</h4>
											</div>
											<div class="modal-body">
												<p>To identify structures, the names of the model
													classes are searched in the rest of the file names and
													replaced by a key word, so that related files end up with a
													similar name. This generic name is adopted as the name of
													the structure</p>
											</div>
											<div class="modal-footer">
												<button type="button" class="btn btn-default"
													data-dismiss="modal">Close</button>
											</div>
										</div>
									</div>
								</div></th>
						<th>Number of elements</th>
						<th>Total similarity <a data-toggle="modal"
								data-target="#modal-TotalSimilarity"> <img
									src="img/help.png" width="17px" height="17px" />
							</a> <!-- Modal -->
								<div id="modal-TotalSimilarity" class="modal fade" role="dialog">
									<div class="modal-dialog">
										<!-- Modal content-->
										<div class="modal-content">
											<div class="modal-header">
												<button type="button" class="close" data-dismiss="modal">&times;</button>
												<h4 class="modal-title">Total similarity</h4>
											</div>
											<div class="modal-body">
												<p>The total similarity of a structure is a weighted
													measure calculated from all the other measures of the
													structure, including mainly the code similarity between its
													classes or files some
													extra measures like the one extracted from the project
													repository.</p>
												<p>It is a measure of the amount of code
													shared among the classes or files of the structure.</p>
											</div>
											<div class="modal-footer">
												<button type="button" class="btn btn-default"
													data-dismiss="modal">Close</button>
											</div>
										</div>
									</div>
								</div></th>
					</tr>
				</thead>
				<tbody>
					<#list resultFiles as resultFile>
						<#assign result = results?api.get(resultFile)>
						<tr class='clickableRow' data-href='${resultFile.name}'>
							<td>${result.name}</td>
							<td>${result.resultNames?size}</td>
							<td>
								<#if simResults?api.get(result)??>
									<#assign simResult = simResults?api.get(result)>
									${simResult.totalSimilarity?string["0.##"]}
								<#else>
									Not applied
								</#if>
							</td>
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