package com.ebudget.income.resource;

import com.ebudget.account.model.Account;
import com.ebudget.account.model.enums.AccountLogo;
import com.ebudget.account.model.enums.AccountType;
import com.ebudget.account.repository.AccountRepository;
import com.ebudget.income.model.Income;
import com.ebudget.income.repository.IncomeRepository;
import com.ebudget.income.resource.request.NewIncomeDTO;
import com.ebudget.income.resource.response.IncomeDTO;
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
@DisplayName("Income Resource")
@TestHTTPEndpoint(IncomeResource.class)
class IncomeResourceTest {
    @Inject
    IncomeRepository incomeRepository;
    @Inject
    AccountRepository accountRepository;

    private Account sampleAccount;
    private Income sampleIncome;

    @BeforeEach
    @Transactional
    void setup() {
        sampleAccount = Account.builder()
                .accountLogo(AccountLogo.NONE)
                .accountName("accountName")
                .accountType(AccountType.BANK_ACCOUNT)
                .initialBalance(new BigDecimal("0.00"))
                .balance(new BigDecimal("0.00"))
                .build();

        accountRepository.persistAndFlush(sampleAccount);

        sampleIncome = Income.builder()
                .incomeDescription("incomeDescription")
                .amount(new BigDecimal("10.00"))
                .account(sampleAccount)
                .build();

        incomeRepository.persistAndFlush(sampleIncome);
    }

    @AfterEach
    @Transactional
    void destroy() {
        incomeRepository.deleteAll();
        accountRepository.deleteAll();
    }

    @Test
    @DisplayName("Should add an income")
    void shouldAddIncome() {
        NewIncomeDTO newIncomeDTO = new NewIncomeDTO(
                "incomeDescription",
                new BigDecimal("10.00"),
                sampleAccount.getAccountId()
        );

        IncomeDTO response = given()
            .contentType(ContentType.JSON)
            .body(newIncomeDTO)
        .when()
            .post()
        .then()
            .statusCode(Response.Status.CREATED.getStatusCode())
            .contentType(ContentType.JSON)
            .extract()
            .as(new TypeRef<IncomeDTO>() {});

        assertThat(response.getIncomeId()).isNotNull();
        assertThat(response.getIncomeId()).isInstanceOf(UUID.class);
        assertThat(response.getIncomeDescription()).isEqualTo(newIncomeDTO.incomeDescription());
        assertThat(response.getAmount()).isEqualTo(newIncomeDTO.amount());
        assertThat(response.getAccount().getAccountId()).isEqualTo(newIncomeDTO.accountId());
        assertThat(response.getCreatedAt()).isNotNull();
        assertThat(response.getCreatedAt()).isInstanceOf(LocalDateTime.class);
        assertThat(response.getUpdatedAt()).isNotNull();
        assertThat(response.getUpdatedAt()).isInstanceOf(LocalDateTime.class);
    }

    @Test
    @DisplayName("Should update an income")
    void shouldUpdateIncome() {
        NewIncomeDTO updateIncomeDTO = new NewIncomeDTO(
                "newIncomeDescription",
                new BigDecimal("150.00"),
                sampleAccount.getAccountId()
        );

        given()
            .contentType(ContentType.JSON)
            .body(updateIncomeDTO)
        .when()
            .put(String.valueOf(sampleIncome.getIncomeId()))
        .then()
            .statusCode(Response.Status.NO_CONTENT.getStatusCode());
    }

    @Test
    @DisplayName("Should get an income")
    void shouldGetIncome() {
        IncomeDTO response = given()
            .contentType(ContentType.JSON)
        .when()
            .get(String.valueOf(sampleIncome.getIncomeId()))
        .then()
            .statusCode(Response.Status.OK.getStatusCode())
            .contentType(ContentType.JSON)
            .extract()
            .as(new TypeRef<IncomeDTO>() {});

        assertThat(response.getIncomeId()).isEqualTo(sampleIncome.getIncomeId());
        assertThat(response.getIncomeDescription()).isEqualTo(sampleIncome.getIncomeDescription());
        assertThat(response.getAmount()).isEqualTo(sampleIncome.getAmount());
        assertThat(response.getAccount().getAccountId()).isEqualTo(sampleIncome.getAccount().getAccountId());
        assertThat(response.getCreatedAt()).isEqualTo(sampleIncome.getCreatedAt());
        assertThat(response.getUpdatedAt()).isEqualTo(sampleIncome.getUpdatedAt());
    }

    @Test
    @DisplayName("Should get all incomes")
    void shouldGetIncomes() {
        List<IncomeDTO> response = given()
            .contentType(ContentType.JSON)
        .when()
            .get()
        .then()
            .statusCode(Response.Status.OK.getStatusCode())
            .contentType(ContentType.JSON)
            .extract()
            .as(new TypeRef<List<IncomeDTO>>() {});

        assertThat(response).hasSize(1);
        assertThat(response.getFirst().getIncomeId()).isEqualTo(sampleIncome.getIncomeId());
        assertThat(response.getFirst().getIncomeDescription()).isEqualTo(sampleIncome.getIncomeDescription());
        assertThat(response.getFirst().getAmount()).isEqualTo(sampleIncome.getAmount());
        assertThat(response.getFirst().getAccount().getAccountId()).isEqualTo(sampleIncome.getAccount().getAccountId());
        assertThat(response.getFirst().getCreatedAt()).isEqualTo(sampleIncome.getCreatedAt());
        assertThat(response.getFirst().getUpdatedAt()).isEqualTo(sampleIncome.getUpdatedAt());
    }

    @Test
    @DisplayName("Should delete an income")
    void shouldDeleteIncome() {
        given()
            .contentType(ContentType.JSON)
        .when()
            .delete(String.valueOf(sampleIncome.getIncomeId()))
        .then()
            .statusCode(Response.Status.OK.getStatusCode());
    }
}