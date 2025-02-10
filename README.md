# video-processor-rest-api

Serviço responsável por expor endpoints para que um consumidor seja capaz de converter vídeo em um arquivo zip contendo um conjunto de imagens (frames).

## 🏛👷 Arquitetura

O serviço foi desenvolvido utilizando o padrão de arquitetura hexagonal, onde a pasta/camada `application` é o centro da aplicação e as camadas `infrastructure` e `adapter` são periféricas.

A camada `application` é <strong>agnóstica</strong>, ela depende só de si mesma, portanto ela não se comunica com camadas periféricas, entretanto as camadas periféricas (`infrastructure` e `adapter`) se comunicam com ela.

A camada `adapter` é responsável por fazer a ponte (comunicação) entre o `"mundo externo"` e a camada `application`, portanto para o desenvolvimento deste há dois *"adapters"*: um responsável pela saída (*"OUTBOUND"*) comunicando com o banco de dados postgresql, e outro pela entrada (*"INBOUND"*) que é responsável por expor uma API rest (recebendo requisições HTTP) para consumidores.

Abaixo uma imagem representativa:

<img src="docs/Arquitetura-hexagonal.drawio.png" />


## 👷🏃 Como construir e executar local?

O desenvolvimento deste foi feito em cima da plataforma de desenvolvimento java (com springboot) e banco de dados postgresql para a persistência dos dados, portanto é recomendado as seguintes versões para a execução local:
```
jvm: 21
postgresql: 16
```

### Comando para rodar local (modo debug):
```
$ ./gradlew bootRun -Dspring.profiles.active=local
# (O comando acima roda o projeto no ambiente local)
```

**Nota:** para executação local, é necessario configurar um banco de dados postgresql, portanto recomenda-se o docker para facilitar este processo. O próximo passo explica como executar com docker.


### Para construir e executar com docker:

- Construção:

```$ docker compose up --build```

- Execução (depois de construído):

```$ docker compose up```

--- 

## ⚙️ Executando os testes

Na aplicação existem testes unitários. Para executar os testes unitários execute o comando abaixo:

### 🔩 Testes unitários

Os testes unitários testam as classes individualmente, sem dependências externas. Para executar os testes unitários execute o comando abaixo:

```
./gradlew test
```
---

### Recursos expostos
Abaixo a uma lista de recursos expostos pela API, contendo exemplos:

- Criar novo usuario (POST /api/users)
```
curl --request POST \
--url http://afbc288a284444fce95adc15bee259dc-cd8277978b8f295a.elb.us-east-1.amazonaws.com:8086/api/users \
--header 'Content-Type: application/json' \
--header 'User-Agent: insomnia/10.3.0' \
--data '{
"email":"test@test.com",
"password":"123"
}'
```

-  Autenticar usuário (POST /api/auth)
```
curl --request POST \
--url http://afbc288a284444fce95adc15bee259dc-cd8277978b8f295a.elb.us-east-1.amazonaws.com:8086/api/auth \
--header 'Content-Type: application/json' \
--header 'User-Agent: insomnia/10.3.0' \
--data '{
"email":"testx@test.com",
"password":"123"
}'
```

- Enviar vídeo para processamento (POST /processing)

```
curl --request POST \
  --url http://afbc288a284444fce95adc15bee259dc-cd8277978b8f295a.elb.us-east-1.amazonaws.com:8086/processing \
  --header 'Authorization: Bearer eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJ0ZXN0eEB0ZXN0LmNvbSIsImlhdCI6MTczOTE0NzE5NiwiZXhwIjoxNzM5MTUwNzk2fQ.CNOboPJ3YSt1PxGeWWXv6y7bs780ar3yQUHadyGXjZSAfNdooNk7FzNfgd3wEZbC' \
  --header 'Content-Type: multipart/form-data' \
  --header 'User-Agent: insomnia/10.1.1' \
  --form 'file=@PATH/3735731-sd_426_240_25fps.mp4'
```

