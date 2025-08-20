package com.example.orders.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Testes unitários para LegacyFormat
 */
class LegacyFormatTest {

    private LegacyFormat legacyFormat;

    @BeforeEach
    void setUp() {
        legacyFormat = new LegacyFormat();
    }

    @Test
    void shouldFormatMoneyWithDefaultLocale() {
        // Given
        BigDecimal value = new BigDecimal("123.45");

        // When
        String formatted = legacyFormat.formatMoney(value);

        // Then
        assertThat(formatted).startsWith("R$").contains("123").contains("45");
    }

    @Test
    void shouldFormatMoneyWithBrazilianLocale() {
        // Given
        BigDecimal value = new BigDecimal("1234.56");
        Locale locale = new Locale("pt", "BR");

        // When
        String formatted = legacyFormat.formatMoney(value, locale);

        // Then
        assertThat(formatted).startsWith("R$").contains("1").contains("234").contains("56");
    }

    @Test
    void shouldFormatZeroValue() {
        // Given
        BigDecimal value = BigDecimal.ZERO;

        // When
        String formatted = legacyFormat.formatMoney(value);

        // Then
        assertThat(formatted).startsWith("R$").contains("0");
    }

    @Test
    void shouldFormatSmallValue() {
        // Given
        BigDecimal value = new BigDecimal("0.01");

        // When
        String formatted = legacyFormat.formatMoney(value);

        // Then
        assertThat(formatted).startsWith("R$").contains("0").contains("01");
    }

    @Test
    void shouldFormatLargeValue() {
        // Given
        BigDecimal value = new BigDecimal("999999.99");

        // When
        String formatted = legacyFormat.formatMoney(value);

        // Then
        assertThat(formatted).startsWith("R$").contains("999").contains("99");
    }

    @Test
    void shouldHandleNullValue() {
        // When
        String formatted = legacyFormat.formatMoney(null);

        // Then
        assertThat(formatted).isEqualTo("R$ 0,00");
    }

    @Test
    void shouldFormatWithUSLocale() {
        // Given
        BigDecimal value = new BigDecimal("1234.56");
        Locale locale = Locale.US;

        // When
        String formatted = legacyFormat.formatMoney(value, locale);

        // Then
        assertThat(formatted).startsWith("$").contains("1,234.56");
    }

    @Test
    void shouldHandleNegativeValues() {
        // Given
        BigDecimal value = new BigDecimal("-100.50");

        // When
        String formatted = legacyFormat.formatMoney(value);

        // Then
        assertThat(formatted).contains("-").contains("100,50");
    }

    @Test
    void shouldFormatValueWithManyDecimals() {
        // Given
        BigDecimal value = new BigDecimal("123.456789");

        // When
        String formatted = legacyFormat.formatMoney(value);

        // Then
        // Deve arredondar para 2 casas decimais
        assertThat(formatted).startsWith("R$").contains("123").contains("46");
    }

    @Test
    void shouldFormatIntegerValue() {
        // Given
        BigDecimal value = new BigDecimal("100");

        // When
        String formatted = legacyFormat.formatMoney(value);

        // Then
        assertThat(formatted).startsWith("R$").contains("100");
    }

    @Test
    @SuppressWarnings("deprecation")
    void shouldMaintainLegacyMethodCompatibility() {
        // Given
        double value = 123.45;

        // When
        String formatted = LegacyFormat.money(value);

        // Then
        assertThat(formatted).contains("123").contains("45");
    }

    @Test
    @SuppressWarnings("deprecation")
    void shouldHandleDoublePrecisionIssuesInLegacyMethod() {
        // Given
        double value = 0.1 + 0.2; // Problema conhecido de precisão com double

        // When
        String formatted = LegacyFormat.money(value);

        // Then
        // O método legacy pode ter problemas de precisão, mas não deve quebrar
        assertThat(formatted).contains("R$").matches(".*[03].*[03].*");
    }

    @Test
    void shouldBeThreadSafeWithMultipleThreads() throws InterruptedException {
        // Given
        final BigDecimal value = new BigDecimal("123.45");
        final int numberOfThreads = 10;
        final Thread[] threads = new Thread[numberOfThreads];
        final String[] results = new String[numberOfThreads];

        // When - múltiplas threads formatando simultaneamente
        for (int i = 0; i < numberOfThreads; i++) {
            final int index = i;
            threads[i] = new Thread(() -> {
                results[index] = legacyFormat.formatMoney(value);
            });
            threads[i].start();
        }

        // Aguarda todas as threads terminarem
        for (Thread thread : threads) {
            thread.join();
        }

        // Then - todos os resultados devem ser iguais
        for (String result : results) {
            assertThat(result).startsWith("R$").contains("123").contains("45");
        }
    }

    @Test
    void shouldHandleVeryLargeNumbers() {
        // Given
        BigDecimal value = new BigDecimal("999999999999.99");

        // When
        String formatted = legacyFormat.formatMoney(value);

        // Then
        assertThat(formatted).contains("999.999.999.999,99");
    }

    @Test
    void shouldFormatWithDifferentScales() {
        // Given
        BigDecimal value1 = new BigDecimal("100.5"); // 1 casa decimal
        BigDecimal value2 = new BigDecimal("100.50"); // 2 casas decimais
        BigDecimal value3 = new BigDecimal("100.500"); // 3 casas decimais

        // When
        String formatted1 = legacyFormat.formatMoney(value1);
        String formatted2 = legacyFormat.formatMoney(value2);
        String formatted3 = legacyFormat.formatMoney(value3);

        // Then - todos devem ser formatados com 2 casas decimais
        assertThat(formatted1).startsWith("R$").contains("100").contains("50");
        assertThat(formatted2).startsWith("R$").contains("100").contains("50");
        assertThat(formatted3).startsWith("R$").contains("100").contains("50");
    }
}
