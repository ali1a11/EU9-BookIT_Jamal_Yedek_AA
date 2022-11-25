package com.bookit.step_definitions;

import com.bookit.pages.SelfPage;
import com.bookit.utilities.BookItApiUtil;
import com.bookit.utilities.ConfigurationReader;
import com.bookit.utilities.DBUtils;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.Assert;

import java.util.Map;

import static io.restassured.RestAssured.*;


public class ApiStepDefs {
    String token;
    Response response;
    String emailGlobal;
    String studentEmail;
    String studentPassword;

    @Given("I logged Bookit api using {string} and {string}")
    public void i_logged_Bookit_api_using_and(String email, String password) {

        token = BookItApiUtil.generateToken(email, password);
        emailGlobal = email;


    }

    @When("I get the current user information from api")
    public void i_get_the_current_user_information_from_api() {

//        System.out.println("token = " + token);

        // send a GET request "/api/users/me" endpoint to get current user info

        response = given().accept(ContentType.JSON)
                .and().header("Authorization", token)
                .when().get(ConfigurationReader.get("base_url") + "/api/users/me");

//        System.out.println("response.statusCode() = " + response.statusCode());
//        System.out.println("response.path(\"firstName\") = " + response.path("firstName"));
//        System.out.println("response.path(\"lastName\") = " + response.path("lastName"));
//
//        response.prettyPrint();

    }

    @Then("status code should be {int}")
    public void status_code_should_be(int statusCode) { // Integer --> int
//        System.out.println("response.statusCode() = " + response.statusCode());

        // verify status code matches with the feature file expected status code
        Assert.assertEquals(statusCode, response.statusCode());

    }

    @Then("the information about current user from api and database should match")
    public void the_information_about_current_user_from_api_and_database_should_match() {
        // we will compare API and database in this step

        // get information from database
        // for jdbc @db over Scenario (in Hooks @Before("@db") and @After("@db")) custom tags)

        String query = "select id, firstname, lastname, role from users\n" +
                "where email = '" + emailGlobal + "'";

        Map<String, Object> dbMap = DBUtils.getRowMap(query);

        System.out.println("dbMap = " + dbMap);

        // save database info into variables
        Long expextedID = (Long) dbMap.get("id");
        String expectedFirstName = (String) dbMap.get("firstname");
        String expectedLastName = (String) dbMap.get("lastname");
        String expectedRole = (String) dbMap.get("role");

        // get information from API
        JsonPath jsonPath = response.jsonPath();

        // save API info into variables
        Long actualID = jsonPath.getLong("id");
        String actualFirstName = jsonPath.getString("firstName");
        String actualLastName = jsonPath.getString("lastName");
        String actualRole = jsonPath.getString("role");

        // compare database vs API
        Assert.assertEquals(expextedID, actualID);
        Assert.assertEquals(expectedFirstName, actualFirstName);
        Assert.assertEquals(expectedLastName, actualLastName);
        Assert.assertEquals(expectedRole, actualRole);

    }

    @Then("UI, API and Database user information must be match")
    public void ui_API_and_Database_user_information_must_be_match() {
        // get information from database
        // for jdbc @db over Scenario (in Hooks @Before("@db") and @After("@db")) custom tags)

        String query = "select id, firstname, lastname, role from users\n" +
                "where email = '" + emailGlobal + "'";

        Map<String, Object> dbMap = DBUtils.getRowMap(query);

        System.out.println("dbMap = " + dbMap);

        // save database info into variables
        Long expextedID = (Long) dbMap.get("id");
        String expectedFirstName = (String) dbMap.get("firstname");
        String expectedLastName = (String) dbMap.get("lastname");
        String expectedRole = (String) dbMap.get("role");

        // get information from API
        JsonPath jsonPath = response.jsonPath();

        // save API info into variables
        Long actualID = jsonPath.getLong("id");
        String actualFirstName = jsonPath.getString("firstName");
        String actualLastName = jsonPath.getString("lastName");
        String actualRole = jsonPath.getString("role");

        // get information from UI
        SelfPage selfPage = new SelfPage();

        String actualUIName = selfPage.name.getText();
        String actualUIRole = selfPage.role.getText();

        System.out.println("selfPage.name.getText() = " + selfPage.name.getText());
        System.out.println("selfPage.role.getText() = " + selfPage.role.getText());

        // UI vs DB
        String expectedFullName = expectedFirstName + " " + expectedLastName;
        Assert.assertEquals(expectedFullName, actualUIName);
        Assert.assertEquals(expectedRole, actualUIRole);

        // UI vs API
        // create fullname for API
        String actualFullName = actualFirstName + " " + actualLastName;
        Assert.assertEquals(expectedFullName, actualFullName);
        Assert.assertEquals(actualRole, actualUIRole);


    }

    @When("I send POST request to {string} endpoint with following information")
    public void i_send_POST_request_to_endpoint_with_following_information(String path, Map<String, String> studentInfo) {

        // why we prefer to get information as a map from feature file?
        // because we have queryParams method that takes map and pass to url as query key&value structure

        // assign email and password value to these variables so that we can use them later for deleting
        studentEmail = studentInfo.get("email");
        studentPassword = studentInfo.get("password");

        response = given().accept(ContentType.JSON)
                .queryParams(studentInfo)
                .and().header("Authorization", token)
                .log().all()
                .when()
                .post(ConfigurationReader.get("base_url") + path)
                .then().log().all().extract().response();
    }

