# Portfolio Management API

[![Java](https://img.shields.io/badge/Java-17-orange?logo=openjdk)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.14-green?logo=springboot)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue?logo=postgresql)](https://www.postgresql.org/)
[![Docker](https://img.shields.io/badge/Docker-Enabled-blue?logo=docker)](https://www.docker.com/)

## 1. Sobre o Projeto
Esta é uma solução executiva para o gerenciamento de portfólio de projetos, desenvolvida como parte de um desafio técnico para a vaga de Desenvolvedor(a) Fullstack Java Pleno/Sênior. A **Portfolio Management API** foi construída utilizando o ecossistema **Spring Boot 3**, focando na robustez de APIs REST, arquitetura MVC limpa, alta qualidade de código e integração entre sistemas.

O sistema permite o ciclo de vida completo de projetos, desde a análise inicial até o encerramento, garantindo conformidade com regras de negócio complexas, como limites de alocação de membros, validações de status e análise dinâmica de risco.

## 2. Tecnologias Utilizadas
- **Java 17**: Linguagem base com recursos modernos de produtividade.
- **Spring Boot 3**: Framework core (Web, Data JPA, Security, Actuator).
- **PostgreSQL**: Banco de dados relacional para persistência de dados.
- **Docker & Docker Compose**: Containerização de toda a infraestrutura (Banco e Mocks).
- **WireMock**: Simulação de APIs externas para testes de integração e desenvolvimento.
- **JUnit 5 & Mockito**: Suíte completa de testes unitários e de integração.
- **Swagger/OpenAPI**: Documentação interativa e contratos de API.
- **MapStruct & Lombok**: Redução de boilerplate e mapeamento eficiente de DTOs.

## 3. Arquitetura e Padrões (MVC)

O projeto adota uma arquitetura em camadas robusta baseada no padrão **MVC (Model-View-Controller)**, com forte inspiração em princípios de arquitetura limpa para o isolamento do núcleo de negócio. A estrutura de pacotes foi desenhada para garantir a clara separação de responsabilidades.

Abaixo, a representação visual do fluxo de dados e da organização arquitetural:

```text
    Client Request
          │
          ▼ (JSON)
┌────────────────────────────────────────────────────────────┐
│   API LAYER (br.com.jonassoares.portfolio.api)             │
│   ├─ Controllers (Endpoints REST, HTTP Status)             │
│   └─ DTOs (Data Transfer Objects, Java Records)            │
└────────────────────────┬─────────────────────────┬─────────┘
                         │ (DTOs)                  │
                         ▼                         │
┌──────────────────────────────────────────────┐   │
│   DOMAIN LAYER (...portfolio.domain)         │   │
│   ├─ Services (Orquestração de negócio)      │   │ (Throws Exceptions)
│   ├─ Validators (Regras isoladas/SOLID)      │   │
│   ├─ Enums (Máquinas de estado/Domínio)      │   │
│   └─ Entities (Mapeamento ORM/JPA)           │   │
└─────────┬────────────────────────────┬───────┘   │
          │                            │           ▼
          │ (Entities)                 │ ┌─────────────────────────┐
          ▼                            │ │   EXCEPTION HANDLING    │
┌──────────────────────────────────┐   │ │   GlobalExceptionHandler│
│   REPOSITORY LAYER               │   │ │   (RFC 7807)            │
│ (...portfolio.domain.repositories│   │ └─────────────────────────┘
│   └─ Spring Data JPA / Spec      │   │
└─────────────────┬────────────────┘   │
                  │                    ▼
                  │       ┌──────────────────────────────────┐
                  │       │   INFRASTRUCTURE LAYER           │
                  │       │ (...portfolio.infrastructure)    │
                  ▼       │   └─ MemberApiClient (RestClient)│
        [(  PostgreSQL)]  └────────────────┬─────────────────┘
                                            │
                                            ▼
                                   [(  WireMock API)]
```

### Detalhamento das Camadas
- **API (Controllers & DTOs)**: A camada de apresentação. É o ponto de entrada da aplicação, responsável por receber as requisições HTTP, validar a sintaxe de entrada (usando Jakarta Validation) e rotear para o domínio. O isolamento é estrito: as entidades de banco de dados nunca transitam por aqui, apenas DTOs.
- **Domain (Services, Validators & Entities)**: O coração do sistema.
  - Os **Services** atuam como orquestradores de casos de uso (ex: `ProjectService`, `ProjectMemberService`).
  - Para evitar o anti-pattern de *Fat Services*, regras de negócio complexas foram extraídas para componentes dedicados no pacote `validators` (ex: `ProjectStatusValidator`, `MemberAllocationValidator`), respeitando o *Single Responsibility Principle* (SRP).
- **Repositories**: Camada de persistência. Isola a comunicação com o banco de dados. Além dos métodos padrões do Spring Data, abriga queries analíticas (JPQL) e utiliza `JpaSpecificationExecutor` para montar cláusulas `WHERE` dinâmicas de forma programática.
- **Infrastructure/Clients**: Isola a comunicação com o mundo externo, utilizando o `RestClient` nativo do Spring Boot 3 para consumir a API de membros mockada, mantendo o domínio agnóstico a detalhes de protocolo HTTP.

## 4. Estratégias e Boas Práticas Aplicadas
- **SOLID e Clean Code**: Aplicação rigorosa de princípios como Injeção de Dependências e Responsabilidade Única (Single Responsibility Principle). As regras de negócio são extraídas para validadores especializados, evitando *Fat Services*.
- **DRY (Don't Repeat Yourself)**: Reutilização de lógica de negócio e centralização de tratamento de erros.
- **Tratamento Global de Exceções**: Implementação de um `GlobalExceptionHandler` seguindo a **RFC 7807** (Problem Details for HTTP APIs), garantindo respostas de erro padronizadas e semânticas.
- **Padrão DTO**: Uso sistemático de *Data Transfer Objects*. As entidades de banco de dados são preservadas e nunca expostas diretamente nos endpoints da API.

## 5. A Jornada de Desenvolvimento (As 6 Etapas)
O desenvolvimento seguiu uma abordagem incremental e iterativa:

- **Sprint 1: Modelagem do Domínio**: Estruturação inicial do banco de dados e implementação do CRUD base para gestão de projetos.
- **Sprint 2: Máquina de Estados e Regras**: Implementação da lógica de transição de status, cálculo dinâmico de risco (BAIXO, MÉDIO, ALTO) e mecanismo de *soft delete* para preservar integridade referencial.
- **Sprint 3: Consultas Dinâmicas**: Introdução do `JpaSpecificationExecutor` para permitir que o usuário realize filtros opcionais complexos e paginação de alta performance.
- **Sprint 4: Segurança**: Camada de proteção com **Spring Security**, utilizando autenticação HTTP Basic (stateless) em memória com senhas criptografadas via **BCrypt**.
- **Sprint 5: Integração de Microsserviços**: Uso do `RestClient` nativo do Spring 3 para consumir APIs externas (simuladas via WireMock), validando regras cruzadas de limites de membros.
- **Sprint 6: Business Intelligence (BI)**: Criação de um endpoint de relatórios consolidado, utilizando agregações nativas no PostgreSQL para garantir performance e evitar gargalos de memória na JVM.

## 6. Pré-requisitos
Antes de iniciar, certifique-se de ter instalado:
- **JDK 17** ou superior.
- **Maven 3.8+**.
- **Docker e Docker Compose**.
- **Git**.

## 7. Como Executar o Projeto
Siga o passo a passo abaixo para rodar a aplicação localmente:

1. **Clonar o repositório**:
   ```bash
   git clone https://github.com/seu-usuario/portfolio-management-api.git
   cd portfolio-management-api/portfolio-management-api
   ```

2. **Configurar as variáveis de ambiente**:
   Duplique o arquivo `.env.example`, renomeie-o para `.env` e preencha conforme o exemplo:
   ```env
   POSTGRES_DB=portfolio_db
   POSTGRES_USER=postgres
   POSTGRES_PASSWORD=suasenha
   POSTGRES_PORT=5432
   SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/portfolio_db
   SPRING_DATASOURCE_USERNAME=postgres
   SPRING_DATASOURCE_PASSWORD=suasenha
   API_ADMIN_USERNAME=admin
   API_ADMIN_PASSWORD=admin123
   API_MEMBER_URL=http://localhost:8081
   ```

3. **Subir a infraestrutura**:
   Execute o comando para iniciar o banco de dados e o WireMock:
   ```bash
   docker-compose up -d
   ```

4. **Iniciar a aplicação**:
   ```bash
   mvn clean spring-boot:run
   ```

## 8. Documentação e Testes
### Documentação Interativa (Swagger)
A API possui documentação completa via Swagger UI. Com a aplicação rodando, acesse:
[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

> **Nota**: Para testar os endpoints, utilize o botão **"Authorize"** e informe as credenciais configuradas no seu arquivo `.env` (ex: `admin` / `admin123`).

### Suíte de Testes
Para garantir a qualidade e prevenir regressões, o projeto conta com uma robusta cobertura de testes:
```bash
mvn test
```
A suíte inclui:
- **Testes Unitários**: Validação de regras de negócio isoladas.
- **Testes de Integração**: Verificação de fluxos completos, segurança e integração com banco de dados.
- **Mocks de API**: Utilização de WireMock para garantir que a integração com microsserviços externos funcione conforme o esperado.

---
Desenvolvido por **Jonas Soares Sousa** para avaliação técnica.
