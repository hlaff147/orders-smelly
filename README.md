# Orders Smelly - Projeto de Refatoração

## 📋 Sobre o Projeto

Este projeto foi criado **intencionalmente com bad patterns e práticas ruins** para servir como exercício de refatoração e treinamento de boas práticas de desenvolvimento.

O código inicial foi commitado propositalmente com problemas de design, segurança e arquitetura para demonstrar como identificar e corrigir anti-patterns comuns em aplicações Spring Boot.

## 🎯 Objetivo

Demonstrar a transformação de um código problemático em uma aplicação bem estruturada, seguindo as melhores práticas de desenvolvimento Java/Spring Boot.

## 🚨 Problemas Identificados no Código Original

### 1. **Injeção de Dependência Inadequada**
**Problema:** Service sem anotação `@Service` e Controller instanciando service manualmente
```java
// ❌ ANTES
public class OrderService { // sem @Service
    private final OrderRepository repo = new OrderRepository(); // instanciação manual
}

@RestController
public class OrderController {
    private OrderService service = new OrderService(); // sem IoC
}
```

**Solução:** Uso correto de anotações Spring e injeção de dependência
```java
// ✅ DEPOIS
@Service
public class OrderService {
    private final OrderRepository orderRepository;
    
    public OrderService(OrderRepository orderRepository, ...) { // injeção por construtor
        this.orderRepository = orderRepository;
    }
}

@RestController
public class OrderController {
    private final OrderService orderService;
    
    public OrderController(OrderService orderService) { // injeção por construtor
        this.orderService = orderService;
    }
}
```

### 2. **Thread Safety Crítico**
**Problema:** SimpleDateFormat estático (não thread-safe) e estado global compartilhado
```java
// ❌ ANTES
private static final SimpleDateFormat SDF = new SimpleDateFormat("dd-MM-yyyy"); // NÃO thread-safe
private static final Map<Integer, Order> DB = new HashMap<>(); // NÃO thread-safe
private static int SEQ = 1; // NÃO thread-safe
```

**Solução:** Uso de classes thread-safe e LocalDate
```java
// ✅ DEPOIS
private final Map<Long, Order> database = new ConcurrentHashMap<>(); // thread-safe
private final AtomicLong sequence = new AtomicLong(1); // thread-safe
private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy"); // thread-safe
```

### 3. **Tratamento de Erros Inadequado**
**Problema:** Exceções engolidas e retorno de erro como String com status 200
```java
// ❌ ANTES
try {
    Date d = SDF.parse(dateStr);
} catch (ParseException | InterruptedException e) {
    // engole a exceção - MUITO PERIGOSO
}

public String make(@RequestParam String customerName...) {
    if (customerName == null || customerName.isEmpty()) {
        return "error: invalid customer"; // erro retornado como 200
    }
}
```

**Solução:** Tratamento adequado com status HTTP corretos
```java
// ✅ DEPOIS
public ResponseEntity<ApiResponse<OrderResponse>> createOrder(@Valid @RequestBody CreateOrderRequest request) {
    try {
        OrderResponse order = orderService.createOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Pedido criado com sucesso", order));
    } catch (IllegalArgumentException e) {
        return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
    }
}
```

### 4. **Tipos Inadequados**
**Problema:** Uso de tipos primitivos inadequados para domínio de negócio
```java
// ❌ ANTES
private double total; // impreciso para dinheiro
private String date; // não type-safe
private String status = "NEW"; // propenso a erros
```

**Solução:** Tipos apropriados para cada domínio
```java
// ✅ DEPOIS
private BigDecimal total; // preciso para valores monetários
private LocalDate orderDate; // type-safe para datas
private OrderStatus status = OrderStatus.NEW; // enum type-safe
```

### 5. **Encapsulamento Quebrado**
**Problema:** Campos públicos em DTOs
```java
// ❌ ANTES
public class ApplyCouponRequest {
    public Integer orderId; // público - quebra encapsulamento
    public String coupon;
}
```

**Solução:** Encapsulamento correto com validações
```java
// ✅ DEPOIS
public class ApplyCouponRequest {
    @NotNull(message = "ID do pedido é obrigatório")
    @Positive(message = "ID do pedido deve ser positivo")
    private Long orderId;
    
    // getters e setters apropriados
}
```

### 6. **Validação Ausente**
**Problema:** Uso inseguro de Optional.get() e falta de validações
```java
// ❌ ANTES
Order o = opt.get(); // pode gerar NoSuchElementException
```

**Solução:** Validações robustas
```java
// ✅ DEPOIS
Order order = orderRepository.findById(orderId)
        .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado com ID: " + orderId));
```

### 7. **Responsabilidades Misturadas**
**Problema:** Controller fazendo validação de negócio
```java
// ❌ ANTES
@PostMapping("/makeOrder")
public String make(@RequestParam String customerName...) {
    if (customerName == null || customerName.isEmpty()) { // validação no controller
        return "error: invalid customer";
    }
}
```

**Solução:** Separação clara de responsabilidades
```java
// ✅ DEPOIS
// Controller: apenas coordenação
@PostMapping
public ResponseEntity<ApiResponse<OrderResponse>> createOrder(@Valid @RequestBody CreateOrderRequest request) {
    OrderResponse order = orderService.createOrder(request); // delegação para service
    return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(order));
}

// Validações: no DTO via anotações
@NotBlank(message = "Nome do cliente é obrigatório")
private String customerName;
```

### 8. **Vazamento de Entidades**
**Problema:** Exposição direta de entidades nos endpoints
```java
// ❌ ANTES
@GetMapping("/orders")
public List<Order> all() {
    return service.getRepo().findAll(); // vaza entidade interna
}
```

