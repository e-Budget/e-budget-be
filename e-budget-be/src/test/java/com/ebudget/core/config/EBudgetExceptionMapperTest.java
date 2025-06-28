package com.ebudget.core.config;

import com.ebudget.account.model.Account;
import com.ebudget.account.model.enums.AccountLogo;
import com.ebudget.account.model.enums.AccountType;
import com.ebudget.account.resource.request.NewAccountDTO;
import com.ebudget.core.exceptions.EntityNotFoundException;
import com.ebudget.core.response.ExceptionDTO;
import io.quarkus.hibernate.validator.runtime.jaxrs.ResteasyReactiveViolationException;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ValidationException;
import jakarta.validation.Validator;
import org.jboss.resteasy.reactive.RestResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
@DisplayName("e-Budget Exception Mapper")
class EBudgetExceptionMapperTest {
    @Inject
    EBudgetExceptionMapper eBudgetExceptionMapper;
    @Inject
    Validator validator;

    @Test
    @DisplayName("Should map an e-Budget Exception")
    void shouldMapEBudgetException() {
        // given
        UUID accountId = UUID.randomUUID();

        EntityNotFoundException entityNotFoundException = new EntityNotFoundException(Account.class, accountId);

        List<Map<String, String>> expectedExceptionDetails = List.of(Map.of(
                "key", "entityId",
                "value", accountId.toString()
        ));

        // when
        RestResponse<ExceptionDTO> restResponse = eBudgetExceptionMapper.mapEBudgetException(entityNotFoundException);

        // then
        assertThat(restResponse.getStatus()).isEqualTo(RestResponse.Status.NOT_FOUND.getStatusCode());
        assertThat(restResponse.getEntity().getException()).isEqualTo(EntityNotFoundException.class.getSimpleName());
        assertThat(restResponse.getEntity().getMessage()).isEqualTo("Account not found");
        assertThat(restResponse.getEntity().getDetails()).isEqualTo(expectedExceptionDetails);
    }

    @Test
    @DisplayName("Should map Validation Exception to custom response")
    void shouldMapValidationException() {
        // given
        List<Map<String, String>> expectedExceptionDetails = List.of(Map.of(
                "key", "accountName",
                "value", "must not be blank"
        ));

        Set<ConstraintViolation<NewAccountDTO>> violations = validator.validate(new NewAccountDTO(
                AccountLogo.NONE,
                null,
                AccountType.BANK_ACCOUNT,
                new BigDecimal("0.00")
        ));

        ValidationException validationException = new ResteasyReactiveViolationException(violations);

        // when
        RestResponse<ExceptionDTO> restResponse = eBudgetExceptionMapper.mapValidationException(validationException);

        // then
        assertThat(restResponse.getStatus()).isEqualTo(RestResponse.Status.BAD_REQUEST.getStatusCode());
        assertThat(restResponse.getEntity().getException()).isEqualTo(ValidationException.class.getSimpleName());
        assertThat(restResponse.getEntity().getMessage()).isEqualTo("Request contains validation errors");
        assertThat(restResponse.getEntity().getDetails()).isEqualTo(expectedExceptionDetails);
    }
}