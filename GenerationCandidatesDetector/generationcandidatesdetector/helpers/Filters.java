package generationcandidatesdetector.helpers;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;

public class Filters {
	public static List<IFile> removeNonGenerated(List<IFile> files) {
		List<IFile> filteredFiles = new LinkedList<IFile>();
		
		for (IFile file : files) {
			if (file.getFullPath().toString().contains("src-gen/")
					|| file.getFullPath().toString().contains("cfg-gen/")) {
				filteredFiles.add(file);
			}
		}
		
		return filteredFiles;
	}
	
	public static boolean isGeneratedFile(String fullFileName) {
		return fullFileName.contains("src-gen/")
				|| fullFileName.contains("cfg-gen/");
	}
	
	public static Map<String, IFile> doGeneratedArtifactsNameFix(Map<String, IFile> files) {
		// Make a copy of the map
		Map<String, IFile> filteredFiles = new HashMap<String, IFile>(files);
		
		for (String name : files.keySet()) {
			if (name.endsWith("BaseImpl")) {
				String baseInterface = name.substring(0, name.indexOf("Impl"));
				String manualInterface = baseInterface.substring(0, name.indexOf("Base"));
				
				// 1. Remove the base interface
				filteredFiles.remove(baseInterface);
				
				// 2. Associate the manual interface's name with the base implementation
				filteredFiles.put(manualInterface, filteredFiles.get(name));
				
				// 3. Remove the old entry for the base implementation
				filteredFiles.remove(name);
			}
		}		
		return filteredFiles;
	}
}
