package generationcandidatesdetector.controls;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import generationcandidatesdetector.helpers.DialogHelpers;
import utils.FileUtils;

/**
 * Composite with a table and buttons to manage the selection of files.
 */
public class FilesTable extends Composite {
	/**
	 * Files in the table. They are indexed by their simple name, so files with
	 * duplicate names are not supported.
	 */
	private Map<String, IFile> files;
	/** Buttons to manage the files. */
	private Button removeButton;

	/**
	 * Creates an empty table.
	 * 
	 * @param parent
	 *            The parent composite.
	 */
	public FilesTable(Composite parent) {
		this(parent, new LinkedList<IFile>());
	}

	/**
	 * Creates a table initialized with some files.
	 * 
	 * @param parent
	 *            The parent composite.
	 * @param files
	 *            The initial list of files.
	 */
	public FilesTable(Composite parent, List<IFile> initialFiles) {
		super(parent, SWT.NONE);

		this.files = new HashMap<String, IFile>();

		GridLayout layout = new GridLayout();
		setLayout(layout);
		layout.numColumns = 2;

		Table table = new Table(this, SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION);
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.heightHint = 200;
		table.setLayoutData(data);
		String[] titles = { "File's simple name" };
		for (int i = 0; i < titles.length; i++) {
			TableColumn column = new TableColumn(table, SWT.NONE);
			column.setText(titles[i]);
		}
		table.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				// Enable the buttons.
				removeButton.setEnabled(true);
			}
		});

		// We initialize the table and the files map.
		for (IFile file : initialFiles) {
			String simpleName = FileUtils.getSimpleName(file);
			// Files with empty names are not handled.
			// Files with duplicate names are not handled.
			if (simpleName.length() > 0 && !files.containsKey(simpleName)) {
				files.put(file.getFullPath().toString(), file);

				TableItem item = new TableItem(table, 0);
				item.setData(file);
				item.setText(0, simpleName);
			}
		}
		// Recalculate columns width
		for (int i = 0; i < titles.length; i++) {
			table.getColumn(i).pack();
		}

		Composite buttonsComposite = new Composite(this, SWT.SHADOW_NONE);
		buttonsComposite.setLayout(new FillLayout(SWT.VERTICAL));
		buttonsComposite.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, false, false));

		Button addButton = new Button(buttonsComposite, SWT.PUSH);
		addButton.setText("Add new files");
		addButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				// Get the files.
				List<IFile> selectedFiles = DialogHelpers.getFilesDialog(getShell());

				for (IFile file : selectedFiles) {
					String simpleName = FileUtils.getSimpleName(file);
					// Files with empty names are not handled.
					// Files with duplicate names are not handled.
					if (simpleName.length() > 0 && !files.containsKey(simpleName)) {
						files.put(simpleName, file);

						// Update the table with the new file.
						TableItem item = new TableItem(table, 0);
						item.setData(file);
						item.setText(0, simpleName);

						// Recalculate columns width
						for (int i = 0; i < titles.length; i++) {
							table.getColumn(i).pack();
						}
					}
				}
			}
		});

		removeButton = new Button(buttonsComposite, SWT.PUSH);
		removeButton.setText("Remove files");
		removeButton.setEnabled(false); // Initially disabled.
		removeButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				// Update the files map.
				for (int selIndex : table.getSelectionIndices()) {					
					files.remove(table.getItem(selIndex).getText());
				}
				// Update the table.
				table.remove(table.getSelectionIndices());
				// Recalculate columns width
				for (int i = 0; i < titles.length; i++) {
					table.getColumn(i).pack();
				}
			}
		});
	}

	public Map<String, IFile> getFiles() {
		return files;
	}
}
