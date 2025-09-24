package ca.herzing.wagecalculator;

public abstract class Employee {
    public double grossPay;
    public double taxes;
    public double netpay;

    /** Force children to implement payroll calculation */
    public abstract void generatePayroll();
}
