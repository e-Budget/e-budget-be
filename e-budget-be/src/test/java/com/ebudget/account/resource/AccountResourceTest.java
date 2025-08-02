package com.ebudget.account.resource;

import com.ebudget.account.model.Account;
import com.ebudget.account.model.enums.FinancialInstitution;
import com.ebudget.account.model.enums.AccountType;
import com.ebudget.account.repository.AccountRepository;
import com.ebudget.account.resource.request.NewAccountDTO;
import com.ebudget.account.resource.request.UpdateAccountDTO;
import com.ebudget.account.resource.response.AccountDTO;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
@DisplayName("Account Resource")
@TestHTTPEndpoint(AccountResource.class)
class AccountResourceTest {
    @Inject
    AccountRepository accountRepository;

    private Account sampleAccount;

    @BeforeEach
    @Transactional
    void setup() {
        sampleAccount = Account.builder()
                .financialInstitution(FinancialInstitution.SANTANDER)
                .accountName("accountName")
                .accountType(AccountType.BANK_ACCOUNT)
                .initialBalance(new BigDecimal("0.00"))
                .balance(new BigDecimal("0.00"))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        accountRepository.persistAndFlush(sampleAccount);
    }

    @AfterEach
    @Transactional
    void destroy() {
        accountRepository.deleteAll();
    }

    @Test
    @DisplayName("Should add a new account")
    void shouldAddAccount() {
        NewAccountDTO newAccountDTO = new NewAccountDTO(
                FinancialInstitution.NONE,
                "accountName",
                AccountType.BANK_ACCOUNT,
                new BigDecimal("0.00")
        );

        AccountDTO response = given()
            .contentType(ContentType.JSON)
            .body(newAccountDTO)
        .when()
            .post()
        .then()
            .statusCode(Response.Status.CREATED.getStatusCode())
            .contentType(ContentType.JSON)
            .extract()
            .as(new TypeRef<AccountDTO>() {});

        assertThat(response.getAccountId()).isNotNull();
        assertThat(response.getAccountId()).isInstanceOf(UUID.class);
        assertThat(response.getFinancialInstitution()).isEqualTo(newAccountDTO.financialInstitution());
        assertThat(response.getAccountName()).isEqualTo(newAccountDTO.accountName());
        assertThat(response.getAccountType()).isEqualTo(newAccountDTO.accountType());
        assertThat(response.getInitialBalance()).isEqualTo(newAccountDTO.initialBalance());
        assertThat(response.getBalance()).isEqualTo(newAccountDTO.initialBalance());
        assertThat(response.getCreatedAt()).isNotNull();
        assertThat(response.getCreatedAt()).isInstanceOf(LocalDateTime.class);
        assertThat(response.getUpdatedAt()).isNotNull();
        assertThat(response.getUpdatedAt()).isInstanceOf(LocalDateTime.class);
    }

    @Test
    @DisplayName("Should update an account")
    void shouldUpdateAnAccount() {
        UpdateAccountDTO updateAccountDTO = new UpdateAccountDTO(
                FinancialInstitution.NONE,
                "newAccountName",
                AccountType.BENEFIT_ACCOUNT
        );

        given()
            .contentType(ContentType.JSON)
            .body(updateAccountDTO)
        .when()
            .put(String.valueOf(sampleAccount.getAccountId()))
        .then()
            .statusCode(Response.Status.NO_CONTENT.getStatusCode());
    }

    @Test
    @DisplayName("Should get an account")
    void shouldGetAnAccount() {
        AccountDTO response = given()
            .contentType(ContentType.JSON)
        .when()
            .get(String.valueOf(sampleAccount.getAccountId()))
        .then()
            .statusCode(Response.Status.OK.getStatusCode())
            .contentType(ContentType.JSON)
            .extract()
            .as(new TypeRef<AccountDTO>() {});

        assertThat(response.getAccountId()).isEqualTo(sampleAccount.getAccountId());
        assertThat(response.getFinancialInstitution()).isEqualTo(sampleAccount.getFinancialInstitution());
        assertThat(response.getAccountName()).isEqualTo(sampleAccount.getAccountName());
        assertThat(response.getAccountType()).isEqualTo(sampleAccount.getAccountType());
        assertThat(response.getInitialBalance()).isEqualTo(sampleAccount.getInitialBalance());
        assertThat(response.getBalance()).isEqualTo(sampleAccount.getBalance());
        assertThat(response.getCreatedAt()).isEqualTo(sampleAccount.getCreatedAt());
        assertThat(response.getUpdatedAt()).isEqualTo(sampleAccount.getUpdatedAt());
    }

    @Test
    @DisplayName("Should get all accounts")
    void shouldGetAllAccounts() {
        List<AccountDTO> response = given()
            .contentType(ContentType.JSON)
        .when()
            .get()
        .then()
            .statusCode(Response.Status.OK.getStatusCode())
            .contentType(ContentType.JSON)
            .extract()
            .as(new TypeRef<List<AccountDTO>>() {});

        assertThat(response).hasSize(1);
        assertThat(response.getFirst().getAccountId()).isEqualTo(sampleAccount.getAccountId());
        assertThat(response.getFirst().getFinancialInstitution()).isEqualTo(sampleAccount.getFinancialInstitution());
        assertThat(response.getFirst().getAccountName()).isEqualTo(sampleAccount.getAccountName());
        assertThat(response.getFirst().getAccountType()).isEqualTo(sampleAccount.getAccountType());
        assertThat(response.getFirst().getInitialBalance()).isEqualTo(sampleAccount.getInitialBalance());
        assertThat(response.getFirst().getBalance()).isEqualTo(sampleAccount.getBalance());
        assertThat(response.getFirst().getCreatedAt()).isEqualTo(sampleAccount.getCreatedAt());
        assertThat(response.getFirst().getUpdatedAt()).isEqualTo(sampleAccount.getUpdatedAt());
    }

    @Test
    @DisplayName("Should delete an account")
    void shouldDeleteAccount() {
        given()
            .contentType(ContentType.JSON)
        .when()
            .delete(String.valueOf(sampleAccount.getAccountId()))
        .then()
            .statusCode(Response.Status.OK.getStatusCode());
    }
}