- Listar processos de um usuário autenticado (GET /processing)
```
curl --request GET \
  --url http://afbc288a284444fce95adc15bee259dc-cd8277978b8f295a.elb.us-east-1.amazonaws.com:8086/processing \
  --header 'Authorization: Bearer eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJ0ZXN0eEB0ZXN0LmNvbSIsImlhdCI6MTczOTE0NzE5NiwiZXhwIjoxNzM5MTUwNzk2fQ.CNOboPJ3YSt1PxGeWWXv6y7bs780ar3yQUHadyGXjZSAfNdooNk7FzNfgd3wEZbC' \
  --header 'Content-Type: multipart/form-data' \
  --header 'User-Agent: insomnia/10.1.1' \
```
response body:
```
[
	{
		"id": "d7d37df4-875a-4386-8f01-18fe0a782eae",
		"originalFile": "2024-06-12 09-29-17.mp4",
		"user": "test@test.com",
		"status": "RECEIVED",
		"createdAt": "2025-02-10T00:27:47.391615797",
		"updatedAt": "2025-02-10T00:27:47.391627042"
	},
]
```

- Download de um zip contendo os frames processados (GET /processing/{id})

```
curl --request GET \
  --url http://afbc288a284444fce95adc15bee259dc-cd8277978b8f295a.elb.us-east-1.amazonaws.com:8086/processing/1bd9a160-dded-4970-8a96-40ebb54ec851 \
  --header 'Authorization: Bearer eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJ0ZXN0QHRlc3QuY29tIiwiaWF0IjoxNzM5MTIzMjQ4LCJleHAiOjE3MzkxMjY4NDh9.KoynYCXrE0nEzWe7rcB98o0VK4TfYxP4eLpzQIvYJ7aXUPRV0Vnfcu1E8q8FRrxS' \
```

Os exemplos acima são apenas exemplos de chamadas apontando pro aws, para testar a aplicação localmente, utilize o endereço `http://localhost:8086` ao invés do endereço `http://afbc288a284444fce95adc15bee259dc-cd8277978b8f295a.elb.us-east-1.amazonaws.com:8086`.

A rota de autenticação é a `/api/auth` e a rota de criação de usuário é a `/api/users`.

A aplicação utiliza JWT para autenticação, portanto o token de autenticação é gerado na rota `/api/auth` e é necessário passar o token gerado no header `Authorization` para as rotas que requerem autenticação. 
**IMPORTANTE**: As rotas abaixo requerem que o consumir esteja devidamente autorizado (com token valido):

- Envia vídeo para processamento
  - POST /processing (multipart/form-data)
- Lista todos processamentos do usuário autenticado (lista com status dos processamentos)
  - GET /processing
- Download do arquivo ZIP (contendo todos frames/imagens)
  - GET /processing/{id}


---

### Diagramas de sequência contendo a interação entre os componentes

- Fluxo do processamento de um vídeo
<img src="docs/diagrama_sequencia_geral.png" />

- Download do arquivo ZIP (contendo todos frames/imagens)
<img src="docs/sequencia_obter_arquivo_zip.png" />

---

### Integração com AWS SQS

A aplicação monitora a fila SQS `update_process` e, ao receber uma mensagem, processa o id.
Se tudo estiver correto, atualiza o status do processamento do vídeo no banco de dados.

Após receber um novo vídeo via upload a aplicação publica uma nova mensagem na fila SQS `trigger_processing` com a referencia do vídeo a ser processado.


----
## Padrão SAGA Coreografado
O padrão Saga Coreografado é aplicado em projetos de aplicações distribuídas e microserviços para assegurar a consistência em transações que envolvem múltiplos serviços. Nesse contexto, cada serviço participante de uma transação executa uma parte da operação e emite eventos para indicar seu estado. Outros serviços ou um coordenador observam esses eventos e coordenam as operações para garantir que a transação seja concluída com sucesso ou revertida de maneira consistente.

Existem várias vantagens em utilizar o padrão Saga Coreografado em aplicações, como as descritas nos repositórios do projeto "video-processor":

- Consistência distribuída: Como as transações envolvem diversos serviços, é crucial garantir que todos estejam em um estado consistente, mesmo em caso de falhas.
  Escalabilidade e desempenho: O padrão possibilita a distribuição das operações entre vários serviços, melhorando a escalabilidade e o desempenho do sistema.
- Resiliência: Este padrão aumenta a resiliência do sistema a falhas, permitindo que as transações sejam revertidas de forma consistente se um dos serviços falhar.
- Visibilidade e monitoramento: Cada serviço emite eventos para indicar seu estado, facilitando o monitoramento e a detecção de problemas no sistema.
- Flexibilidade e manutenção: O padrão torna o sistema mais flexível, permitindo a adição ou alteração de novos serviços sem modificar a lógica de negócios existente.
- Em resumo, o padrão Saga Coreografado é utilizado em aplicações distribuídas e microserviços para assegurar a consistência e a integridade das transações, mesmo em ambientes distribuídos e de alta escalabilidade.  
