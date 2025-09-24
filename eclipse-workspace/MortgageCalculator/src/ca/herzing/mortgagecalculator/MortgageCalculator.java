package ca.herzing.mortgagecalculator;

import java.awt.*;
import java.sql.*;
import java.util.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class MortgageCalculator extends JFrame {
	private final ArrayList<LoanAccount> loans = new ArrayList<>();
    private final JTextField customerIDField = new JTextField();
    private final JTextField loanAmountField = new JTextField();
    private final JTextField interestRateField = new JTextField();
    private final JTextField termField = new JTextField();;
    private final JTextField homeValueField = new JTextField();
    private final JTextField monthlyPaymentField = new JTextField();
    private final JButton calculateButton = new JButton("Calculate Loan");
    private final JButton submitButton = new JButton("Submit");
    private final JButton exitButton = new JButton("Exit");
  
    
    // to change
    private final String DB_URL_CONNECTION = "jdbc:mysql://localhost:3306/loanaccounts";
	private final String DB_USERNAME = "root"; // change as needed
	private final String DB_PWD = "";     // change as needed
    
    public MortgageCalculator() {
    	super("Mortgage Calculator");
    	// setTitle("Mortgage Calculator");
    	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	
    	// Layout
        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBorder(new EmptyBorder(12, 12, 12, 12));
        setContentPane(root);
        
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(6, 6, 6, 6);
        gc.fill = GridBagConstraints.HORIZONTAL;
        
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        buttons.add(calculateButton);
        buttons.add(submitButton);
        buttons.add(exitButton);
      //  buttons.add(btnReport);
        
        int r = 0;
        this.addRow(form, gc, r++, new JLabel("Customer Id:"), customerIDField);
        this.addRow(form, gc, r++, new JLabel("Loan Amount:"), loanAmountField);
        this.addRow(form, gc, r++, new JLabel("Interest Rate:"), interestRateField);
        this.addRow(form, gc, r++, new JLabel("Term in Month:"), termField);
        this.addRow(form, gc, r++, new JLabel("Home Value: "), homeValueField);
        this.addRow(form, gc, r++, new JLabel("Monthly Payment:"), monthlyPaymentField);
        
        this.homeValueField.setEnabled(false);
        this.monthlyPaymentField.setEnabled(false);
        
        JPanel top = new JPanel(new BorderLayout(8, 8));
        top.add(form, BorderLayout.NORTH);
        top.add(buttons, BorderLayout.SOUTH);
        
        root.add(top, BorderLayout.NORTH);
        
        // Listeners
        calculateButton.addActionListener(e -> this.onCalculate());
        submitButton.addActionListener(e -> this.onSubmit());
        exitButton.addActionListener(e -> this.onExit());
        
        
        setPreferredSize(new Dimension(750, 500));
        pack();
        setLocationRelativeTo(null);
    }
    
    public void onCalculate() {
    	// Validate fields
    	String message = "";
        try {
        	String customerId = this.customerIDField.getText().trim();
        	
            if(customerId.isEmpty()) {
            	this.showError("Customer Id is required");
            	return;
            }
            message = "Invalid Loan Amount";
            double loanAmount = Double.parseDouble(this.loanAmountField.getText().trim());
            message = "Invalid Interest";
            double interestRate = Double.parseDouble(this.interestRateField.getText().trim());
            message = "Invalid Term";
            double term = Double.parseDouble(this.termField.getText().trim());            
            message = "";
            LoanAccount loan = new LoanAccount(loanAmount, interestRate, term);
            loan.calculateLoan();
            
            // update field value
            this.homeValueField.setText(String.format("%.2f",loan.homeValue));
            this.monthlyPaymentField.setText(String.format("%.2f",loan.monthlyPayment));
            
            loans.add(loan);
            showError("Loan Calculation Done Successfully");
            
        }catch (NumberFormatException ex) {
			// TODO: handle exception
        	showError(message == "" ? ex.getMessage(): message);
        	
		}
        
        
    }
    
    public void onSubmit() {
    	if(loans.isEmpty()) {
    		showError("No Loan Recorded Yet!");
    		return;
    	}
		
		 Connection conn = null;
		 PreparedStatement ps = null;
		 try {
			 conn = DriverManager.getConnection(this.DB_URL_CONNECTION, this.DB_USERNAME, this.DB_PWD);
		     String sql = "INSERT INTO loans (loanamount, interestrate, term, homevalue, monthlypayment, totalinterest) "
		     				+ "VALUES (?, ?, ?, ?, ?, ?)";
		     ps = conn.prepareStatement(sql);
		
		     // looping loans list
		     for (LoanAccount loan : loans) {
		         ps.setDouble(1, loan.loanAmount);
		         ps.setDouble(2, loan.interestRate);
		         ps.setInt(3, (int) loan.term);
		         ps.setDouble(4, loan.homeValue);
		         ps.setDouble(5, loan.monthlyPayment);
		         ps.setDouble(6, loan.totalInterest);
		         ps.executeUpdate();
		     }
		     loans.clear();
		     showError("All loan records submitted to database.");
		     
		 } catch (SQLException e) {
			 showError("Database error: " + e.getMessage());
		 }
		 finally { //closing all resources
			 try {
		            if (ps != null) ps.close();
		            if (conn != null) conn.close();
		        } catch (SQLException e) {
		        	showError("Error closing database resources: " + e.getMessage());
		        }
		 }
    	
    }
    
   
    public void onExit() {
    	System.exit(0);
    }
    
    private void addRow(JPanel panel, GridBagConstraints gc, int row, JComponent left, JComponent right) {
        gc.gridx = 0; gc.gridy = row; gc.weightx = 0;
        panel.add(left, gc);
        gc.gridx = 1; gc.gridy = row; gc.weightx = 1;
        panel.add(right, gc);
    }
    
    
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Invalid Input", JOptionPane.ERROR_MESSAGE);
    }
    
	public static void main(String[] args) {
		try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("MySQL JDBC Driver not found.");
        }
		 SwingUtilities.invokeLater(() -> new MortgageCalculator().setVisible(true));
	}
	
}
