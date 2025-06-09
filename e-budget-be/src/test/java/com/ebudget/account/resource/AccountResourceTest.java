package com.ebudget.account.resource;

import com.ebudget.account.model.enums.AccountType;
import com.ebudget.account.resource.request.NewAccountDTO;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@QuarkusTest
@DisplayName("Account Resource")
@TestHTTPEndpoint(AccountResource.class)
class AccountResourceTest {
    @Test
    @DisplayName("Should add a new account")
    void shouldAddAccount() {
        NewAccountDTO newAccountDTO = new NewAccountDTO(
                "accountLogo",
                "accountName",
                AccountType.BANK_ACCOUNT,
                new BigDecimal("0.0")
        );

        given()
            .contentType(ContentType.JSON)
            .body(newAccountDTO)
        .when()
            .post()
        .then()
            .statusCode(Response.Status.CREATED.getStatusCode())
            .contentType(ContentType.JSON)
            .body("accountId", notNullValue())
            .body("accountLogo", equalTo(newAccountDTO.accountLogo()))
            .body("accountName", equalTo(newAccountDTO.accountName()))
            .body("accountType", equalTo(newAccountDTO.accountType().toString()))
            .body("initialBalance", equalTo(newAccountDTO.initialBalance().floatValue()))
            .body("balance", equalTo(newAccountDTO.initialBalance().floatValue()))
            .body("createdAt", notNullValue())
            .body("updatedAt", notNullValue());
    }
}