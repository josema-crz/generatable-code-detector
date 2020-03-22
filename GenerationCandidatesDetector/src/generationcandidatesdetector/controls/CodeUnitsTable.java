package generationcandidatesdetector.controls;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.IType;
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
import similaritycalculation.CodeUnit;

/**
 * Composite with a table and buttons to manage multiple code units and their
 * optional model classes.
 */
public class CodeUnitsTable extends Composite {
	/** Code units in the table. */
	private Map<CodeUnit, IType> codeUnits;
	/** Buttons to manage the code units. */
	private Button setModelButton, removeButton;

	/**
	 * Creates an empty table.
	 * 
	 * @param parent
	 *            The parent composite.
	 */
	public CodeUnitsTable(Composite parent) {
		this(parent, new LinkedList<CodeUnit>());
	}

	/**
	 * Creates a table initialized with some code units.
	 * 
	 * @param parent
	 *            The parent composite.
	 * @param codeUnits
	 *            The initial list of code units.
	 */
	public CodeUnitsTable(Composite parent, List<CodeUnit> initialCodeUnits) {
		super(parent, SWT.NONE);

		this.codeUnits = new HashMap<CodeUnit, IType>();

		GridLayout layout = new GridLayout();
		setLayout(layout);
		layout.numColumns = 2;

		Table table = new Table(this, SWT.SINGLE | SWT.BORDER | SWT.FULL_SELECTION);
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.heightHint = 200;
		table.setLayoutData(data);
		String[] titles = { "Code unit", "Model class" };
		for (int i = 0; i < titles.length; i++) {
			TableColumn column = new TableColumn(table, SWT.NONE);
			column.setText(titles[i]);
		}
		table.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				// Enable the buttons.
				setModelButton.setEnabled(true);
				removeButton.setEnabled(true);
			}
		});
		
		// We initialize the table and the code unit map.
		for (CodeUnit codeUnit : initialCodeUnits) {
			codeUnits.put(codeUnit, null);
			
			TableItem item = new TableItem(table, 0);
			item.setData(codeUnit);
			item.setText(0, codeUnit.getName());
		}		
		// Recalculate columns width
		for (int i = 0; i < titles.length; i++) {
			table.getColumn(i).pack();
		}

		Composite buttonsComposite = new Composite(this, SWT.SHADOW_NONE);
		buttonsComposite.setLayout(new FillLayout(SWT.VERTICAL));
		buttonsComposite.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, false, false));

		Button addButton = new Button(buttonsComposite, SWT.PUSH);
		addButton.setText("Add new code units");
		addButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				// Get the code units.
				List<CodeUnit> selectedCodeUnits = DialogHelpers.getCodeUnitsDialog(getShell());

				for (CodeUnit codeUnit : selectedCodeUnits) {
					// Update the code unit map.
					if (!codeUnits.containsKey(codeUnit)) {
						codeUnits.put(codeUnit, null);

						// Update the table with the new code unit.
						TableItem item = new TableItem(table, 0);
						item.setData(codeUnit);
						item.setText(0, codeUnit.getName());

						// Recalculate columns width
						for (int i = 0; i < titles.length; i++) {
							table.getColumn(i).pack();
						}
					}
				}
			}
		});

		setModelButton = new Button(buttonsComposite, SWT.PUSH);
		setModelButton.setText("Set model class");
		setModelButton.setEnabled(false); // Initially disabled.
		setModelButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				if (table.getSelectionCount() > 0) {
					// Get the model class.
					IType modelClass = DialogHelpers.getTypeDialog(getShell());

					// Update the table.
					table.getSelection()[0].setText(1, modelClass.getElementName());

					// Update the code unit map.
					CodeUnit codeUnit = (CodeUnit) table.getSelection()[0].getData();
					codeUnits.put(codeUnit, modelClass);

					// Recalculate columns width
					for (int i = 0; i < titles.length; i++) {
						table.getColumn(i).pack();
					}
				}
			}
		});

		removeButton = new Button(buttonsComposite, SWT.PUSH);
		removeButton.setText("Remove code unit");
		removeButton.setEnabled(false); // Initially disabled.
		removeButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				if (table.getSelectionCount() > 0) {
					// Update the code unit map.
					codeUnits.remove((CodeUnit) table.getSelection()[0].getData());

					// Remove the selected item in the table.
					table.remove(table.getSelectionIndex());

					// Recalculate columns width
					for (int i = 0; i < titles.length; i++) {
						table.getColumn(i).pack();
					}
				}
			}
		});
	}

	public Map<CodeUnit, IType> getCodeUnits() {
		return codeUnits;
	}
}
