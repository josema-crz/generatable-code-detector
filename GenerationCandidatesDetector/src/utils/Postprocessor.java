package utils;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;

import clonedetection.CloneBlock;
import clonedetection.CloneDetectionResult;
import clonedetection.CloneSet;
import namesimilarity.NameSimilarityResultGroup;
import namesimilarity.NameSimilarityResultName;
import similaritycalculation.GlobalSimilarityCalculationResult;
import similaritycalculation.MultipleGlobalSimilarityCalculationResult;

/**
 * Contains methods to process the results of clone detection and name
 * similarity/custom clone detection. Collects and writes into files all the
 * necessary information.
 *
 */
public class Postprocessor {
	private static final boolean activated = true;

	public static boolean isActivated() {
		return activated;
	}

	public static void postProcess(Map<NameSimilarityResultGroup, MultipleGlobalSimilarityCalculationResult> results,
			Map<String, IFile> files) {
		if (!activated)
			return;

		int genGroups = 0;
		int nogenGroups = 0;
		int groupCandidates = 0;
		Set<String> genArtifacts = new HashSet<String>();
		Set<String> nogenArtifacts = new HashSet<String>();
		Set<String> genArtifactsSim = new HashSet<String>();
		Set<String> nogenArtifactsSim = new HashSet<String>();

		PrintWriter totalsWriter, nogenGroupsSizes, nogenGroupsSims, nogenArts, genArts, nogenArtsSim, genArtsSim;
		try {
			totalsWriter = new PrintWriter(
					"C:\\Users\\jza\\workspace-tfm\\maZapataCodeGen\\10.Notizen\\Case-study\\Name-and-code-similarity\\__totals.txt",
					"UTF-8");
			nogenGroupsSims = new PrintWriter(
					"C:\\Users\\jza\\workspace-tfm\\maZapataCodeGen\\10.Notizen\\Case-study\\Name-and-code-similarity\\__nogenGroupsSims.txt",
					"UTF-8");
			nogenGroupsSizes = new PrintWriter(
					"C:\\Users\\jza\\workspace-tfm\\maZapataCodeGen\\10.Notizen\\Case-study\\Name-and-code-similarity\\__nogenGroupsSizes.txt",
					"UTF-8");
			nogenArts = new PrintWriter(
					"C:\\Users\\jza\\workspace-tfm\\maZapataCodeGen\\10.Notizen\\Case-study\\Name-and-code-similarity\\__nogenarts.txt",
					"UTF-8");
			genArts = new PrintWriter(
					"C:\\Users\\jza\\workspace-tfm\\maZapataCodeGen\\10.Notizen\\Case-study\\Name-and-code-similarity\\__genarts.txt",
					"UTF-8");
			nogenArtsSim = new PrintWriter(
					"C:\\Users\\jza\\workspace-tfm\\maZapataCodeGen\\10.Notizen\\Case-study\\Name-and-code-similarity\\__nogenartssim.txt",
					"UTF-8");
			genArtsSim = new PrintWriter(
					"C:\\Users\\jza\\workspace-tfm\\maZapataCodeGen\\10.Notizen\\Case-study\\Name-and-code-similarity\\__genartssim.txt",
					"UTF-8");

			for (NameSimilarityResultGroup group : results.keySet()) {
				boolean genGroup = false;
				boolean nogenGroup = false;
				int nogenNames = 0;

				for (NameSimilarityResultName name : group.getResultNames()) {
					if (isGeneratedFile(files.get(name.getOriginalName()).getFullPath().toString())) {
						genGroup = true;
						genArtifacts.add(name.getOriginalName());
					} else {
						nogenGroup = true;
						nogenArtifacts.add(name.getOriginalName());
						nogenNames++;
					}
				}
				nogenGroupsSizes.println(nogenNames);

				if (genGroup) {
					genGroups++;
				}
				if (nogenGroup) {
					nogenGroups++;
				}

				if (results.get(group) != null) {
					for (GlobalSimilarityCalculationResult simResult : results.get(group).getResults()) {
						if (simResult.getFinalResult().getSimilarity() > 30
								|| simResult.getFinalResult().getPercentageCodeUnitA() > 30
								|| simResult.getFinalResult().getPercentageCodeUnitB() > 30) {

							if (isGeneratedFile(simResult.getCodeUnitA().getId())) {
								genArtifactsSim.add(simResult.getCodeUnitA().getName());

							} else {
								nogenArtifactsSim.add(simResult.getCodeUnitA().getName());
							}

							if (isGeneratedFile(simResult.getCodeUnitB().getId())) {
								genArtifactsSim.add(simResult.getCodeUnitB().getName());

							} else {
								nogenArtifactsSim.add(simResult.getCodeUnitB().getName());
							}
						}
					}

					if (nogenGroup) {
						nogenGroupsSims.println(results.get(group).getTotalSimilarity());

						if (results.get(group).getTotalSimilarity() > 30) {
							groupCandidates++;
						}
					}
				}
			}

			totalsWriter.println("Number of groups with one or more non-generated artifact: " + nogenGroups);
			totalsWriter.println("Number of groups with one or more generated artifact: " + genGroups);
			totalsWriter.println("Total different non-generated artifacts in groups: " + nogenArtifacts.size());
			totalsWriter.println("Total different generated artifacts in groups: " + genArtifacts.size());
			totalsWriter.println();
			totalsWriter.println("Total number of groups with total similarity above threshold: " + groupCandidates);
			totalsWriter.println();
			totalsWriter
					.println("Total number of generated artifacts identified as candidates: " + genArtifactsSim.size());
			totalsWriter.println(
					"Total number of non-generated artifacts identified as candidates: " + nogenArtifactsSim.size());

			int totalGenFiles = 0;
			int totalNogenFiles = 0;
			for (IFile file : files.values()) {
				if (isGeneratedFile(file.getFullPath().toString())) {
					totalGenFiles++;
				} else {
					totalNogenFiles++;
				}
			}
			totalsWriter.println();
			totalsWriter.println("Total number of generated artifacts in the projects: " + totalGenFiles);
			totalsWriter.println("Total number of non-generated artifacts in the projects: " + totalNogenFiles);

			for (String artifact : nogenArtifacts) {
				nogenArts.println(artifact);
			}

			for (String artifact : genArtifacts) {
				genArts.println(artifact);
			}

			for (String artifact : nogenArtifactsSim) {
				nogenArtsSim.println(artifact);
			}

			for (String artifact : genArtifactsSim) {
				genArtsSim.println(artifact);
			}

			totalsWriter.close();
			nogenGroupsSims.close();
			nogenArts.close();
			genArts.close();
			nogenArtsSim.close();
			genArtsSim.close();
			nogenGroupsSizes.close();

		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	public static void postProcess(CloneDetectionResult result) {
		if (!activated)
			return;

		int genSets = 0;
		int nogenSets = 0;
		int genLOC = 0;
		int nogenLOC = 0;
		Set<String> genArtifacts = new HashSet<String>();
		Set<String> nogenArtifacts = new HashSet<String>();

		PrintWriter totalsWriter, nogenSetSizes, nogenSetLOCs, nogenArts, genArts;
		try {
			totalsWriter = new PrintWriter(
					"C:\\Users\\jza\\workspace-tfm\\maZapataCodeGen\\10.Notizen\\Case-study\\Clone-detection\\__totals.txt",
					"UTF-8");
			nogenSetSizes = new PrintWriter(
					"C:\\Users\\jza\\workspace-tfm\\maZapataCodeGen\\10.Notizen\\Case-study\\Clone-detection\\__nogenSetsizes.txt",
					"UTF-8");
			nogenSetLOCs = new PrintWriter(
					"C:\\Users\\jza\\workspace-tfm\\maZapataCodeGen\\10.Notizen\\Case-study\\Clone-detection\\__nogenSetlocs.txt",
					"UTF-8");
			nogenArts = new PrintWriter(
					"C:\\Users\\jza\\workspace-tfm\\maZapataCodeGen\\10.Notizen\\Case-study\\Clone-detection\\__nogenarts.txt",
					"UTF-8");
			genArts = new PrintWriter(
					"C:\\Users\\jza\\workspace-tfm\\maZapataCodeGen\\10.Notizen\\Case-study\\Clone-detection\\__genarts.txt",
					"UTF-8");

			for (CloneSet set : result.getSets()) {
				boolean genSet = false;
				boolean nogenSet = false;

				for (CloneBlock block : set.getBlocks()) {
					if (isGeneratedFile(block.getFileFullName())) {
						genSet = true;
						genArtifacts.add(block.getFileName());
						genLOC += set.getLineCount();

					} else {
						nogenSet = true;
						nogenArtifacts.add(block.getFileName());
						nogenLOC += set.getLineCount();
					}
				}

				if (genSet) {
					genSets++;
				}
				if (nogenSet) {
					nogenSets++;
					nogenSetSizes.println(set.getBlocks().size());
					nogenSetLOCs.println(set.getLineCount());
				}
			}

			totalsWriter.println("Number of sets with one or more non-generated artifact: " + nogenSets);
			totalsWriter.println("Number of sets with one or more generated artifact: " + genSets);
			totalsWriter.println("Total different non-generated artifacts: " + nogenArtifacts.size());
			totalsWriter.println("Total different generated artifacts: " + genArtifacts.size());
			totalsWriter.println("LOC for non-generated artifacts: " + nogenLOC);
			totalsWriter.println("LOC for generated artifacts: " + genLOC);

			for (String artifact : nogenArtifacts) {
				nogenArts.println(artifact);
			}

			for (String artifact : genArtifacts) {
				genArts.println(artifact);
			}

			totalsWriter.close();
			nogenSetSizes.close();
			nogenSetLOCs.close();
			nogenArts.close();
			genArts.close();

		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	public static void count(Map<NameSimilarityResultGroup, MultipleGlobalSimilarityCalculationResult> results,
			Map<String, IFile> files) {
		if (!activated)
			return;

		Set<String> genArtifacts = new HashSet<String>();
		Set<String> nogenArtifacts = new HashSet<String>();

		PrintWriter writer;
		try {
			writer = new PrintWriter(
					"C:\\Users\\jza\\workspace-tfm\\maZapataCodeGen\\10.Notizen\\Case-study\\Name-similarity\\__counts.txt",
					"UTF-8");

			for (NameSimilarityResultGroup group : results.keySet()) {
				if (group.getResultNames().size() >= 50) {
					for (NameSimilarityResultName name : group.getResultNames()) {
						if (isGeneratedFile(files.get(name.getOriginalName()).getFullPath().toString())) {
							genArtifacts.add(name.getOriginalName());
						} else {
							nogenArtifacts.add(name.getOriginalName());
						}
					}
				}
			}

			writer.println("Number of generated artifacts in groups bigger than 50: " + genArtifacts.size());
			writer.println("Number of non-generated artifacts in groups bigger than 50: " + nogenArtifacts.size());
				
			writer.close();

		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	private static boolean isGeneratedFile(String fullFileName) {
		return fullFileName.contains("src-gen") || fullFileName.contains("cfg-gen");
	}
}
