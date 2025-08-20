package com.example.orders.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Testes unitários para CreateOrderRequest incluindo validações Bean Validation
 */
class CreateOrderRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldCreateValidRequest() {
        // Given
        CreateOrderRequest request = new CreateOrderRequest(
                "João Silva",
                new BigDecimal("100.50"),
                "15-12-2024"
        );

        // When
        Set<ConstraintViolation<CreateOrderRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).isEmpty();
        assertThat(request.getCustomerName()).isEqualTo("João Silva");
        assertThat(request.getTotal()).isEqualTo(new BigDecimal("100.50"));
        assertThat(request.getOrderDate()).isEqualTo("15-12-2024");
    }

    @Test
    void shouldCreateEmptyRequest() {
        // When
        CreateOrderRequest request = new CreateOrderRequest();

        // Then
        assertThat(request.getCustomerName()).isNull();
        assertThat(request.getTotal()).isNull();
        assertThat(request.getOrderDate()).isNull();
    }

    @Test
    void shouldSetAndGetAllFields() {
        // Given
        CreateOrderRequest request = new CreateOrderRequest();

        // When
        request.setCustomerName("Maria Santos");
        request.setTotal(new BigDecimal("250.75"));
        request.setOrderDate("16-12-2024");

        // Then
        assertThat(request.getCustomerName()).isEqualTo("Maria Santos");
        assertThat(request.getTotal()).isEqualTo(new BigDecimal("250.75"));
        assertThat(request.getOrderDate()).isEqualTo("16-12-2024");
    }

    @Test
    void shouldFailValidationWhenCustomerNameIsNull() {
        // Given
        CreateOrderRequest request = new CreateOrderRequest(
                null,
                new BigDecimal("100.50"),
                "15-12-2024"
        );

        // When
        Set<ConstraintViolation<CreateOrderRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("Nome do cliente é obrigatório");
    }

    @Test
    void shouldFailValidationWhenCustomerNameIsEmpty() {
        // Given
        CreateOrderRequest request = new CreateOrderRequest(
                "",
                new BigDecimal("100.50"),
                "15-12-2024"
        );

        // When
        Set<ConstraintViolation<CreateOrderRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("Nome do cliente é obrigatório");
    }

    @Test
    void shouldFailValidationWhenCustomerNameIsBlank() {
        // Given
        CreateOrderRequest request = new CreateOrderRequest(
                "   ",
                new BigDecimal("100.50"),
                "15-12-2024"
        );

        // When
        Set<ConstraintViolation<CreateOrderRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("Nome do cliente é obrigatório");
    }

    @Test
    void shouldFailValidationWhenTotalIsNull() {
        // Given
        CreateOrderRequest request = new CreateOrderRequest(
                "João Silva",
                null,
                "15-12-2024"
        );

        // When
        Set<ConstraintViolation<CreateOrderRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("Total é obrigatório");
    }

    @Test
    void shouldFailValidationWhenTotalIsZero() {
        // Given
        CreateOrderRequest request = new CreateOrderRequest(
                "João Silva",
                BigDecimal.ZERO,
                "15-12-2024"
        );

        // When
        Set<ConstraintViolation<CreateOrderRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("Total deve ser positivo");
    }

    @Test
    void shouldFailValidationWhenTotalIsNegative() {
        // Given
        CreateOrderRequest request = new CreateOrderRequest(
                "João Silva",
                new BigDecimal("-10.00"),
                "15-12-2024"
        );

        // When
        Set<ConstraintViolation<CreateOrderRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("Total deve ser positivo");
    }

    @Test
    void shouldFailValidationWhenOrderDateIsNull() {
        // Given
        CreateOrderRequest request = new CreateOrderRequest(
                "João Silva",
                new BigDecimal("100.50"),
                null
        );

        // When
        Set<ConstraintViolation<CreateOrderRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("Data é obrigatória");
    }

    @Test
    void shouldFailValidationWhenOrderDateIsEmpty() {
        // Given
        CreateOrderRequest request = new CreateOrderRequest(
                "João Silva",
                new BigDecimal("100.50"),
                ""
        );

        // When
        Set<ConstraintViolation<CreateOrderRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("Data é obrigatória");
    }

    @Test
    void shouldFailValidationWithMultipleErrors() {
        // Given
        CreateOrderRequest request = new CreateOrderRequest(
                "",
                BigDecimal.ZERO,
                ""
        );

        // When
        Set<ConstraintViolation<CreateOrderRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).hasSize(3);
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .containsExactlyInAnyOrder(
                        "Nome do cliente é obrigatório",
                        "Total deve ser positivo",
                        "Data é obrigatória"
                );
    }
}
