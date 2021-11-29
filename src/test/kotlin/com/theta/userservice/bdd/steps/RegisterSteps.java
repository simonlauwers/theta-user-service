package com.theta.userservice.bdd.steps;

import com.theta.userservice.model.User;
import cucumber.api.java8.En;
import io.cucumber.datatable.DataTable;

import java.util.List;

public class RegisterSteps implements En {

    public RegisterSteps() {
        Given("I have the following users in my database$", (DataTable users) -> {
            List<User> employeeList = users.asList(User.class);
            employeeList.stream().map(User::getDisplayName).forEach(System.out::println);
        });
        When("the client calls endpoint register with email youssef@dev\\.com,$", () -> {
        });
        Then("the client receives status code of (\\d+)$", (Integer arg0) -> {
        });
        And("the client receives the created user object$", () -> {
        });
        When("the client calls endpoint register with email nathan@dev\\.com$", () -> {
        });
        And("the client receives message that the user already exists$", () -> {
        });
    }
}