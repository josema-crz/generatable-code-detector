<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Name similarity result</title>
<link rel="stylesheet"
	href="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css">
<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
<script
	src="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/js/bootstrap.min.js"></script>
<script src="scripts/functions.js"></script>

<link rel="stylesheet" href="css/styles.css?v=1.0">
</head>
<body>
	<nav>
		<#if parentFile??>
			<a href="${parentFile.name}">Go back</a>			
		</#if>
	</nav>

	<section id="structure">
		<header>
			<h1>Name similarity result</h1>
		</header>
		<article id="summary">
			<table id="structures-table">
				<caption>Summary information about the structure.</caption>
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
											<p>To identify structures, the names of the model classes
												are searched in the rest of the file names and replaced by a
												key word, so that related files end up with a similar name.
												This generic name is adopted as the name of the structure</p>
										</div>
										<div class="modal-footer">
											<button type="button" class="btn btn-default"
												data-dismiss="modal">Close</button>
										</div>
									</div>
								</div>
							</div></th>
						<th>Total files/classes</th>
						<th>Total similarity <a data-toggle="modal"
							data-target="#modal-TotalSimilarity"> <img src="img/help.png"
								width="17px" height="17px" />
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
					<tr>
						<td>${result.name}</td>
							<td>${result.resultNames?size}</td>
							<td>
								<#if similarityResult??>
									${similarityResult.totalSimilarity?string["0.##"]}
									<a href="${similarityResultFile.name}"> <img src="img/arrow.jpg"
										width="17px" height="17px" title="See similarity results" />
									</a>
								<#else>
									Not applied
								</#if>
							</td>
					</tr>
				</tbody>
			</table>
		</article>
		<article id="elements">
			<h2>Group elements</h2>
				<table id="elements-table">
					<caption>List of the identified elements that form the
						structure.</caption>
					<thead>
						<tr>
							<th>Element</th>
							<th>Derived from</th>
							<th>Applied heuristics <a data-toggle="modal"
								data-target="#modal-NameHeuristics"> <img src="img/help.png"
									width="17px" height="17px" />
							</a> <!-- Modal -->
								<div id="modal-NameHeuristics" class="modal fade" role="dialog">
									<div class="modal-dialog">
										<!-- Modal content-->
										<div class="modal-content">
											<div class="modal-header">
												<button type="button" class="close" data-dismiss="modal">&times;</button>
												<h4 class="modal-title">Name heuristics</h4>
											</div>
											<div class="modal-body">
												<p>During the name substitution process, some heuristics
													may be used to find additional related files.</p>
												<p>It is always better when no heuristic was necessary
													to include the file in the structure, since heuristics
													aren't perfect and false positives may appear.</p>
											</div>
											<div class="modal-footer">
												<button type="button" class="btn btn-default"
													data-dismiss="modal">Close</button>
											</div>
										</div>
									</div>
								</div></th>							
							<th>Simultaneous updates <a data-toggle="modal"
								data-target="#modal-SimultaneousUpdates"> <img
									src="img/help.png" width="17px" height="17px" />
							</a> <!-- Modal -->
								<div id="modal-SimultaneousUpdates" class="modal fade"
									role="dialog">
									<div class="modal-dialog">
										<!-- Modal content-->
										<div class="modal-content">
											<div class="modal-header">
												<button type="button" class="close" data-dismiss="modal">&times;</button>
												<h4 class="modal-title">Simultaneous updates</h4>
											</div>
											<div class="modal-body">
												<p>As an additional measure to know if a class is
													related to its domain class, we check the project
													repository.</p>
												<p>According to the updates of each class since it was
													created, we can infer some information about its relation:
												</p>
												<ul>
													<li>If &gt;80% of the times the model class gets
														updated, so does the other class, then it is probable that
														both classes are related.</li>
													<li>If &lt;20% of the times the model class gets
														updated, so does the other class, then it is probable that
														both classes are not related.</li>
													<li>If none of the above conditions are true, then we
														can not tell anything about the relation between the two
														classes.</li>
												</ul>
												<p>All these assumptions are only significant if the
													total number of updates of both classes is greater than 7.</p>
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
						<#list result.resultNames as resultName>
							<tr>
								<td>${resultName.originalName}</td>
								<td>${resultName.derivedFrom?join(", ")}</td>
								<td>None</td>
								<td>Not available</td>
							</tr>
						</#list>
					</tbody>
				</table>
			</details>
		</article>
	</section>
	<footer>
		<p>Generatable code evaluator. 2016.</p>
		<p>José Manuel Cruz Zapata</p>
	</footer>
</body>
</html>