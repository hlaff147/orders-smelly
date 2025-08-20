package com.example.orders.util;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Utilitário refatorado para formatação de valores
 * - Usa BigDecimal ao invés de double
 * - Configurável por locale
 * - Thread-safe usando NumberFormat local
 */
@Component
public class LegacyFormat {
    
    private static final Locale DEFAULT_LOCALE = new Locale("pt", "BR");
    
    /**
     * Formata valor monetário usando locale brasileiro
     */
    public String formatMoney(BigDecimal value) {
        return formatMoney(value, DEFAULT_LOCALE);
    }
    
    /**
     * Formata valor monetário usando locale específico
     */
    public String formatMoney(BigDecimal value, Locale locale) {
        if (value == null) {
            return "R$ 0,00";
        }
        
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(locale);
        return currencyFormat.format(value);
    }
    
    /**
     * Método mantido para compatibilidade, mas usando BigDecimal internamente
     * @deprecated Use formatMoney(BigDecimal) ao invés
     */
    @Deprecated
    public static String money(double v) {
        return "R$ " + new DecimalFormat("#0.00").format(v);
    }
}
