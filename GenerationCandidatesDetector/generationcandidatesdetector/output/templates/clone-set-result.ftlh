<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Clone detection result - Clone set</title>
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

	<section id="cloneset">
		<header>
			<h1>Clone set</h1>
		</header>
		<article id="summary">
			<table id="summary-table">
				<caption>Summary of the clone set</caption>
				<tbody>
					<tr>
						<th>Number of blocks</th>
						<th>Line count</th>
						<th>Similarity</th>
					</tr>
					<tr>
						<td>${set.size}</td>
						<td><#if set.lineCount = -1>-<#else>${set.lineCount}</#if></td>
						<td><#if set.similarity = -1>-<#else>${set.similarity}</#if></td>
					</tr>
				</tbody>
			</table>
		</article>
		<article id="blocks">
			<h2>Clone blocks</h2>
			<table id="blocks-table">
				<caption>Clone blocks</caption>
				<tbody>
					<tr>
						<th>File name</th>
						<th>Start line number</th>
						<th>End line number</th>
					</tr>
					<#list set.blocks as block>
					<tr>
						<td>${block.fileName}</td>
						<td>${block.startLineNumber}</td>
						<td>${block.endLineNumber}</td>
					</tr>
					</#list>					
				</tbody>
			</table>
		</article>
		<#if set.duplicatedCode??>
		<article id="code">			
			<b>Duplicated code:</b>
<pre><code>${set.duplicatedCode}</code></pre>
		</article>
		</#if>
	</section>
	<footer>
		<p>Generation candidates detector. 2016.</p>
		<p>Jose Manuel Cruz Zapata</p>
	</footer>
</body>
</html>