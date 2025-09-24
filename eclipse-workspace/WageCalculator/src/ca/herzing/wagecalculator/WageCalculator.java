package ca.herzing.wagecalculator;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
// import java.awt.event.*;
import java.util.ArrayList;

public class WageCalculator extends JFrame {
	private static final long serialVersionUID = 1L;
    private final JComboBox<String> cmbType = new JComboBox<>(new String[] {
            "Select", "Hourly", "Salary"
    });
    private final JTextField txtName   = new JTextField();
    private final JTextField txtWage   = new JTextField();   // wage OR salary depending on type
    private final JTextField txtHours  = new JTextField();   // only for hourly
    private final JTextArea  txtOutput = new JTextArea(10, 40);
    private final JLabel lblNote = new JLabel(" "); // add Small note for clarification

    private final JButton btnSubmit    = new JButton("Submit");
    private final JButton btnReport    = new JButton("Show Report");

    private final ArrayList<Employee> employees = new ArrayList<>();

    public WageCalculator() {
        super("Wage Calculator App"); // add title
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Layout
        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBorder(new EmptyBorder(12, 12, 12, 12));
        setContentPane(root);

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(6, 6, 6, 6);
        gc.fill = GridBagConstraints.HORIZONTAL;

        int r = 0;
        this.addRow(form, gc, r++, new JLabel("Employee Type:"), cmbType);
        this.addRow(form, gc, r++, new JLabel("Employee Name:"), txtName);
        this.addRow(form, gc, r++, new JLabel("Wage/Salary:"), txtWage);
        this.addRow(form, gc, r++, new JLabel("Hours Worked:"), txtHours);
        
     // NOTE row (spans 2 columns)
        gc.gridx = 0; gc.gridy = r++; 
        gc.gridwidth = 2; 
        gc.weightx = 1;
        lblNote.setForeground(Color.DARK_GRAY);
        lblNote.setFont(lblNote.getFont().deriveFont(Font.ITALIC, 12f));
        form.add(lblNote, gc);
        // reset for later rows if needed
        gc.gridwidth = 1;

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        buttons.add(btnSubmit);
        buttons.add(btnReport);

        txtOutput.setEditable(false);
        txtOutput.setLineWrap(false);
        txtOutput.setWrapStyleWord(true);
        txtOutput.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JScrollPane scroll = new JScrollPane(txtOutput);

        root.add(scroll, BorderLayout.CENTER);
        
        JPanel top = new JPanel(new BorderLayout(8, 8));
        top.add(form, BorderLayout.NORTH);
        top.add(buttons, BorderLayout.SOUTH);

        root.add(top, BorderLayout.NORTH);
        root.add(scroll, BorderLayout.CENTER);
        
        // Initial state: disable everything until a valid employee type is selected
        this.setAllInputsEnabled(false);
        btnSubmit.setEnabled(false);
        btnReport.setEnabled(false);

        // Listeners
        cmbType.addActionListener(e -> this.onTypeChanged());
        btnSubmit.addActionListener(e -> this.onSubmit());
        btnReport.addActionListener(e -> this.onReport());

        setPreferredSize(new Dimension(750, 500));
        pack();
        setLocationRelativeTo(null);
    }

    private void addRow(JPanel panel, GridBagConstraints gc, int row, JComponent left, JComponent right) {
        gc.gridx = 0; gc.gridy = row; gc.weightx = 0;
        panel.add(left, gc);
        gc.gridx = 1; gc.gridy = row; gc.weightx = 1;
        panel.add(right, gc);
    }

    private void onTypeChanged() {
        String selected = (String) cmbType.getSelectedItem();
        boolean validType = selected != null && !selected.equals("Select");

        // Enable/disable groups based on selection
        setAllInputsEnabled(validType);
        btnSubmit.setEnabled(validType);
        btnReport.setEnabled(true); // allow report anytime once app is open

        // Conditional visibility/enabled for hours vs salary
        if ("Hourly".equals(selected)) {
            txtWage.setEnabled(true);
            txtWage.setToolTipText("Enter hourly wage");
            txtHours.setEnabled(true);
            txtHours.setToolTipText("Enter hours worked");
            lblNote.setText("Note: Calculations are weekly. Gross = wage × this week's hours. Taxes at 15%.");
            
        } else if ("Salary".equals(selected)) {
            txtWage.setEnabled(true);
            txtWage.setToolTipText("Enter yearly salary");
            txtHours.setEnabled(false);
            txtHours.setText(""); // clear irrelevant field
            lblNote.setText("Note: Calculations are weekly. Weekly pay = annual salary ÷ 52. Taxes at 15%.");
        } else {
            // "Select"
            this.clearInputs();
            this.setAllInputsEnabled(false);
            btnSubmit.setEnabled(false);
        }
    }

