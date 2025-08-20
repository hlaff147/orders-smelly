package com.example.orders;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Teste de sanidade para verificar se o contexto Spring carrega corretamente
 */
@SpringBootTest
@ActiveProfiles("test")
class OrdersSmellyApplicationTests {

	@Test
	void contextLoads() {
		// Este teste verifica se todas as dependências estão configuradas corretamente
		// e se o contexto Spring consegue ser inicializado sem erros
	}

	@Test
	void mainMethodRuns() {
		// Verifica se o método main pode ser executado
		OrdersSmellyApplication.main(new String[]{});
	}
}
