# Guia de Autenticação 🍃 Spring Boot OAuth2 & JWT

> Guia completo para implementar autenticação usando OAuth2 e tokens JWT em um projeto Spring Boot:

- Servidor de Autorização OAuth2
- Autenticação com token JWT
- Controle de acesso baseado em papéis (roles ADMIN e OPERATOR)
- Tipo de concessão personalizada por senha
- Banco de dados H2 para testes

## Pré-requisitos

- Java 17+
- Maven
- Spring Boot

## Dependências

> Passos:
- Modelo de dados User-Role
    - Entidades
    - Seed da base de dados
- Incluir infraestrutura de exceções e validação
    - Dependências Bean Validation
    - Exceções de serviço customizadas
    - Handler de exceções dos controladores
- Incluir infraestrutura de segurança ao projeto
    - Dependências Spring Security e OAuth2
    - Variáveis de ambiente
    - checklist do Spring Security
    - Implementação customizada password grant
    - Classes de configuração Authorization Server e Resource Server
- Implementar as funcionalidades

---

### Adicionar as dependências ao `pom.xml`:

```xml
<!-- Servidor de Autorização OAuth2 -->
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-oauth2-authorization-server</artifactId>
</dependency>

<!-- Servidor de Recursos OAuth2 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-oauth2-resource-server</artifactId>
</dependency>

<!-- Spring Security -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>

<!-- Spring Validation -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```
---

### Criar entidades:

![alt text](image.png)

Implementar classes e métodos das interfaces do `spring.security.core` nas entididades (models) e no service `UserService.java`;

---

#### Criar `UserDetailsProjection` para fazer a `nativeQuery`:
 ```java
 public interface UserDetailProjection {

    String getUsername();
    String getPassword();
    Long getRoleId();
    String getAuthority();
}
```
---

#### Adicionar em `UserRepository` query para retornar usuário e sua detarminada Role:

```java
@Query(nativeQuery = true, value = """
    SELECT 
        tb_user.email AS username,
        tb_user.password,
        tb_role.id AS roleId,
        tb_role.authority AS authority
        FROM tb_user
        INNER JOIN tb_user_role ON tb_user.id = tb_user_role.user_id
        INNER JOIN tb_role ON tb_role.id = tb_user_role.role_id
        WHERE tb_user.email = :email
    """)
    List<UserDetailProjection> searchUserAndRolesByEmail(String email);
```

---
### Custom Exceptions

- Incluir os handlers na camada de controllers e as custom exceptions na camada de services.
- Para as exceptions 422 do `jakarta.validation.constraints`, como `@NotBlank` adicionar a annotation `@Valid` nos parâmetros das funções POST.

---
### No arquivo `application.properties`:

```properties
security.client-id=${CLIENT_ID:myclientid}
security.client-secret=${CLIENT_SECRET:myclientsecret}
security.jwt.duration=${JWT_DURATION:86400}
cors.origins=${CORS_ORIGINS:http://localhost:3000,http://localhost:5173}
```
---
### Configurações de Segurança

#### Usar PasswordEncoder para criptografar as senhas do DB para testes com JWT;
```java
@Configuration
public class AppConfig {
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```
- Para criptografar uma senha "123456", por exemplo e receber um código hash com o BCrypt, basta instanciar e classe criada:
```java
public class TesteSenha {
    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(AppConfig.class); // ou sua classe @SpringBootApplication
        PasswordEncoder encoder = context.getBean(PasswordEncoder.class);

        String rawPassword = "123456";
        String encoded = encoder.encode(rawPassword);

        System.out.println("Senha criptografada: " + encoded);
    }
}
```
---

### Usando H2 para testes
```sql
INSERT INTO tb_role (authority) VALUES ('ROLE_OPERATOR');
INSERT INTO tb_role (authority) VALUES ('ROLE_ADMIN');

INSERT INTO tb_user(name, email, password) VALUES ('ana', 'ana@gmail.com', '$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG');
INSERT INTO tb_user(name, email, password) VALUES ('bob', 'bob@gmail.com', '$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG');

INSERT INTO tb_user_role(user_id, role_id) VALUES (1, 1);
INSERT INTO tb_user_role(user_id, role_id) VALUES (2, 1);
INSERT INTO tb_user_role(user_id, role_id) VALUES (2, 2);
```
---

### Autenticar usuário, testar no Insomnia:

Adicionar `Form Data` ao body:

    - username: bob@gmail.com
    - password: 123456
    - grant_type: passowrd

Adicionar ao `Auth` o login e a senha que foram adicionados ao `application.properties`:

    - username: myclientid
    - password: myclientsecret

Editar Tag, ou adicionar às `environments`:

    - token:
        - Response, Body Attribute, ... ,
        - Filter (Json): $.access_token

Validar Requests `@PreAuthorize`, em Auth, usar `Bearer Token`:

    - Token: _.token

---