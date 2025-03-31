# RecipesApp - Spring Boot Backend 🍽️

This is a Spring Boot-based REST API that provides recipe management functionality, including fetching recipes from an external API, retrieving stored recipes, and searching recipes by name and cuisine.

---

## 📌 Features
- Fetch and store recipe data from an external API.
- Retrieve a single recipe by its ID.
- Search recipes by name and cuisine.

---


## 🚀 Getting Started

### **1️⃣ Prerequisites**

Make sure you have the following installed:
- **Java 17** or later
- **Maven** (for dependency management)
- **Postman** (optional, for testing API endpoints)
- **Spring Tool Suite (STS)** (for running the project in an IDE)

---

### 🛠 **Project Structure**  
```
src/  
├── main/  
│   ├── java/com/publicis/sapient/recipeapi/  
│   │   ├── rest/              # Controllers (API Endpoints)  
│   │   ├── service/           # Business Logic Layer  
│   │   ├── dto/               # Data Transfer Objects  
│   │   ├── entity/            # Database Entities  
│   │   ├── repo/              # Repository Interfaces  
│   │   ├── mapper/            # Entity <-> DTO Conversions  
│   │   ├── exception/         # Custom Exceptions  
│   ├── resources/  
│   │   ├── application.properties  # Configurations  
│   │   ├── application-dev.properties  # Development Config  
│   │   ├── application-prod.properties  # Production Config  
│   │   ├── application-test.properties  # Test Config    
├── test/  
│   ├── java/com/publicis/sapient/recipeapi/  
│   │   ├── rest/              # Controller Tests  
│   │   ├── service/           # Service Layer Tests  
│   │   ├── mapper/            # Mapper Tests  
│   │   ├── exception/         # Exception Handling Tests  
│   │   ├── ApplicationTests.java # Integration Tests  
├── README.md  


```

## ⚙️ **Running the Project in STS (Spring Tool Suite)**
### **1️⃣ Import the Project**
1. Open **Spring Tool Suite (STS)**.
2. Click **File** → **Import...** → **Existing Maven Projects**.
3. Browse to the project folder and click **Finish**.

### **2️⃣ Run the Application**
1. Locate `RecipeApiApplication.java` in `src/main/java`.
2. Right-click the file and select **Run As → Spring Boot App**.

### **3️⃣ Verify the Application**
Once the application starts, you should see logs indicating that it is running on **http://localhost:8080**.



## ⚙️ **Building & Running the Project in terminal**
### **Run with Maven**
```sh
mvn spring-boot:run
```

### **Run with Java**
After building the project, run:
```sh
java -jar target/recipeapi-app-0.0.1-SNAPSHOT.jar

```

### **Build JAR File**
```sh
mvn clean package
```

---

## 🔥 API Endpoints

### **1️⃣ Fetch and Store External Recipes**
**Endpoint:** `POST /recipes/external-api`  
**Response:** Fetches recipes from an external API and saves them in the database.

**Example Request:**
Post Request
  http://localhost:8080/recipes/external-api
  
### **2️⃣ Get Recipe by ID**
**Endpoint:** `GET /recipe?recipeId={id}`  
**Response:** Returns the details of a single recipe.  
**Query Params:** 
    - `id` - Recipe Id
GET Request
  http://localhost:8080/recipe?recipeId=1

### **3️⃣ Search Recipes by Name & Cuisine**
**Endpoint:** `GET /recipes`  
**Response:** Returns the list of matching recipes.  
**Query Params:**
- `name` (mandatory) - Recipe name
- `cuisine` (optional) - Cuisine type

**Example Request:**
GET Request
 http://localhost:8080/recipes?name=pasta&cuisine=italian



### 🧪 **Running Tests**
To execute unit tests, run:
```sh
mvn test
```

## 🌱 **Test Coverage**

1. Run tests and generate coverage report:

```sh
mvn clean test
mvn verify
 ```

2. Open the report at `target/site/jacoco/index.html` in a browser.

---

## 👨‍💻 **Contributor**
- **Hariprasath V** - Software Engineer



