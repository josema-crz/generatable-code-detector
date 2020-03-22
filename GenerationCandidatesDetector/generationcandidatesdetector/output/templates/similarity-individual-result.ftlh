<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Similarity result</title>
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

	<section id="similarity">
		<header>
			<h1>Similarity result</h1>
		</header>
		<article id="results">
				<a data-toggle="modal" data-target="#modal-measures"> <img
					src="img/help.png" width="17px" height="17px" />
				</a>
				<!-- Modal -->
				<div id="modal-measures" class="modal fade" role="dialog">
					<div class="modal-dialog">
						<!-- Modal content-->
						<div class="modal-content">
							<div class="modal-header">
								<button type="button" class="close" data-dismiss="modal">&times;</button>
								<h4 class="modal-title">Similarity measures</h4>
							</div>
							<div class="modal-body">
								<p>The code of both code units is first pre-processed in
									different steps, and for each step a similarity measure is
									obtained representing the number of similar LOC. Each step
									includes additional heuristics, so the results may be better
									but also less reliable. Next to the similarity measure in each
									step, we show the increase in similar LOC according to the
									previous step. A great increase after some specific step could
									be meaningful, but the interpretation of these results is left
									to the user.</p>
								<p>After these pre-processing steps, additional measures are
									taken to evaluate other similarity aspects, such as the
									comparison between the code units' dependency structures.</p>
								<p>Finally, a total similarity measure is calculated taking
									into account the importance and accuracy of each individual
									measure.</p>
							</div>
							<div class="modal-footer">
								<button type="button" class="btn btn-default"
									data-dismiss="modal">Close</button>
							</div>
						</div>
					</div>
				</div>
			<table id="results-table">
				<caption>Similarity results between the code units</caption>
				<tbody>
					<tr>
						<th>Code units</th>
						<td>${result.codeUnitA.name} / ${result.codeUnitB.name}</td>
					</tr>
					<tr>
						<th>Model classes</th>
						<td><#if modelClassA??>
								${modelClassA.elementName}
							<#else>
								Not specified
							</#if>
								 / 
						 	<#if modelClassB??>
						 		${modelClassB.elementName}
						 	<#else>
						 		Not specified
						 	</#if>
						 </td>
					</tr>
											<tr>
							<th>Step 0. Original code units <a data-toggle="modal"
								data-target="#modal-Step0"> <img src="img/help.png"
									width="17px" height="17px" />
							</a> <!-- Modal -->
								<div id="modal-Step0" class="modal fade" role="dialog">
									<div class="modal-dialog">
										<!-- Modal content-->
										<div class="modal-content">
											<div class="modal-header">
												<button type="button" class="close" data-dismiss="modal">&times;</button>
												<h4 class="modal-title">Step 0. Original code units
												</h4>
											</div>
											<div class="modal-body">
												<p>In this step, the two code units are compared in its original form, without any pre-processing action being taken</p>
											</div>
											<div class="modal-footer">
												<button type="button" class="btn btn-default"
													data-dismiss="modal">Close</button>
											</div>
										</div>
									</div>
								</div></th>
							<td>
								<#if result.stepResults?api.get(0?int)??>
									<#assign stepResult = result.stepResults?api.get(0?int)>
									${stepResult.similarity?string["0.##"]} (${(stepResult.percentageCodeUnitA * 100)?string["0.##"]}%/${(stepResult.percentageCodeUnitB * 100)?string["0.##"]}%)									
									<#assign lastResult = stepResult>
								<#else>
									Not applied
								</#if>
							</td>
						</tr>
						<tr>
							<th>Step 1. Java basic pre-processing <a data-toggle="modal"
								data-target="#modal-Step1"> <img src="img/help.png"
									width="17px" height="17px" />
							</a> <!-- Modal -->
								<div id="modal-Step1" class="modal fade" role="dialog">
									<div class="modal-dialog">
										<!-- Modal content-->
										<div class="modal-content">
											<div class="modal-header">
												<button type="button" class="close" data-dismiss="modal">&times;</button>
												<h4 class="modal-title">Step 1. Java basic
													pre-processing</h4>
											</div>
											<div class="modal-body">
												<p>This step includes the following actions:</p>
												<ul>
													<li>Remove comments, blank lines and spaces and import
														statements.</li>
													<li>Remove Java modifiers like the visibility related ones.</li>
													<li>Replace all the variable, parameter and attribute
														names with an identifier.</li>
													<li>Remove attribute declarations and simple get and
														set methods</li>
													<li>Remove method parameters</li>
												</ul>
											</div>
											<div class="modal-footer">
												<button type="button" class="btn btn-default"
													data-dismiss="modal">Close</button>
											</div>
										</div>
									</div>
								</div></th>
							<td>
								<#if result.stepResults?api.get(1?int)??>
									<#assign stepResult = result.stepResults?api.get(1?int)>
									${stepResult.similarity?string["0.##"]} (${(stepResult.percentageCodeUnitA * 100)?string["0.##"]}%/${(stepResult.percentageCodeUnitB * 100)?string["0.##"]}%)									
									
									<#-- Calculate and show the relative measures -->
									<#if lastResult??>
										<#if lastResult.similarity = 0>
											<#assign incSimilarity = 0>
										<#else>
											<#assign incSimilarity = stepResult.similarity/lastResult.similarity>
										</#if>
										<#if lastResult.percentageCodeUnitA = 0>
											<#assign incPercentageA = 0>
										<#else>
											<#assign incPercentageA = stepResult.percentageCodeUnitA/lastResult.percentageCodeUnitA>
										</#if>
										<#if lastResult.percentageCodeUnitB = 0>
											<#assign incPercentageB = 0>
										<#else>
											<#assign incPercentageB = stepResult.percentageCodeUnitB/lastResult.percentageCodeUnitB>
										</#if>
										 [${incSimilarity?string["0.##"]} (${incPercentageA?string["0.##"]}/${incPercentageB?string["0.##"]})]
									</#if>

									<#assign lastResult = stepResult>
								<#else>
									Not applied
								</#if>
							</td>
						</tr>
						<tr>
							<th>Step 2. Generation-oriented basic pre-processing <a
								data-toggle="modal" data-target="#modal-Step2"> <img
									src="img/help.png" width="17px" height="17px" />
							</a> <!-- Modal -->
								<div id="modal-Step2" class="modal fade" role="dialog">
									<div class="modal-dialog">
										<!-- Modal content-->
										<div class="modal-content">
											<div class="modal-header">
												<button type="button" class="close" data-dismiss="modal">&times;</button>
												<h4 class="modal-title">Step 2. Generation-oriented
													basic pre-processing</h4>
											</div>
											<div class="modal-body">
												<p>This step includes the following actions:</p>
												<ul>
													<li>Replace every occurrence of the model class name
														in the code with an identifier.</li>
													<li>Replace every occurrence of the model class
														attributes names in the code with an identifier.</li>
												</ul>
											</div>
											<div class="modal-footer">
												<button type="button" class="btn btn-default"
													data-dismiss="modal">Close</button>
											</div>
										</div>
									</div>
								</div></th>
							<td>
								<#if result.stepResults?api.get(2?int)??>
									<#assign stepResult = result.stepResults?api.get(2?int)>
									${stepResult.similarity?string["0.##"]} (${(stepResult.percentageCodeUnitA * 100)?string["0.##"]}%/${(stepResult.percentageCodeUnitB * 100)?string["0.##"]}%)									
									
									<#-- Calculate and show the relative measures -->
									<#if lastResult??>
										<#if lastResult.similarity = 0>
											<#assign incSimilarity = 0>
										<#else>
											<#assign incSimilarity = stepResult.similarity/lastResult.similarity>
										</#if>
										<#if lastResult.percentageCodeUnitA = 0>
											<#assign incPercentageA = 0>
										<#else>
											<#assign incPercentageA = stepResult.percentageCodeUnitA/lastResult.percentageCodeUnitA>
										</#if>
										<#if lastResult.percentageCodeUnitB = 0>
											<#assign incPercentageB = 0>
										<#else>
											<#assign incPercentageB = stepResult.percentageCodeUnitB/lastResult.percentageCodeUnitB>
										</#if>
										 [${incSimilarity?string["0.##"]} (${incPercentageA?string["0.##"]}/${incPercentageB?string["0.##"]})]
									</#if>

									<#assign lastResult = stepResult>
								<#else>
									Not applied
								</#if>
							</td>
						</tr>
						<tr>
							<th>Step 3. Generation-oriented advanced pre-processing <a
								data-toggle="modal" data-target="#modal-Step4"> <img
									src="img/help.png" width="17px" height="17px" />
							</a> <!-- Modal -->
								<div id="modal-Step4" class="modal fade" role="dialog">
									<div class="modal-dialog">
										<!-- Modal content-->
										<div class="modal-content">
											<div class="modal-header">
												<button type="button" class="close" data-dismiss="modal">&times;</button>
												<h4 class="modal-title">Step 3. Generation-oriented
													advanced pre-processing</h4>
											</div>
											<div class="modal-body">
												<p>This step includes the following actions:</p>
												<ul>
													<li>Replace all the method calls on objects of the
														model class with an identifier.</li>
													<li>Remove duplicated methods and LOC.</li>
												</ul>
											</div>
											<div class="modal-footer">
												<button type="button" class="btn btn-default"
													data-dismiss="modal">Close</button>
											</div>
										</div>
									</div>
								</div></th>							
							<td>
								<#if result.stepResults?api.get(3?int)??>
									<#assign stepResult = result.stepResults?api.get(3?int)>
									${stepResult.similarity?string["0.##"]} (${(stepResult.percentageCodeUnitA * 100)?string["0.##"]}%/${(stepResult.percentageCodeUnitB * 100)?string["0.##"]}%)									
									
									<#-- Calculate and show the relative measures -->
									<#if lastResult??>
										<#if lastResult.similarity = 0>
											<#assign incSimilarity = 0>
										<#else>
											<#assign incSimilarity = stepResult.similarity/lastResult.similarity>
										</#if>
										<#if lastResult.percentageCodeUnitA = 0>
											<#assign incPercentageA = 0>
										<#else>
											<#assign incPercentageA = stepResult.percentageCodeUnitA/lastResult.percentageCodeUnitA>
										</#if>
										<#if lastResult.percentageCodeUnitB = 0>
											<#assign incPercentageB = 0>
										<#else>
											<#assign incPercentageB = stepResult.percentageCodeUnitB/lastResult.percentageCodeUnitB>
										</#if>
										 [${incSimilarity?string["0.##"]} (${incPercentageA?string["0.##"]}/${incPercentageB?string["0.##"]})]
									</#if>

									<#assign lastResult = stepResult>
								<#else>
									Not applied
								</#if>
							</td>
						</tr>
						<tr>
							<th>Step 4. Java advanced pre-processing <a
								data-toggle="modal" data-target="#modal-Step3"> <img
									src="img/help.png" width="17px" height="17px" />
							</a> <!-- Modal -->
								<div id="modal-Step3" class="modal fade" role="dialog">
									<div class="modal-dialog">
										<!-- Modal content-->
										<div class="modal-content">
											<div class="modal-header">
												<button type="button" class="close" data-dismiss="modal">&times;</button>
												<h4 class="modal-title">Step 4. Java advanced
													pre-processing</h4>
											</div>
											<div class="modal-body">
												<p>This step includes the following actions:</p>
												<ul>
													<li>Replace the variables types with an identifier.</li>
													<li>Replace variables initializations with an
														identifier.</li>
													<li>Replace method invocations with an identifier.</li>
												</ul>
											</div>
											<div class="modal-footer">
												<button type="button" class="btn btn-default"
													data-dismiss="modal">Close</button>
											</div>
										</div>
									</div>
								</div></th>
							<td>
								<#if result.stepResults?api.get(4?int)??>
									<#assign stepResult = result.stepResults?api.get(4?int)>
									${stepResult.similarity?string["0.##"]} (${(stepResult.percentageCodeUnitA * 100)?string["0.##"]}%/${(stepResult.percentageCodeUnitB * 100)?string["0.##"]}%)									
									
									<#-- Calculate and show the relative measures -->
									<#if lastResult??>
										<#if lastResult.similarity = 0>
											<#assign incSimilarity = 0>
										<#else>
											<#assign incSimilarity = stepResult.similarity/lastResult.similarity>
										</#if>
										<#if lastResult.percentageCodeUnitA = 0>
											<#assign incPercentageA = 0>
										<#else>
											<#assign incPercentageA = stepResult.percentageCodeUnitA/lastResult.percentageCodeUnitA>
										</#if>
										<#if lastResult.percentageCodeUnitB = 0>
											<#assign incPercentageB = 0>
										<#else>
											<#assign incPercentageB = stepResult.percentageCodeUnitB/lastResult.percentageCodeUnitB>
										</#if>
										 [${incSimilarity?string["0.##"]} (${incPercentageA?string["0.##"]}/${incPercentageB?string["0.##"]})]
									</#if>

									<#assign lastResult = stepResult>
								<#else>
									Not applied
								</#if>
							</td>
						</tr>
						<tr>
							<th>Javadoc simmilarity <a data-toggle="modal"
								data-target="#modal-ExtraStep"> <img src="img/help.png"
									width="17px" height="17px" />
							</a> <!-- Modal -->
								<div id="modal-ExtraStep" class="modal fade" role="dialog">
									<div class="modal-dialog">
										<!-- Modal content-->
										<div class="modal-content">
											<div class="modal-header">
												<button type="button" class="close" data-dismiss="modal">&times;</button>
												<h4 class="modal-title">Javadoc simmilarity</h4>
											</div>
											<div class="modal-body">
												<p>In this step, the Javadoc comments are compared. The
													natural language description of the class and its methods
													also contains valuable information, and sometimes including
													this in the comparison can be useful as an extra factor to
													determine the similarity between the two classes.</p>
											</div>
											<div class="modal-footer">
												<button type="button" class="btn btn-default"
													data-dismiss="modal">Close</button>
											</div>
										</div>
									</div>
								</div></th>
							<td>Not available</td>
						</tr>
						<tr>
							<th>Dependency structure similarity <a data-toggle="modal"
								data-target="#modal-dependency"> <img src="img/help.png"
									width="17px" height="17px" />
							</a> <!-- Modal -->
								<div id="modal-dependency" class="modal fade" role="dialog">
									<div class="modal-dialog">
										<!-- Modal content-->
										<div class="modal-content">
											<div class="modal-header">
												<button type="button" class="close" data-dismiss="modal">&times;</button>
												<h4 class="modal-title">Dependency structure similarity</h4>
											</div>
											<div class="modal-body">
												<p>The relationship between a class and the other
													classes in the project is also an important factor to take
													into account when calculating the similarity between
													classes.</p>
												<p>In this step, we take each of the two classes and
													their interfaces, super classes and/or sub classes and
													compare the two structures, to check if they share some
													elements or if at least their composition is similar.</p>
											</div>
											<div class="modal-footer">
												<button type="button" class="btn btn-default"
													data-dismiss="modal">Close</button>
											</div>
										</div>
									</div>
								</div></th>
							<td>Not available</td>
						</tr>
						<tr>
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
								</div></th>
							<td>
								<#assign finalResult = result.finalResult>
								${finalResult.similarity?string["0.##"]} (${(finalResult.percentageCodeUnitA * 100)?string["0.##"]}%/${(finalResult.percentageCodeUnitB * 100)?string["0.##"]}%)
							</td>
						</tr>
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