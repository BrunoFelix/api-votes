# API Votes

## Como executar a aplicação

Basta clonar o projeto e executar o comando para subir as imagens docker através do docker-compose.

```sh
    git clone https://github.com/BrunoFelix/api-votes.git
```

No diretório raiz do repositório deve-se executar o seguinte comando:

```sh
    docker-compose up -d
```
Caso queira rodar via command line:

```sh
    ./gradlew bootRun
```

## Dependências/Tecnologias utilizadas

A API foi desenvolvida utilizando Java, Spring boot e MySQL. Para testes, utilizei Junit, Mockito e TestContainers para subir uma
base de dados MySQL somente para os testes. Utilizei também o Kafka como sistema de mensageria.

- **Spring Boot Starter Data JPA**
  - Integração de aplicações Spring com o JPA (Java Persistence API);
  
- **Spring Boot Starter Validation**
  - Conjunto de anotações que servem para validar dados;
  
- **Spring Boot Starter Web**
  - Auxilia na construção de aplicações web trazendo já disponíveis para uso Spring MVC, Rest e o Tomcat como servidor;

- **Spring Boot DevTools**
  - Reinicia automaticamente a aplicação quando arquivos no classpath são alterados;
  
- **Spring Cloud OpenFeign**
  - Cliente REST declarativo para aplicações Spring Boot. Muito utilizado para comunicações entre APIs;
  
- **Spring for Apache Kafka**
  - Plataforma de streaming distribuído excelente para a troca de mensagem em alta escala. Muito utilizada para troca de mensagens entre microserviços.
  
- **Project Lombok**
  - Permite gerar em tempo de compilação os métodos getters e setters, métodos construtores, padrão builder e muito mais

- **Springdoc OpenAPI UI (swagger)**
  - Formato de descrição para API’s REST, permitindo descrevê-la informando endpoints disponíveis e seu método HTTP, parâmetros de entrada e saída, além de informações como contato, licenças e termos de uso da API

- **MySQL**
  - Banco de dados utilizado;

- **Spring Boot Starter Test**
  - Adiciona suporte a testes unitários e integrados do Spring Framework;
  
- **Testcontainers**
  - Biblioteca Java que ajuda a mitigar esse desafio ao gerenciar dependências ‘dockerizadas’ para seus testes. Utilizado para subir uma instância do MySQL somente para testes.

## Endpoints

- **Documentação Swagger:**
  - http://localhost:8095/swagger-ui/index.html#

- **Associados (associate):**
  - [POST] /v1/associate
  - [GET] /v1/associate
  - [GET] /v1/associate/{id}

- **Pauta (agenda):**
    - [POST] /v1/agenda
    - [GET] /v1/agenda
    - [GET] /v1/agenda/{id}

- **Sessão de votação (vote session):**
    - [POST] /v1/vote-session
    - [GET] /v1/vote-session
    - [GET] /v1/vote-session/{id}
  
- **Vote (vote):**
    - [POST] /v1/vote
    - [GET] /v1/vote
    - [GET] /v1/vote/{id}

## Desafio Técnico

Feito conforme solicitado, o associado precisa ser cadastrado previamente antes de efetuar seu voto, no seu cadastro será validado seu CPF, caso esteja inválido, não poderá se cadastrar e consequentemente não poderá votar. Após seu cadastro, utilizaremos o id do mesmo para outras ações no sistema, já que CPF é uma informação pessoal e não deve ser utilizada para esse fim. Retornei o resultado da votação tanto na agenda quando na sessão.

Utilizei o banco de dados MySQL pois é um dos mais utilizados do mercado, mas como estou utilizando spring data JPA poderia ser outro SQL. 
Utilizei o testContainers pois acho que facilita bastante o teste, além que de ele sozinho sobe uma intância docker do banco e ao final dos testes ele mesmo apaga, sendo assim não precisamos ficar nos preocupando com os dados gerados.
Utilizei JMeter para teste de performance pois é OpenSource e mais simples de utilizar rapidamente.

As mensagens dos commits seguiram o padrão do Conventional Commits (https://www.conventionalcommits.org/en/v1.0.0/).
A versão da API seguiu o padrão do Semantic Versioning (https://semver.org/).

### Tarefa Bônus 1 - Integração com sistemas externos

Realizado utilizando OpenFeign, a interface está dentro do package com.brunofelix.api.votes.service.client;
https://spring.io/projects/spring-cloud-openfeign

### Tarefa Bônus 2 - Mensageria e filas

Realizado utilizando Kafka, no docker-compose coloquei as imagens necessárias para subir o servidor kafka. 
Criei uma função automatizada utilizado o @Sheduled que irá executar a cada 1 minuto para contabilizar
sessões de votações encerradas e enviar o resultado como um evento no kafka.

### Tarefa Bônus 3 - Performance

Utilizei o JMeter para realizar os testes de performance, testei dois endpoins principais do sistema, Get Associates (com 5 registros) e POST Vote.

Resultado - 300 Requests:
[Get] /v1/associate: Tempo médio de resposta de 627ms. Vazão de 80 requests por segundos. Taxa de erro de 0%;
[POST] /v1/vote: Tempo médio de resposta de 1535ms. Vazão de 68.5 requests por segundos. Taxa de erro de 0%;

Obs: teste realizado na minha máquina (i5-7300HQ 2.50 GHz, 8GB);
![image](https://user-images.githubusercontent.com/11357706/194333289-433b2086-4537-40cc-8588-d5e5919df847.png)

Uma boa ferramenta para monitoramento de performance durante o uso do sistema é o ElasticAPM, porém é paga.

### Tarefa Bônus 4 - Versionamento da API

Realizado, através do versionamento da URL, a especificação da versão fica no path, por exemplo:

{url}/**v1**/associate

Utilizei essa abordagem, pois além de dar um visual mais clean na URL, facilita a navegação para outras versões da API.
