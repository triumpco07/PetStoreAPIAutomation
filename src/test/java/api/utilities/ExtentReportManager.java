package api.utilities;

// Extent report 5.x

import java.text.SimpleDateFormat;
import java.util.Date;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

public class ExtentReportManager implements ITestListener
{
    public ExtentSparkReporter sparkReporter;
    public ExtentReports extent;
    public ExtentTest test;
    
    // ThreadLocal to store ExtentTest instance for each thread
    private static ThreadLocal<ExtentTest> extentTest = new ThreadLocal<ExtentTest>();

    String repName;
    
    // Method to get ExtentTest instance from test methods
    public static ExtentTest getTest() {
        return extentTest.get();
    }

    public void onStart(ITestContext testContext)
    {
        String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()); // time stamp
        repName = "Test-Report-" + timeStamp + ".html";

        sparkReporter = new ExtentSparkReporter(".\\reports\\" + repName); // specify location of the report

        sparkReporter.config().setDocumentTitle("RestAssuredAutomationProject"); // Title of report
        sparkReporter.config().setReportName("Pet Store API - Users & Pets"); // name of the report
        sparkReporter.config().setTheme(Theme.DARK);

        extent = new ExtentReports();
        extent.attachReporter(sparkReporter);

        extent.setSystemInfo("Application", "Pet Store API - Users & Pets");
        extent.setSystemInfo("Operating System", System.getProperty("os.name"));
        extent.setSystemInfo("User Name", System.getProperty("user.name"));
        extent.setSystemInfo("Environment", "QA");
    }

    public void onTestStart(ITestResult result)
    {
        test = extent.createTest(result.getName());
        test.assignCategory(result.getMethod().getGroups());
        extentTest.set(test); // Store in ThreadLocal for access in test methods
    }

    public void onTestSuccess(ITestResult result)
    {
        test = extentTest.get();
        if (test == null) {
            test = extent.createTest(result.getName());
            extentTest.set(test);
        }
        test.assignCategory(result.getMethod().getGroups());
        test.createNode(result.getName());
        test.log(Status.PASS, "Test Passed");
    }

    public void onTestFailure(ITestResult result)
    {
        test = extentTest.get();
        if (test == null) {
            test = extent.createTest(result.getName());
            extentTest.set(test);
        }
        test.createNode(result.getName());
        test.assignCategory(result.getMethod().getGroups());
        test.log(Status.FAIL, "Test Failed");
        test.log(Status.FAIL, result.getThrowable().getMessage());
    }

    public void onTestSkipped(ITestResult result)
    {
        test = extentTest.get();
        if (test == null) {
            test = extent.createTest(result.getName());
            extentTest.set(test);
        }
        test.createNode(result.getName());
        test.assignCategory(result.getMethod().getGroups());
        test.log(Status.SKIP, "Test Skipped");
        test.log(Status.SKIP, result.getThrowable().getMessage());
    }
    
    public void onTestFinish(ITestResult result)
    {
        extentTest.remove(); // Clean up ThreadLocal after test
    }

    public void onFinish(ITestContext testContext)
    {
        extent.flush();
    }
}
