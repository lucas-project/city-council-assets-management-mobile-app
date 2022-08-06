package com.application.issue_reporter;

public class ReportDistance {

    public ReportDistance(Report report, double aDouble) {
        this.report = report;
        this.aDouble = aDouble;
    }

    public Report getReport() {
        return report;
    }

    public void setReport(Report report) {
        this.report = report;
    }

    public double getaDouble() {
        return aDouble;
    }

    public void setaDouble(double aDouble) {
        this.aDouble = aDouble;
    }

    private Report report;
    private double aDouble;
}
