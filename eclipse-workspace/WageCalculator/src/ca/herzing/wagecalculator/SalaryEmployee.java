package ca.herzing.wagecalculator;

public class SalaryEmployee extends Employee implements Payroll {
    public String name;
    public double salary; // yearly salary

    public SalaryEmployee(String name, double salary) {
        this.name = name;
        this.salary = salary;
    }

    @Override
    public void generatePayroll() {
        // Year = 52 weeks
        double weeklysalary = salary / 52.0;
        grossPay = weeklysalary;
        taxes    = weeklysalary * 0.15;
        netpay   = grossPay - taxes;
    }

    @Override
    public String toString() {
    	return String.format("%-8s %-20s %-12.2f %-8s %-10.2f %-10.2f %-10.2f\n",
                "Salary", name, salary, "-",
                grossPay, taxes, netpay);
    }
}