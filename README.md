# API Principal - MVP do Sprint de Arquitetura de Software
Trabalho da Pós- graduacao em Engenharia de software da PUC Rio realizado por Mauro Sergio Lopes dos Santos Junior. 

Esse repositorio representa a API Principal do MVP.

# Movie List API

API de frontend baseada em Play Framework que atua como gateway para o serviço de filmes, fornecendo uma interface simplificada para aplicações cliente.

## Visão Geral da Arquitetura

```
┌─────────────────┐
│  Aplicação      │
│  Cliente        │
└─────────┬───────┘
          │ HTTP Requests
          ▼
┌─────────────────┐
│  Movie List API │
│  (Play Framework│
│   Porta 9000)   │
└─────────┬───────┘
          │ HTTP Proxy
          ▼
┌─────────────────┐
│ Movies Provider │
│    Service      │
│  (Porta 9090)   │
└─────────────────┘
```

## Funcionalidades Principais

### 🎭 **Gateway de Filmes**
- Interface simplificada para busca de filmes
- Proxy transparente para o Movies Provider Service
- Tratamento de erros e transformação de dados

### 📊 **Gerenciamento de Preferências**
- Adicionar avaliações de filmes
- Consultar histórico de preferências do usuário
- Integração com sistema de recomendação

### 🎯 **Sugestões Personalizadas**
- Endpoint dedicado para recomendações de filmes
- Baseado nas preferências armazenadas do usuário
- Algoritmo inteligente por gêneros favoritos

### 🔄 **Arquitetura de Proxy**
- Abstrai complexidade do serviço backend
- Transformação de modelos de dados
- Tratamento unificado de erros

## Estrutura do Projeto

### Controladores

#### MovieController.scala
**Responsabilidades:**
- Gerenciar requisições HTTP
- Validar dados de entrada
- Coordenar chamadas para o serviço backend
- Transformar respostas para o formato adequado

**Endpoints:**
- `GET /movie/:title` - Buscar filme por título
- `GET /movie/suggestion/:userId` - Obter sugestão personalizada
- `POST /preferences` - Adicionar preferência do usuário
- `GET /preferences/:userId` - Listar preferências do usuário

### Cliente HTTP

#### MovieProviderClient.scala
**Propósito:** Cliente HTTP para comunicação com Movies Provider Service

**Métodos:**
- `getMovie(title)` - Busca filme no serviço backend
- `addPreference(preference)` - Envia preferência para armazenamento
- `getUserPreferences(userId)` - Recupera preferências do usuário
- `getMovieSuggestion(userId)` - Solicita sugestão personalizada

**Características:**
- Uso do Play WS Client para requisições HTTP
- Tratamento de erros com fallbacks
- Transformação automática de JSON
- Recuperação de falhas de rede

### Modelos de Domínio

#### Movie.scala
```scala
case class Movie(title: String, year: String, plot: String, imdbID: String, genre: String)
```
- Representa dados de filme
- Formatação JSON automática via Play JSON
- Compatível com Movies Provider Service

#### UserPreference.scala
```scala
case class UserPreference(userId: String, movieId: String, rating: Int)
```
- Representa avaliação do usuário
- Escala de rating de 1-5
- Serialização JSON integrada

## Fluxos de Requisição

### Busca de Filme
```
Cliente → GET /movie/Inception
    ↓
MovieController.getMovie()
    ↓
MovieProviderClient.getMovie("Inception")
    ↓
HTTP GET → Movies Provider Service
    ↓
Resposta JSON transformada
    ↓
Retorna Movie para cliente
```

### Adição de Preferência
```
Cliente → POST /preferences
    ↓
MovieController.addPreference()
    ↓
Validação JSON → UserPreference
    ↓
MovieProviderClient.addPreference()
    ↓
Transformação para formato backend (com id: 0)
    ↓
HTTP POST → Movies Provider Service
    ↓
Confirmação de sucesso/erro
```

