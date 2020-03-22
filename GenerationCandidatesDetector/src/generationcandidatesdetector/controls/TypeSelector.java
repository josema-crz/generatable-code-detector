package generationcandidatesdetector.controls;

import org.eclipse.jdt.core.IType;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

import generationcandidatesdetector.helpers.DialogHelpers;

/**
 * Composite with a text field and a browse button to select a Type.
 */
public class TypeSelector extends Composite {
	/** Selected type. */
	private IType type;
	/** Text field. */
	private Text typeText;
	/** Browse button. */
	private Button button;

	public TypeSelector(Composite parent) {
		super(parent, SWT.NONE);

		GridLayout layout = new GridLayout();
		setLayout(layout);
		layout.numColumns = 2;

		typeText = new Text(this, SWT.BORDER);
		typeText.setEditable(false);
		typeText.setLayoutData(new GridData(140, 16));
		typeText.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));

		button = new Button(this, SWT.PUSH);
		button.setText("Browse...");
		button.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				type = DialogHelpers.getTypeDialog(getShell());

				// Update the text with the name of the selected type. The
				// wizard page will detect this change.
				if (type != null) {
					typeText.setText(type.getElementName());
				}
			}
		});
	}

	public IType getType() {
		return type;
	}

	public Text getTypeText() {
		return typeText;
	}

	public Button getButton() {
		return button;
	}
}
