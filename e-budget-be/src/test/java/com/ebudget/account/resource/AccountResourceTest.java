package com.ebudget.account.resource;

import com.ebudget.account.model.Account;
import com.ebudget.account.model.enumeration.AccountType;
import com.ebudget.account.repository.AccountRepository;
import com.ebudget.account.resource.request.NewAccountDTO;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.quarkus.test.InjectMock;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;

@QuarkusTest
@DisplayName("Account Resource")
@TestHTTPEndpoint(AccountResource.class)
class AccountResourceTest {
    @InjectMock
    AccountRepository accountRepository;

    @Test
    @DisplayName("Should a new account")
    void shouldAddAccount() {
        NewAccountDTO newAccountDTO = new NewAccountDTO(
                "logo",
                "name",
                AccountType.BANK_ACCOUNT,
                0.0
        );

        doNothing().when(accountRepository).persistAndFlush(any(Account.class));

        given()
                .contentType(ContentType.JSON)
        .when()
                .body(newAccountDTO)
                .post()
        .then()
                .statusCode(HttpResponseStatus.CREATED.code());
    }
}