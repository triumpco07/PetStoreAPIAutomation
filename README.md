![Java](https://img.shields.io/badge/Java-17-blue)
![Maven](https://img.shields.io/badge/Maven-Build-success)
![TestNG](https://img.shields.io/badge/TestNG-Framework-green)
![RestAssured](https://img.shields.io/badge/Rest--Assured-API-orange)
![ExtentReports](https://img.shields.io/badge/Reporting-ExtentReports-purple)
![CI/CD](https://img.shields.io/badge/CI%2FCD-Jenkins-red)



# 🐾 PetStore API Automation Framework

This project is a **complete API Automation Framework** built using **Java, Rest Assured, TestNG, and Maven**, targeting the **Swagger PetStore API**.

It demonstrates **real-world API testing practices** including:
- Data-driven testing
- API chaining
- End-to-end (E2E) scenarios
- Reporting and logging
- CI/CD readiness with Jenkins

The framework covers **User**, **Pet**, and **Store** modules and is designed to be **scalable, maintainable, and production-ready**.

---

## 🚀 What This Project Does

- Automates REST APIs using **Rest Assured**
- Supports **Data-Driven Testing** using Excel
- Implements **API Chaining** and **End-to-End flows**
- Generates detailed **Extent HTML Reports**
- Uses **Log4j2** for structured logging
- Supports **CI/CD execution** using Jenkins

---

## 🛠 Tech Stack Used

- **Java**
- **Rest Assured**
- **TestNG**
- **Maven**
- **Apache POI** (Excel integration)
- **Extent Reports**
- **Log4j2**
- **Jenkins** (CI/CD)

---

## 📂 Project Structure Overview
```bash
project-root
│
├── src/test/java
│   ├── api.endpoints        # All API endpoint methods
│   ├── api.payload          # POJO classes (User, Pet, Store)
│   ├── api.test             # Test classes (DD, E2E, Regression)
│   ├── api.utilities        # Excel, Extent, Helpers
│
├── src/test/resources
│   ├── routes.properties    # API endpoints
│   ├── log4j2.xml           # Logging configuration
│
├── testData
│   ├── UserData.xlsx
│   ├── PetData.xlsx
│   ├── StoreData.xlsx
│   └── E2E_Data.xlsx
│
├── reports                  # Extent HTML reports
├── pom.xml
├── testng.xml
└── README.md
```


---

## 📦 Modules Covered

### 👤 User Module
- Create user
- Get user by username
- Update user
- Delete user
- Login and logout
- Data-driven user tests

### 🐶 Pet Module
- Add new pet
- Update pet
- Get pet by ID
- Delete pet
- Upload pet image
- Data-driven pet creation

### 🛒 Store Module
- Place order for a pet
- Get order by ID
- Delete order
- Get inventory by status
- Performance and negative testing

---

## 🔗 End-to-End (E2E) Testing

This project includes **real API chaining**, where output from one API is used as input for the next.

### Example E2E Flow
1. Create User  
2. Create Pet  
3. Place Order for the Pet  
4. Verify Order using Order ID  
5. Cleanup (Delete Order, Pet, User)

This mimics **real production workflows**, not isolated API calls.

---

## 📊 Data-Driven Testing (Excel)

All test data is maintained in **Excel sheets**:
- Positive scenarios
- Negative scenarios
- Boundary cases
- Invalid inputs

### Example E2E Excel Columns
username | firstName | lastName | email | password | phone | petName | petStatus | orderQty


**Apache POI** is used to read Excel data dynamically.

---

## 📈 Reporting (Extent Reports)

- Auto-generated **HTML reports**
- Each test step is logged
- Supports **parallel execution** (ThreadLocal)

📁 Reports are generated under: /reports

## Extent Report Preview

![Extent Report](https://github.com/triumpco07/PetStoreAPIAutomation/blob/master/screenshorts/extent-report-summary.png?raw=true)
![Extent Report](https://github.com/triumpco07/PetStoreAPIAutomation/blob/master/screenshorts/extent-report-test-details.png?raw=true)


## 🪵 Logging (Log4j2)

- File-based logging enabled
- Logs API requests, responses, warnings, and errors
- Useful for debugging and CI/CD analysis

📁 Logs are available under: /logs/automation.log


## ▶️ Running the Tests

### Run all tests
```bash
mvn clean test
```

## 🔁 CI/CD with Jenkins

This framework is **Jenkins-ready**:

- Code checkout from GitHub  
- Maven build execution  
- Test execution  
- Extent report generation  
- Report archived as Jenkins artifact

## CI/CD Pipeline (Jenkins)

![Jenkins Pipeline](https://github.com/triumpco07/PetStoreAPIAutomation/blob/master/screenshorts/jenkins-build-success.png?raw=true)
![Jenkins Pipeline](https://github.com/triumpco07/PetStoreAPIAutomation/blob/master/screenshorts/Jenkins-pipeline-overview.png?raw=true)

### Supports
- Smoke testing  
- Regression testing  
- End-to-end pipeline execution  

---

## 🧠 Design Principles Followed

- Clean separation of concerns  
- POJO-based request bodies  
- Reusable endpoint methods  
- Centralized data management  
- Industry-standard assertions  
- Graceful handling of known API limitations  

---

## API Chaining Example

User places order for a pet (E2E flow):

1. Create User → extract username
2. Create Pet → extract petId
3. Place Order → use petId
4. Get Order → validate order
5. Cleanup → delete order, pet, user

## Why This Project?

This project is designed to demonstrate:
- Real-world API automation design
- Data-driven testing using Excel
- API chaining and end-to-end flows
- Reporting and logging best practices
- CI/CD readiness using Jenkins

