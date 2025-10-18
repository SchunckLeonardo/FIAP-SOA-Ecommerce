# E-commerce Product API

A Spring Boot REST API for managing products in an e-commerce platform.

# Team
- Leonardo Schunck Rainha - 99902
- Kayky Oliveira Schunck - 99756
- Ricardo Augusto de Matos Filho - 95906
- Felipe Voidela Toledo - 98595
- Paulo Lopes Junior - 551137

## Technologies

- Kotlin 1.8
- Spring Boot 3.x
- Spring Data JPA
- Gradle
- PostgreSQL
- Jakarta Validation

## Setup & Running

1. Clone the repository
2. Run docker-compose `docker compose up -d`
3. Configure database in `application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/ecommerce
spring.datasource.username=your_username
spring.datasource.password=your_password
```
4. Run: `./gradlew bootRun`

## Link Postman
[Postman Collection](https://app.getpostman.com/join-team?invite_code=722c6c34bfe18ba768dbf01606de03f7d7c8b526d1f78d5984aeb109e922cc9f&target_code=a7a729a2e4f49b1be6f46e6de30b715a)

## API Endpoints

### Register Product
```http
POST /v1/products
```
**Request:**
```json
{
  "name": "Smartphone XYZ",
  "description": "Latest model",
  "category": "TECHNOLOGY",
  "price": 999.99,
  "amountAvailable": 50
}
```
**Response:** `201 Created`
```json
{
  "id": "123e4567-e89b-12d3-a456-426614174000",
  "name": "Smartphone XYZ",
  "description": "Latest model",
  "category": "TECHNOLOGY",
  "price": 999.99,
  "amountAvailable": 50
}
```

### List Products
```http
GET /v1/products?page=0&size=10&productName=phone&productCategory=TECHNOLOGY
```
**Response:** `200 OK`
```json
[
  {
    "id": "123e4567-e89b-12d3-a456-426614174000",
    "name": "Smartphone XYZ",
    "category": "TECHNOLOGY",
    "price": 999.99,
    "amountAvailable": 50
  }
]
```

### Get Product
```http
GET /v1/products/{id}
```
**Response:** `200 OK`
```json
{
  "id": "123e4567-e89b-12d3-a456-426614174000",
  "name": "Smartphone XYZ",
  "description": "Latest model",
  "category": "TECHNOLOGY",
  "price": 999.99,
  "amountAvailable": 50
}
```

### Update Product
```http
PUT /v1/products/{id}
```
**Request:**
```json
{
  "price": 899.99,
  "amountAvailable": 45
}
```
**Response:** `204 No Content`

### Delete Product
```http
DELETE /v1/products/{id}
```
**Response:** `204 No Content`

## Error Handling

The API uses standard HTTP status codes and returns error responses in the following format:

```json
{
  "message": "Error description",
  "status": 400,
  "errors": [
    {
      "error": "Field specific error",
      "field": "fieldName"
    }
  ]
}
```

# Diagram

![E-commerce Product API Diagram](./docs/images/diagram.png)

---

## Descrição do Projeto (PT-BR)
Uma API REST desenvolvida com Spring Boot e Kotlin para gerenciar o catálogo de produtos de um e-commerce, com autenticação JWT, documentação via OpenAPI/Swagger, persistência em PostgreSQL e migrações com Flyway. O projeto contempla:
- Cadastro, consulta, listagem, atualização e remoção de produtos
- Autenticação e registro de clientes (login/registro) para obtenção de token JWT
- Validações de entrada (Jakarta Validation) e tratamento padronizado de erros
- Documentação interativa via Swagger UI

## Pré-requisitos
- JDK 21
- Docker e Docker Compose (para banco Postgres)
- Acesso à internet para baixar dependências via Gradle Wrapper (não é necessário ter Gradle instalado)

## Instruções de Execução
Você pode executar a aplicação de duas formas: com Docker Compose para subir o banco e rodando a aplicação localmente com o Gradle Wrapper.

1) Subir o banco de dados com Docker Compose
- No diretório do projeto, execute:
  - Windows (PowerShell): docker compose up -d
  - Linux/Mac: docker compose up -d
