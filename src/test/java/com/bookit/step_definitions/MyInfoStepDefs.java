package com.bookit.step_definitions;


import com.bookit.pages.SelfPage;
import com.bookit.pages.SignInPage;
import com.bookit.pages.TopNavigationBar;
import com.bookit.utilities.*;
import cucumber.runtime.Env;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;


public class MyInfoStepDefs {

    // UI https://cybertek-reservation-qa.herokuapp.com/sign-in
    @Given("user logs in using {string} {string}")
    public void user_logs_in_using(String email, String password) {
        TopNavigationBar topNavigationBar = new TopNavigationBar();

        Driver.get().get(ConfigurationReader.get("url"));
        Driver.get().manage().window().maximize();
        SignInPage signInPage = new SignInPage();
        signInPage.email.sendKeys(email);
        signInPage.password.sendKeys(password);
        BrowserUtils.waitFor(1);
        signInPage.signInButton.click();

        WebDriverWait wait = new WebDriverWait(Driver.get(), 10);
        wait.until(ExpectedConditions.visibilityOf(topNavigationBar.my));

    }

    // UI https://cybertek-reservation-qa.herokuapp.com/sign-in
    @When("user is on the my self page")
    public void user_is_on_the_my_self_page() {
        BrowserUtils.waitFor(3);
        SelfPage selfPage = new SelfPage();
        selfPage.goToSelf();
    }



}