    private void onSubmit() {
        // Validate selection
        String type = (String) cmbType.getSelectedItem();
        if (type == null || type.equals("Select")) {
            this.showError("Please choose an employee type (Hourly or Salary).");
            return;
        }

        // Validate common fields
        String name = txtName.getText().trim();
        String wageStr = txtWage.getText().trim();
        String hoursStr = txtHours.getText().trim();

        if (name.isEmpty())             { this.showError("Name cannot be empty."); return; }
        if (wageStr.isEmpty())          { this.showError((type.equals("Hourly") ? "Wage" : "Salary") + " cannot be empty."); return; }
        double wageOrSalary;
        try {
            wageOrSalary = Double.parseDouble(wageStr);
            if (wageOrSalary < 0) { this.showError("Wage/Salary must be a positive number."); return; }
        } catch (NumberFormatException ex) {
            this.showError("Wage/Salary must be a valid number.");
            return;
        }

        if (type.equals("Hourly")) {
            if (hoursStr.isEmpty()) { this.showError("Hours worked cannot be empty for Hourly employees."); return; }
            double hours;
            try {
                hours = Double.parseDouble(hoursStr);
                if (hours < 0) { this.showError("Hours worked must be a positive number."); return; }
            } catch (NumberFormatException ex) {
                this.showError("Hours worked must be a valid number.");
                return;
            }

            HourlyEmployee he = new HourlyEmployee(name, wageOrSalary, hours);
            he.generatePayroll();
            employees.add(he);
            
            txtOutput.setText(
            		this.addTitle()+he.toString());

        } else { // Salary
            SalaryEmployee se = new SalaryEmployee(name, wageOrSalary);
            se.generatePayroll();
            employees.add(se);
            txtOutput.setText(this.addTitle()+se.toString());
        }

        // Optional: clear inputs (keep type for convenience)
        txtName.setText("");
        txtWage.setText("");
        txtHours.setText("");
        if (type.equals("Salary")) txtHours.setText(""); // stays disabled anyway
        txtName.requestFocus();
    }

    private String addTitle() {
    	return 
    			String.format("%-8s %-20s %-12s %-8s %-10s %-10s %-10s\n",
                "Type", "Name", "Wage/Salary", "Hours", "Gross", "Taxes", "Net")
    			+"-".repeat(82)+"\n";
    }
    
    private void onReport() {
        if (employees.isEmpty()) {
            txtOutput.setText("No employees submitted yet.");
            return;
        }
        StringBuilder sb = new StringBuilder("Payroll Report\n");
        sb.append("-".repeat(82)+"\n");
        
        sb.append(this.addTitle());
        for (Employee emp : employees) {
//            sb.append(emp.toString()).append("\n");
//        }
	        if (emp instanceof HourlyEmployee he) {
	            sb.append(String.format("%-8s %-20s %-12.2f %-8.2f %-10.2f %-10.2f %-10.2f\n",
	                    "Hourly", he.name, he.hourlyWage, he.hoursWorked,
	                    he.grossPay, he.taxes, he.netpay));
	        } else if (emp instanceof SalaryEmployee se) {
	            sb.append(String.format("%-8s %-20s %-12.2f %-8s %-10.2f %-10.2f %-10.2f\n",
	                    "Salary", se.name, se.salary, "-",
	                    se.grossPay, se.taxes, se.netpay));
	        }
        }
        txtOutput.setText(sb.toString());
    }

    private void setAllInputsEnabled(boolean enabled) {
        txtName.setEnabled(enabled);
        txtWage.setEnabled(enabled);
        txtHours.setEnabled(enabled);
    }

    private void clearInputs() {
        txtName.setText("");
        txtWage.setText("");
        txtHours.setText("");
        txtOutput.setText("");
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Invalid Input", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new WageCalculator().setVisible(true));
    }
}