    @Then("I delete previously added student")
    public void i_delete_previously_added_student() {

        // We have created one method to delete student,
        // you pass email and password of the student that you want to delete

        BookItApiUtil.deleteStudent(studentEmail, studentPassword);
        /* --> BookItApiUtil.deleteStudent();
        // 1. send a GET request to get token with student information
        String studentToken = BookItApiUtil.generateToken(studentInfo.get("email"), studentInfo.get("password"));

        // 2. send a GET request to /api/users/me endpoint and get the id number
        int idToDelete = given().accept(ContentType.JSON)
                .and().header("Authorization", studentToken)
                .when().get(ConfigurationReader.get("base_url") + "/api/users/me")
                .then().statusCode(200)
                .extract().jsonPath().getInt("id");

        // 3. send a DELETE request as a teacher to /api/students/{id} endpoint to delete the student

        String teacherToken = BookItApiUtil.generateToken(ConfigurationReader.get("teacher_email"), ConfigurationReader.get("teacher_password"));

        given().pathParam("id", idToDelete)
                .and().header("Authorization",teacherToken)
                .when().delete(ConfigurationReader.get("base_url") + "/api/students/{id}")
                .then().statusCode(204);
        */
    }

    Long IDFromAPI;
    String firstNameFromAPI;
    String lastNameFromAPI;
    String fullNameFromAPI;
    String roleFromAPI;
    int batchNumberFromAPI;
    String teamNameFromAPI;
    String campusFromAPI;

    @When("I get the current user information set from api")
    public void i_get_the_current_user_information_set_from_api() {

        // send a GET request "/api/users/me" endpoint to get current user info
        response = given().accept(ContentType.JSON)
                .and().header("Authorization", token)
                .when().get(ConfigurationReader.get("base_url") + "/api/users/me");

        // get information from API
        JsonPath jsonPath = response.jsonPath();

        // save API info into variables
        IDFromAPI = jsonPath.getLong("id");
        firstNameFromAPI = jsonPath.getString("firstName");
        lastNameFromAPI = jsonPath.getString("lastName");
        fullNameFromAPI = firstNameFromAPI + " " + lastNameFromAPI;
        roleFromAPI = jsonPath.getString("role");

        // to get batch number
        // send a GET request "/api/batches/my" endpoint to get current user info
        batchNumberFromAPI = given().accept(ContentType.JSON)
                .and().header("Authorization", token)
                .when().get(ConfigurationReader.get("base_url") + "/api/batches/my")
                .body().path("number");

//        System.out.println("batchNumber = " + batchNumberFromAPI);

        // to get campus
        // send a GET request "/api/campuses/my" endpoint to get current user info
        campusFromAPI = given().accept(ContentType.JSON)
                .and().header("Authorization", token)
                .when().get(ConfigurationReader.get("base_url") + "/api/campuses/my")
                .body().path("location");

//        System.out.println("campusLocation = " + campusFromAPI);

        // to get team name
        // send a GET request "/api/teams/my" endpoint to get current user info
        teamNameFromAPI = given().accept(ContentType.JSON)
                .and().header("Authorization", token)
                .when().get(ConfigurationReader.get("base_url") + "/api/teams/my")
                .body().path("name");

//        System.out.println("teamName = " + teamNameFromAPI);
    }

    @Then("UI, API and Database user information set must be match")
    public void ui_API_and_Database_user_information_set_must_be_match() {

        // get information from database
        // for jdbc @db over Scenario (in Hooks @Before("@db") and @After("@db")) custom tags)

        String query2 = "select users.id, users.firstname, users.lastname, users.role, c.location, t.name, t.batch_number from users join team t on users.team_id = t.id join campus c on c.id = users.campus_id\n" +
                "where users.id = " + IDFromAPI;

        Map<String, Object> dbMap2 = DBUtils.getRowMap(query2);

//        System.out.println("dbMap2 = " + dbMap2);

        // save database info into variables
        Long expextedIDFromDB = (Long) dbMap2.get("id");
        String expectedFirstNameFromDB = (String) dbMap2.get("firstname");
        String expectedLastNameFromDB = (String) dbMap2.get("lastname");
        String expectedFullNameFromDB = expectedFirstNameFromDB + " " + expectedLastNameFromDB;
        String expectedRoleFromDB = (String) dbMap2.get("role");
        String expectedTeamNameFromDB = (String) dbMap2.get("name");
        int expectedBatchNumberFromDB = (int) dbMap2.get("batch_number");
        String expectedCampusLocationFromDB = (String) dbMap2.get("location");


        // get information from UI
        SelfPage selfPage = new SelfPage();

        String actualNameFromUI = selfPage.name.getText();
        String actualRoleFromUI = selfPage.role.getText();
        String actualTeamFromUI = selfPage.team.getText();
        String actualBatchNumberFromUIString = selfPage.batch.getText();
        int actualBatchNumberFromUI = Integer.parseInt(actualBatchNumberFromUIString.substring(actualBatchNumberFromUIString.indexOf('#') + 1));
        String actualCampusFromUI = selfPage.campus.getText();

        // UI vs DB
        Assert.assertEquals(expectedFullNameFromDB, actualNameFromUI);
        Assert.assertEquals(expectedRoleFromDB, actualRoleFromUI);
        Assert.assertEquals(expectedTeamNameFromDB, actualTeamFromUI);
        Assert.assertEquals(expectedBatchNumberFromDB, actualBatchNumberFromUI);
        Assert.assertEquals(expectedCampusLocationFromDB, actualCampusFromUI);

        // UI vs API
        Assert.assertEquals(fullNameFromAPI, actualNameFromUI);
        Assert.assertEquals(roleFromAPI, actualRoleFromUI);
        Assert.assertEquals(teamNameFromAPI, actualTeamFromUI);
        Assert.assertEquals(batchNumberFromAPI, actualBatchNumberFromUI);
        Assert.assertEquals(campusFromAPI, actualCampusFromUI);

    }


}
