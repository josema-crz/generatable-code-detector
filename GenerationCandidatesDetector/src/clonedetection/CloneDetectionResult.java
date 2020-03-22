package clonedetection;

import java.util.LinkedList;
import java.util.List;

/**
 * Represents the global result of the clone detection process. Includes the
 * sets of clones found as well as a Summary object with general information.
 */
public class CloneDetectionResult {
	/** Sets of clones found. */
	private List<CloneSet> sets;

	/**
	 * Contains some basic general information about the result of the clone
	 * detection process.
	 */
	public static class Summary {
		/** Different measures on the files and clones. */
		private int totalFileCount, totalLOCCount, totalCloneFileCount, totalCloneLOCCount;

		/**
		 * Initializes a Summary.
		 */
		public Summary() {
			// The values are optional.
			// They are all initialized to -1 by default
			totalFileCount = -1;
			totalLOCCount = -1;
			totalCloneFileCount = -1;
			totalCloneLOCCount = -1;
		}

		public int getTotalFileCount() {
			return totalFileCount;
		}

		public void setTotalFileCount(int totalFileCount) {
			this.totalFileCount = totalFileCount;
		}

		public int getTotalLOCCount() {
			return totalLOCCount;
		}

		public void setTotalLOCCount(int totalLOCCount) {
			this.totalLOCCount = totalLOCCount;
		}

		public int getTotalCloneFileCount() {
			return totalCloneFileCount;
		}

		public void setTotalCloneFileCount(int totalCloneFileCount) {
			this.totalCloneFileCount = totalCloneFileCount;
		}

		public int getTotalCloneLOCCount() {
			return totalCloneLOCCount;
		}

		public void setTotalCloneLOCCount(int totalCloneLOCCount) {
			this.totalCloneLOCCount = totalCloneLOCCount;
		}
	}

	/** Global information about the results. */
	private Summary summary;

	/**
	 * Initializes a new empty CloneDetectionResult.
	 */
	public CloneDetectionResult() {
		this.sets = new LinkedList<CloneSet>();
		this.summary = new Summary();
	}

	public List<CloneSet> getSets() {
		return sets;
	}

	public void setSets(List<CloneSet> sets) {
		this.sets = sets;
	}
	
	public void addSet(CloneSet set) {
		sets.add(set);
	}

	public Summary getSummary() {
		return summary;
	}

	public void setSummary(Summary summary) {
		this.summary = summary;
	}
}
