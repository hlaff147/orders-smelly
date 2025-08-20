package com.example.orders.util;


import java.text.DecimalFormat;

// BAD: utilitário fixo e não internacionalizável
public class LegacyFormat {
    public static String money(double v) {
        return "R$ " + new DecimalFormat("#0.00").format(v);
    }
}
