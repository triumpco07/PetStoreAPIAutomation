package api.utilities;

import java.io.IOException;
import org.testng.annotations.DataProvider;

public class DataProviders {

    private static final String EXCEL_PATH =
            System.getProperty("user.dir") + "/testData/userData.xlsx";
    
    private static final String PET_EXCEL_PATH =
            System.getProperty("user.dir") + "/testData/PetData.xlsx";
    // ==========================================================
    // 1) Create / Update / Login (Positive)
    // ==========================================================
    @DataProvider(name = "Data")
    public String[][] getAllData() throws IOException {

        ExcelUtility xl = new ExcelUtility(EXCEL_PATH);

        int rowCount = xl.getRowCount("Sheet1");      // excludes header
        int colCount = xl.getCellCount("Sheet1", 1);

        String[][] data = new String[rowCount][colCount];

        for (int i = 1; i <= rowCount; i++) {
            for (int j = 0; j < colCount; j++) {
                data[i - 1][j] = xl.getCellData("Sheet1", i, j);
            }
        }
        return data;
    }

    // ==========================================================
    // 2) Delete User
    // ==========================================================
    @DataProvider(name = "UserNames")
    public String[] getUserNames() throws IOException {

        ExcelUtility xl = new ExcelUtility(EXCEL_PATH);
        int rowCount = xl.getRowCount("Sheet1");

        String[] data = new String[rowCount];

        for (int i = 1; i <= rowCount; i++) {
            data[i - 1] = xl.getCellData("Sheet1", i, 1);
        }
        return data;
    }

    // ==========================================================
    // 3) Login Negative Scenarios
    // ==========================================================
    @DataProvider(name = "LoginNegativeData")
    public String[][] getNegativeLoginData() throws IOException {

        ExcelUtility xl = new ExcelUtility(EXCEL_PATH);

        int rowCount = xl.getRowCount("LoginNegative");
        int colCount = xl.getCellCount("LoginNegative", 1);

        String[][] data = new String[rowCount][colCount];

        for (int i = 1; i <= rowCount; i++) {
            for (int j = 0; j < colCount; j++) {
                data[i - 1][j] = xl.getCellData("LoginNegative", i, j);
            }
        }
        return data;
    }
    
    // ==========================================================
    // 4) Create Pet
    // ==========================================================
    
    @DataProvider(name = "PetData")
    public Object[][] getPetData() throws IOException {
        
        ExcelUtility xl = new ExcelUtility(PET_EXCEL_PATH);

        int rows = xl.getRowCount("PetData");
        int cols = xl.getCellCount("PetData", 1);

        Object[][] data = new Object[rows][cols];

        for (int i = 1; i <= rows; i++) {
            for (int j = 0; j < cols; j++) {
                data[i - 1][j] = xl.getCellData("PetData", i, j);
            }
        }
        return data;
    }
    
    // ==========================================================
    // 5) Pet Statuses for Find Pets by Status
    // ==========================================================
    @DataProvider(name = "PetStatuses")
    public Object[][] getPetStatuses() {
        return new Object[][] {
            {"available"},
            {"pending"},
            {"sold"}
        };
    }
    
    @DataProvider(name = "PlaceOrderData")
    public String[][] getPlaceOrderData() throws IOException {

        String path = System.getProperty("user.dir") + "/testData/PlaceOrder.xlsx";
        ExcelUtility xl = new ExcelUtility(path);

        int rownum = xl.getRowCount("PlaceOrder");
        int colcount = xl.getCellCount("PlaceOrder", 1);

        String orderData[][] = new String[rownum][colcount];

        for (int i = 1; i <= rownum; i++) {
            for (int j = 0; j < colcount; j++) {
                orderData[i - 1][j] = xl.getCellData("PlaceOrder", i, j);
            }
        }

        return orderData;
    }


    

}
