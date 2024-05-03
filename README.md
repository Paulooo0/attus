## Teste técnico da Attus

API de gerenciamento de pessoas

Test coverage: 84%
Incluindo todos as condicionais

Alguns endpoints estão listados no arquivo `api.http`

### Como inicializar
Requisitos: Docker

Para subir a aplicação, abra a pasta `prod` no terminal (pode ser colocada em qualquer localização, basta acessa-la pelo terminal) e utilize o comando `docker compose up`.
Um container contendo a aplicação e o banco de dados será instalado e inicializado automaticamente.
Simula um ambiente de produção, inclui variáveis de ambiente.

Em caso de erro, certifique-se de que as portas 8080 e 5432 não estão ocupadas.
Se estiverem ocupadas, no Linux, utilize o comando `fuser -k 8080/tcp` e o comando `fuser -k 5432/tcp`.