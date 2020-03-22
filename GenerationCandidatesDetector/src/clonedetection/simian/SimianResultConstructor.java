package clonedetection.simian;

import com.harukizaemon.simian.AuditListener;
import com.harukizaemon.simian.Block;
import com.harukizaemon.simian.CheckSummary;
import com.harukizaemon.simian.Options;
import com.harukizaemon.simian.SourceFile;

import clonedetection.CloneBlock;
import clonedetection.CloneDetectionResult;
import clonedetection.CloneSet;
import utils.Logger;

/**
 * Implements the AuditListener interface, with listener methods that get
 * invoked during the Simian clone detection process. The implementation of
 * these methods constructs a CloneDetectionResult object with the results.
 */
public class SimianResultConstructor implements AuditListener {
	/** The result to be constructed */
	private CloneDetectionResult result;
	/** The set being processed */
	private CloneSet currentSet;
	
	private int processedFiles = 0, totalFiles;
		
	public SimianResultConstructor(int totalFiles) {
		super();
		this.totalFiles = totalFiles;
	}

	@Override
	public void block(Block block) {
		// Creates the block and adds it to the current set:
		CloneBlock b = new CloneBlock(block.getSourceFile().getFilename(), block.getStartLineNumber(),
				block.getEndLineNumber());
		currentSet.addBlock(b);
	}

	@Override
	public void endCheck(CheckSummary summary) {
		// Set the summary values
		result.getSummary().setTotalFileCount(summary.getTotalFileCount());
		result.getSummary().setTotalLOCCount(summary.getTotalRawLineCount());
		result.getSummary().setTotalCloneFileCount(summary.getDuplicateFileCount());
		result.getSummary().setTotalCloneLOCCount(summary.getDuplicateLineCount());
	}

	@Override
	public void endSet(String text) {
		// Ends building the set and adds it to the result:
		
		currentSet.setDuplicatedCode(text);
		result.addSet(currentSet);
	}

	@Override
	public void fileProcessed(SourceFile sourceFile) {
		// Do nothing
		processedFiles++;
		if (processedFiles%10 == 0) {
			Logger.log("Clone detection: file processed (" + processedFiles + "/" + totalFiles + ")");
		}
	}

	@Override
	public void startCheck(Options options) {
		// Initialize the result
		result = new CloneDetectionResult();
	}

	@Override
	public void startSet(int lineCount) {
		// Initializes the current set
		currentSet = new CloneSet();
		currentSet.setLineCount(lineCount);
	}

	public CloneDetectionResult getResult() {
		return result;
	}

}
