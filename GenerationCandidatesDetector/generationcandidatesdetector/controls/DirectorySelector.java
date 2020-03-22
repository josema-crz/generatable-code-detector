package generationcandidatesdetector.controls;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

/**
 * Composite with a text field and a button to select a directory.
 */
public class DirectorySelector extends Composite {
	/** Path of the selected directory. */
	private String dirPath;
	/** Text field. */
	private Text dirText;
	/** Browse button. */
	private Button button;

	public DirectorySelector(Composite parent) {
		super(parent, SWT.NONE);

		GridLayout layout = new GridLayout();
		setLayout(layout);
		layout.numColumns = 2;

		dirText = new Text(this, SWT.BORDER);
		dirText.setEditable(false);
		dirText.setLayoutData(new GridData(140, 16));
		dirText.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));

		button = new Button(this, SWT.PUSH);
		button.setText("Browse...");
		button.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				DirectoryDialog directoryDialog = new DirectoryDialog(getShell());

				directoryDialog.setFilterPath(dirText.getText());
				directoryDialog.setMessage("Please select a directory");

				String dir = directoryDialog.open();
				if (dir != null) {
					// Update this attribute first.
					dirPath = dir;
					// Update the GUI Text later, which will be detected by the
					// wizard page.
					dirText.setText(dir);
				}
			}
		});
	}

	public Text getDirText() {
		return dirText;
	}

	public Button getButton() {
		return button;
	}

	public String getDirPath() {
		return dirPath;
	}
}
