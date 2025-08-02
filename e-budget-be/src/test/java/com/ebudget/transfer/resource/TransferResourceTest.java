package com.ebudget.transfer.resource;

import com.ebudget.account.model.Account;
import com.ebudget.account.model.enums.FinancialInstitution;
import com.ebudget.account.model.enums.AccountType;
import com.ebudget.account.repository.AccountRepository;
import com.ebudget.transfer.model.Transfer;
import com.ebudget.transfer.repository.TransferRepository;
import com.ebudget.transfer.resource.request.NewTransferDTO;
import com.ebudget.transfer.resource.response.TransferDTO;
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
@DisplayName("Transfer Resource")
@TestHTTPEndpoint(TransferResource.class)
class TransferResourceTest {
    @Inject
    TransferRepository transferRepository;
    @Inject
    AccountRepository accountRepository;

    private Account sampleFromAccount;
    private Account sampleToAccount;
    private Transfer sampleTransfer;

    @BeforeEach
    @Transactional
    void setup() {
        sampleFromAccount = Account.builder()
                .financialInstitution(FinancialInstitution.NONE)
                .accountName("fromAccountName")
                .accountType(AccountType.BANK_ACCOUNT)
                .initialBalance(new BigDecimal("100.00"))
                .balance(new BigDecimal("100.00"))
                .build();

        sampleToAccount = Account.builder()
                .financialInstitution(FinancialInstitution.NONE)
                .accountName("toAccountName")
                .accountType(AccountType.BANK_ACCOUNT)
                .initialBalance(new BigDecimal("0.00"))
                .balance(new BigDecimal("0.00"))
                .build();

        accountRepository.persistAndFlush(sampleFromAccount);
        accountRepository.persistAndFlush(sampleToAccount);

        sampleTransfer = Transfer.builder()
                .amount(new BigDecimal("100.00"))
                .fromAccount(sampleFromAccount)
                .toAccount(sampleToAccount)
                .build();

        transferRepository.persistAndFlush(sampleTransfer);
    }

    @AfterEach
    @Transactional
    void destroy() {
        transferRepository.deleteAll();
        accountRepository.deleteAll();
    }

    @Test
    @DisplayName("Should add an account")
    void shouldAddTransfer() {
        NewTransferDTO newTransferDTO = new NewTransferDTO(
                "transferDescription",
                new BigDecimal("10.00"),
                sampleFromAccount.getAccountId(),
                sampleToAccount.getAccountId()
        );

        TransferDTO response = given()
            .contentType(ContentType.JSON)
            .body(newTransferDTO)
        .when()
            .post()
        .then()
            .statusCode(Response.Status.CREATED.getStatusCode())
            .contentType(ContentType.JSON)
            .extract()
            .as(new TypeRef<TransferDTO>() {});

        assertThat(response.getTransferId()).isNotNull();
        assertThat(response.getTransferId()).isInstanceOf(UUID.class);
        assertThat(response.getTransferDescription()).isEqualTo(newTransferDTO.transferDescription());
        assertThat(response.getAmount()).isEqualTo(newTransferDTO.amount());
        assertThat(response.getFromAccount().getAccountId()).isEqualTo(newTransferDTO.fromAccount());
        assertThat(response.getToAccount().getAccountId()).isEqualTo(newTransferDTO.toAccount());
        assertThat(response.getCreatedAt()).isNotNull();
        assertThat(response.getCreatedAt()).isInstanceOf(LocalDateTime.class);
        assertThat(response.getUpdatedAt()).isNotNull();
        assertThat(response.getUpdatedAt()).isInstanceOf(LocalDateTime.class);
    }

    @Test
    @DisplayName("Should delete a transfer")
    void shouldDeleteTransfer() {
        given()
            .contentType(ContentType.JSON)
        .when()
            .delete(String.valueOf(sampleTransfer.getTransferId()))
        .then()
            .statusCode(Response.Status.OK.getStatusCode());
    }

    @Test
    @DisplayName("Should get a transfer")
    void shouldGetTransfer() {
        TransferDTO response = given()
            .contentType(ContentType.JSON)
        .when()
            .get(String.valueOf(sampleTransfer.getTransferId()))
        .then()
            .statusCode(Response.Status.OK.getStatusCode())
            .contentType(ContentType.JSON)
            .extract()
            .as(new TypeRef<TransferDTO>() {});

        assertThat(response.getTransferId()).isEqualTo(sampleTransfer.getTransferId());
        assertThat(response.getTransferDescription()).isEqualTo(sampleTransfer.getTransferDescription());
        assertThat(response.getAmount()).isEqualTo(sampleTransfer.getAmount());
        assertThat(response.getFromAccount().getAccountId()).isEqualTo(sampleTransfer.getFromAccount().getAccountId());
        assertThat(response.getToAccount().getAccountId()).isEqualTo(sampleTransfer.getToAccount().getAccountId());
        assertThat(response.getCreatedAt()).isEqualTo(sampleTransfer.getCreatedAt());
        assertThat(response.getUpdatedAt()).isEqualTo(sampleTransfer.getUpdatedAt());
    }

    @Test
    @DisplayName("Should get all transfers")
    void getTransfers() {
        List<TransferDTO> response = given()
            .contentType(ContentType.JSON)
        .when()
            .get()
        .then()
            .statusCode(Response.Status.OK.getStatusCode())
            .contentType(ContentType.JSON)
            .extract()
            .as(new TypeRef<List<TransferDTO>>() {});

        assertThat(response).hasSize(1);
        assertThat(response.getFirst().getTransferId()).isEqualTo(sampleTransfer.getTransferId());
        assertThat(response.getFirst().getTransferDescription()).isEqualTo(sampleTransfer.getTransferDescription());
        assertThat(response.getFirst().getAmount()).isEqualTo(sampleTransfer.getAmount());
        assertThat(response.getFirst().getFromAccount().getAccountId()).isEqualTo(sampleTransfer.getFromAccount().getAccountId());
        assertThat(response.getFirst().getToAccount().getAccountId()).isEqualTo(sampleTransfer.getToAccount().getAccountId());
        assertThat(response.getFirst().getCreatedAt()).isEqualTo(sampleTransfer.getCreatedAt());
        assertThat(response.getFirst().getUpdatedAt()).isEqualTo(sampleTransfer.getUpdatedAt());
    }
}