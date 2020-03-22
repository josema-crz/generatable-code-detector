package generationcandidatesdetector.controls;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

/**
 * Group containing the widgets to ask the user for the output-related information.
 */
public class OutputGroup extends Composite {
	/** Text field. */
	private Label outputLabel;
	/** Browse button. */
	private DirectorySelector outputSelec;

	public OutputGroup(Composite parent) {
		super(parent, SWT.NONE);
		
		setLayout(new GridLayout());
		Group outputGroup = new Group(this, SWT.SHADOW_NONE);
		outputGroup.setText("Output");
		GridLayout outputLayout = new GridLayout();
		outputLayout.numColumns = 2;
		outputGroup.setLayout(outputLayout);
		outputGroup.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));

		outputLabel = new Label(outputGroup, SWT.NONE);
		outputLabel.setText("Result location*:");
		outputSelec = new DirectorySelector(outputGroup);
	}

	public Label getOutputLabel() {
		return outputLabel;
	}

	public DirectorySelector getOutputSelec() {
		return outputSelec;
	}
}