### Sugestão de Filme
```
Cliente → GET /movie/suggestion/user123
    ↓
MovieController.getMovieSuggestion("user123")
    ↓
MovieProviderClient.getMovieSuggestion("user123")
    ↓
HTTP GET → Movies Provider Service
    ↓
Algoritmo de recomendação por gênero
    ↓
Novo filme sugerido baseado em preferências
```

## Configuração

### Dependências (build.sbt)
- **Play Framework** - Framework web principal
- **Play WS** - Cliente HTTP para requisições
- **Guice** - Injeção de dependência
- **ScalaTest** - Framework de testes

### Configuração de Rotas (conf/routes)
```
GET     /movie/suggestion/:userId    controllers.MovieController.getMovieSuggestion(userId: String)
GET     /movie/:title                controllers.MovieController.getMovie(title: String)
POST    /preferences                 controllers.MovieController.addPreference()
GET     /preferences/:userId         controllers.MovieController.getUserPreferences(userId: String)
```

## Executando a Aplicação

### Pré-requisitos
1. Movies Provider Service deve estar executando na porta 9090
2. Java 17 ou superior (para execução local)
3. SBT instalado (para execução local)

### Opção 1: Execução Local
```bash
# Executar em modo desenvolvimento
sbt run

# Compilar projeto
sbt compile

# Executar testes
sbt test
```

### Opção 2: Execução com Docker

#### Executar apenas Movie List API
```bash
# Construir a imagem Docker
docker build -t movie-list-api .

# Executar o container (certifique-se que movies-provider-service está rodando)
docker run -p 9000:9000 movie-list-api
```

#### Executar ambos os serviços com Docker
```bash
# 1. Primeiro, execute o Movies Provider Service
cd ../pucrio-arqsoft-api-secund-ria
docker build -t movies-provider-service .
docker run -d --name movies-provider -p 9090:9090 -p 8081:8081 movies-provider-service

# 2. Em seguida, execute o Movie List API
cd ../pucrio-arqsoft-api-principal
docker build -t movie-list-api .
docker run -p 9000:9000 --link movies-provider:movies-provider movie-list-api
```

#### Usando Docker Network (Recomendado)
```bash
# Criar uma rede Docker
docker network create movies-network

# Executar Movies Provider Service
cd ../pucrio-arqsoft-api-secund-ria
docker build -t movies-provider-service .
docker run -d --name movies-provider --network movies-network -p 9090:9090 -p 8081:8081 movies-provider-service

# Executar Movie List API
cd ../pucrio-arqsoft-api-principal
docker build -t movie-list-api .
docker run --name movie-list-api --network movies-network -p 9000:9000 movie-list-api
```

#### Usando Docker Compose (Mais Fácil)
```bash
# No diretório raiz do projeto (arq-soft)
cd ..
docker-compose up --build

# Para executar em background
docker-compose up -d --build

# Para parar os serviços
docker-compose down
```

**Serviço Disponível:**
- API REST: http://localhost:9000

## Exemplos de Uso

### Buscar Filme
```bash
curl "http://localhost:9000/movie/Inception"
```

**Resposta:**
```json
{
  "title": "Inception",
  "year": "2010",
  "plot": "A thief who steals corporate secrets...",
  "imdbID": "tt1375666",
  "genre": "Action, Sci-Fi, Thriller"
}
```

### Adicionar Preferência
```bash
curl -X POST "http://localhost:9000/preferences" \
  -H "Content-Type: application/json" \
  -d '{"userId":"user123","movieId":"tt1375666","rating":5}'
```

### Obter Sugestão
```bash
curl "http://localhost:9000/movie/suggestion/user123"
```

**Resposta:**
```json
{
  "title": "Blade Runner 2049",
  "year": "2017",
  "plot": "Young Blade Runner K's discovery...",
  "imdbID": "tt1856101",
  "genre": "Action, Drama, Mystery"
}
```

### Listar Preferências
```bash
curl "http://localhost:9000/preferences/user123"
```

## Tecnologias Utilizadas

- **Scala 2.13** - Linguagem de programação
- **Play Framework 3.0** - Framework web reativo
- **Play WS** - Cliente HTTP assíncrono
- **Guice** - Container de injeção de dependência
- **Play JSON** - Biblioteca de processamento JSON
