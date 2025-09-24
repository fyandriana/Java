package ca.herzing.wagecalculator;

public class HourlyEmployee extends Employee implements Payroll {
    public String name;
    public double hourlyWage;
    public double hoursWorked;

    public HourlyEmployee(String name, double wage, double hours) {
        this.name = name;
        this.hourlyWage = wage;
        this.hoursWorked = hours;
    }

    @Override
    public void generatePayroll() {
        grossPay = hourlyWage * hoursWorked;
        taxes    = grossPay * 0.15;
        netpay   = grossPay - taxes;
    }

    @Override
    public String toString() {
    	return String.format("%-8s %-20s %-12.2f %-8.2f %-10.2f %-10.2f %-10.2f\n",
                "Hourly", name, hourlyWage, hoursWorked,
                grossPay, taxes, netpay);
    }
}
