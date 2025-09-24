package ca.herzing.mortgagecalculator;

public class LoanAccount {
	public double loanAmount; // required
	public double interestRate; // required
	public double term; //term is in months required
	public double homeValue; //required
	public double monthlyPayment;
	public double totalInterest;
	
	public LoanAccount(double loanAmount, double interestRate, double term) {
		this.loanAmount = loanAmount;
		this.interestRate = interestRate;
		this.term = term;
		// this.homeValue =  this.loanAmount * 1.25;
	}
	
	@Override
	public String toString() {
		return String.format(""
				+ "LoanAmount: %.2f\n "
				+ "InterestRate: %.2f%%\n "
				+ "Term: %.0f months\n "
				+ "HomeValue: %.2f\n "
				+ "MonthlyPayment: %.2f\n "
				+ "TotalInterest: %.2f\n",
	                loanAmount, interestRate, term, homeValue, monthlyPayment, totalInterest);
	}
	
	
	
	public void calculateLoan() {
//		calcInterest = interestRate / (12 * 100).  5pts
//		The monthlyPayment = loanAmount * (calcInterest / (1-(Math.pow(1 + calcInterest, -this.term))))   3pts
//		homeValue = loanAmount * 1.25
//		totalInterest = loanAmount * interestRate * (term / 12)
		double calcInterest = this.interestRate/(12*100);	
		this.monthlyPayment = this.loanAmount * (calcInterest/(1-(Math.pow(1+ calcInterest, -this.term))));
		this.homeValue =  this.loanAmount * 1.25;
		this.totalInterest = this.loanAmount * this.interestRate *(this.term / 12);
		
	}
}