**Solução:** Uso de DTOs para camada de apresentação
```java
// ✅ DEPOIS
@GetMapping
public ResponseEntity<ApiResponse<List<OrderResponse>>> getAllOrders() {
    List<OrderResponse> orders = orderService.getAllOrders(); // DTOs
    return ResponseEntity.ok(ApiResponse.success(orders));
}
```

## 🏗️ Arquitetura Refatorada

A arquitetura mantém as camadas solicitadas, mas com implementação adequada:

```
src/main/java/com/example/orders/
├── controller/          # Controllers REST
│   └── OrderController.java
├── dto/                 # DTOs para requests/responses  
│   ├── CreateOrderRequest.java
│   ├── OrderResponse.java
│   └── ApiResponse.java
├── mapper/              # Conversão DTO ↔ Entity
│   └── OrderMapper.java
├── model/               # Entidades de domínio
│   ├── Order.java
│   ├── OrderStatus.java (enum)
│   ├── ApplyCouponRequest.java
│   └── FulfillRequest.java
├── repository/          # Acesso a dados
│   └── OrderRepository.java
├── service/             # Lógica de negócio
│   └── OrderService.java
└── util/                # Utilitários
    └── LegacyFormat.java
```

## 🔧 Principais Melhorias Implementadas

### 1. **Injeção de Dependência**
- ✅ Uso correto de anotações Spring (`@Service`, `@Repository`, `@Component`)
- ✅ Injeção por construtor (mais segura que por campo)
- ✅ Remoção de anotações `@Autowired` desnecessárias

### 2. **Thread Safety**
- ✅ `ConcurrentHashMap` para estado compartilhado
- ✅ `AtomicLong` para geração de IDs
- ✅ `DateTimeFormatter` thread-safe
- ✅ Eliminação de estado estático mutável

### 3. **Tipos de Dados Apropriados**
- ✅ `BigDecimal` para valores monetários
- ✅ `LocalDate` para datas
- ✅ `Enum` para status
- ✅ `Long` para IDs (preparado para banco real)

### 4. **Validação Robusta**
- ✅ Bean Validation (`@Valid`, `@NotNull`, `@Positive`)
- ✅ Tratamento seguro de `Optional`
- ✅ Validações de regra de negócio no service

### 5. **Tratamento de Erros**
- ✅ Status HTTP adequados (400, 404, 409, 500)
- ✅ Responses estruturados (`ApiResponse<T>`)
- ✅ Logging apropriado
- ✅ Exceções específicas para cada caso

### 6. **Separação de Responsabilidades**
- ✅ Controller: apenas coordenação HTTP
- ✅ Service: lógica de negócio
- ✅ Repository: acesso a dados
- ✅ Mapper: conversão DTO ↔ Entity
- ✅ DTOs: validação e transferência de dados

### 7. **Logging e Observabilidade**
- ✅ SLF4J para logging estruturado
- ✅ Levels apropriados (INFO, WARN, ERROR)
- ✅ Remoção de `System.out.println`

### 8. **Flexibilidade e Manutenibilidade**
- ✅ Formatação monetária configurável por locale
- ✅ Lógica de cupons extensível e testável
- ✅ Métodos pequenos e com responsabilidade única

## 🚀 Como Executar

```bash
# Compilar o projeto
./mvnw clean compile

# Executar a aplicação
./mvnw spring-boot:run

# Executar testes
./mvnw test
```

## 📚 Endpoints da API

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| `POST` | `/api/orders` | Criar pedido |
| `GET` | `/api/orders` | Listar todos os pedidos |
| `GET` | `/api/orders/{id}` | Buscar pedido por ID |
| `POST` | `/api/orders/apply-coupon` | Aplicar cupom |
| `POST` | `/api/orders/fulfill` | Processar entrega |

### Exemplos de Uso

**Criar Pedido:**
```json
POST /api/orders
{
  "customerName": "João Silva",
  "total": 100.50,
  "orderDate": "15-12-2024"
}
```

**Aplicar Cupom:**
```json
POST /api/orders/apply-coupon
{
  "orderId": 1,
  "coupon": "OFF10"
}
```

## 🧪 Exemplos de Cupons Suportados

- `OFF10`: 10% de desconto
- `OFF25`: 25% de desconto  
- `VALOR15`: R$ 15,00 de desconto fixo

## 📝 Lições Aprendidas

Este exercício de refatoração demonstra a importância de:

1. **Code Review**: Muitos problemas poderiam ter sido evitados com revisão adequada
2. **Testes**: Testes automatizados ajudam a identificar problemas cedo
3. **Princípios SOLID**: Especialmente Single Responsibility e Dependency Inversion
4. **Thread Safety**: Fundamental em aplicações multi-thread
5. **Tratamento de Erros**: Essencial para aplicações robustas
6. **Tipos Apropriados**: Evita bugs sutis e melhora a expressividade
7. **Separação de Responsabilidades**: Facilita manutenção e testes

## 🔍 Próximos Passos Recomendados

- [ ] Implementar testes unitários e de integração
- [ ] Adicionar documentação OpenAPI/Swagger
- [ ] Implementar cache para consultas frequentes
- [ ] Adicionar métricas e monitoring
- [ ] Implementar persistência real (JPA/Database)
- [ ] Adicionar autenticação e autorização
- [ ] Implementar circuit breaker para resiliência

---

**Nota:** Este projeto serve como exemplo didático de como NÃO escrever código inicialmente, e como refatorar adequadamente seguindo as melhores práticas do ecossistema Spring Boot.