- Isso iniciará um container PostgreSQL de acordo com o arquivo docker-compose.yml.

2) Configurar a aplicação
- A configuração padrão já está em src/main/resources/application.yml:
  - URL do banco: jdbc:postgresql://localhost:5432/ecommerce
  - Usuário: postgres | Senha: postgres
  - Porta da API: 8080
  - JWT secret: variável de ambiente JWT_SECRET_KEY (padrão test-secret-default)
- Opcionalmente, você pode sobrescrever propriedades via variáveis de ambiente. Exemplo (PowerShell):
  - $env:JWT_SECRET_KEY="minha-chave-secreta-super-segura"

3) Rodar a aplicação
- Windows (PowerShell): .\gradlew.bat bootRun
- Linux/Mac: ./gradlew bootRun
- A API iniciará em http://localhost:8080

4) Acessar a documentação (Swagger/OpenAPI)
- Swagger UI: http://localhost:8080/swagger-ui/index.html
- OpenAPI JSON: http://localhost:8080/v3/api-docs

## Como acessar a API (Fluxo de Autenticação)
Alguns endpoints podem exigir autenticação via Bearer Token (JWT).

1) Registro de cliente
- POST http://localhost:8080/v1/auth/register
- Exemplo de corpo:
```
{
  "name": "Maria Silva",
  "email": "maria@exemplo.com",
  "password": "senhaSegura123"
}
```
- Retorno: 201 Created

2) Login
- POST http://localhost:8080/v1/auth/login
- Exemplo de corpo:
```
{
  "email": "maria@exemplo.com",
  "password": "senhaSegura123"
}
```
- Resposta:
```
{
  "token": "<jwt>",
  "type": "Bearer"
}
```
- Utilize o token na autorização: Authorization: Bearer <jwt>

3) Exemplo de requisição autenticada (cURL)
```
curl -H "Authorization: Bearer <jwt>" http://localhost:8080/v1/products?page=0&size=10
```

## Como rodar os testes
- Todos os testes (unitários e de integração):
  - Windows (PowerShell): .\gradlew.bat test
  - Linux/Mac: ./gradlew test
- Relatório de testes (HTML) será gerado em build/reports/tests/test/index.html
- Observações:
  - Os testes de integração utilizam H2 em memória por padrão (conforme dependências). Alguns cenários podem usar Testcontainers; nesse caso, é necessário Docker em execução.

## Dicas de Troubleshooting
- Porta 5432 ocupada: ajuste a porta do Postgres no docker-compose.yml e em application.yml.
- Erro de autenticação JWT: defina JWT_SECRET_KEY antes de iniciar a aplicação.
- Não consegue acessar Swagger UI? Verifique se a aplicação está na porta 8080 e se não há proxy bloqueando.

## Tecnologias utilizadas (detalhado)
- Kotlin 1.9.x
- Spring Boot 3.5.x (Web, Validation, Data JPA, Security, OAuth2 Resource Server)
- JWT (com.auth0:java-jwt)
- Springdoc OpenAPI (Swagger UI)
- PostgreSQL (runtime)
- Flyway (migração de banco)
- JUnit 5, Spring Boot Test, MockK, SpringMockK
- H2 (testes), Testcontainers (opcional)
- Gradle Wrapper

## Exemplos rápidos de uso (produtos)
1) Criar produto
```
POST /v1/products
{
  "name": "Smartphone XYZ",
  "description": "Latest model",
  "category": "TECHNOLOGY",
  "price": 999.99,
  "amountAvailable": 50
}
```

2) Listar produtos
```
GET /v1/products?page=0&size=10&productName=phone&productCategory=TECHNOLOGY
```

3) Obter por ID
```
GET /v1/products/{id}
```

4) Atualizar
```
PUT /v1/products/{id}
{
  "price": 899.99,
  "amountAvailable": 45
}
```

5) Remover
```
DELETE /v1/products/{id}
```
