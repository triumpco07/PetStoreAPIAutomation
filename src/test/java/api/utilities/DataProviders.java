package api.utilities;

import java.io.IOException;
import org.testng.annotations.DataProvider;

public class DataProviders {

    // ==========================================================
    // 1) DataProvider → returns all user details (2D array)
    // ==========================================================
    @DataProvider(name = "Data")
    public String[][] getAllData() throws IOException {

        String path = System.getProperty("user.dir") + "//testData//userData.xlsx";

        ExcelUtility xl = new ExcelUtility(path);	

        int rownum = xl.getRowCount("Sheet1");
        int colcount = xl.getCellCount("Sheet1", 1);

        String apidata[][] = new String[rownum][colcount];

        for (int i = 1; i <= rownum; i++) {
            for (int j = 0; j < colcount; j++) {
                apidata[i - 1][j] = xl.getCellData("Sheet1", i, j);
            }
        }

        return apidata;
    }

    // ==========================================================
    // 2) DataProvider → returns only usernames (1D array)
    // ==========================================================
    @DataProvider(name = "UserNames")
    public String[] getUserNames() throws IOException {

        String path = System.getProperty("user.dir") + "/testData/Userdata.xlsx";

        ExcelUtility xl = new ExcelUtility(path);

        int rownum = xl.getRowCount("Sheet1");

        String apidata[] = new String[rownum];

        for (int i = 1; i <= rownum; i++) {
            apidata[i - 1] = xl.getCellData("Sheet1", i, 1);  // Column 1 contains username
        }

        return apidata;
    }
}
