# AdminCliente - Sistema de Gestão de Usuários

## Descrição
Sistema web desenvolvido em Spring Boot para gestão de usuários com diferentes níveis de acesso (SUPERADMIN, ADMIN e CLIENTE).

## Acesso ao Sistema

### Superusuário (SUPERADMIN)
Para acessar o sistema pela primeira vez, utilize as seguintes credenciais do superusuário:
- **Email:** root@admin.com
- **Senha:** root123

**Importante:** 
- Este usuário é criado automaticamente na primeira execução do sistema
- O superusuário não pode ser excluído do sistema
- Possui acesso total a todas as funcionalidades do sistema, incluindo:
  - Gerenciamento de todos os usuários (ADMIN e CLIENTE)
  - Criação, edição e exclusão de ADMINs
  - Visualização de todas as informações do sistema

## Requisitos
- Java 17 ou superior
- MySQL 8.0 ou superior
- Maven 3.6 ou superior

## Configuração do Ambiente

### 1. Banco de Dados
O sistema utiliza MySQL como banco de dados. Certifique-se de ter o MySQL instalado e rodando em sua máquina.

Por padrão, a aplicação está configurada para:
- Host: localhost
- Porta: 3306
- Usuário: root
- Senha: (em branco)
- Database: admincliente (será criada automaticamente)

Se necessário, você pode alterar estas configurações no arquivo `src/main/resources/application.properties`.

### 2. Configuração do Projeto
1. Clone o repositório:
```bash
git clone [URL_DO_REPOSITORIO]
cd AdminCliente
```

2. Instale as dependências:
```bash
mvn clean install
```

### 3. Executando o Projeto
1. Execute o projeto usando Maven:
```bash
mvn spring-boot:run
```

2. Acesse a aplicação em: `http://localhost:8080`

## Funcionalidades

### Níveis de Acesso
1. **SUPERADMIN**
   - Gerenciar todos os usuários (ADMIN e CLIENTE)
   - Criar, editar e excluir ADMINs
   - Todas as funcionalidades do ADMIN

2. **ADMIN**
   - Gerenciar CLIENTEs
   - Criar, editar e excluir CLIENTEs
   - Visualizar lista de usuários

3. **CLIENTE**
   - Visualizar seu perfil
   - Editar suas informações
   - Alterar sua senha

### Principais Funcionalidades
- Autenticação e autorização
- Gestão de perfil de usuário
- Upload de imagem de perfil
- Alteração de senha
- Listagem de usuários (para ADMIN e SUPERADMIN)
- Interface responsiva

## Estrutura do Projeto
```
src/
├── main/
│   ├── java/
│   │   └── org/example/admincliente/
│   │       ├── config/
│   │       ├── controllers/
│   │       ├── dtos/
│   │       ├── entities/
│   │       ├── enums/
│   │       ├── exceptions/
│   │       ├── repositories/
│   │       ├── security/
│   │       └── services/
│   └── resources/
│       ├── static/
│       │   ├── css/
│       │   ├── js/
│       │   └── images/
│       └── templates/
└── test/
```

## Segurança
- Senhas são armazenadas com criptografia BCrypt
- Controle de acesso baseado em roles
- Proteção contra CSRF desativada para facilitar o desenvolvimento
- Sessão com timeout de 24 horas

## Uploads
As imagens de perfil são armazenadas em:
```
/uploads/usuarios/
```

## Solução de Problemas

### Erro de Conexão com Banco de Dados
1. Verifique se o MySQL está rodando
2. Confirme as credenciais no `application.properties`
3. Certifique-se que a porta 3306 está disponível

### Erro de Permissão de Arquivos
1. Verifique se a pasta `uploads` tem permissão de escrita
2. Certifique-se que o usuário que executa a aplicação tem permissões adequadas

## Contribuição
1. Faça um fork do projeto
2. Crie uma branch para sua feature (`git checkout -b feature/AmazingFeature`)
3. Commit suas mudanças (`git commit -m 'Add some AmazingFeature'`)
4. Push para a branch (`git push origin feature/AmazingFeature`)
5. Abra um Pull Request

## Licença
Este projeto está sob a licença MIT. Veja o arquivo `LICENSE` para mais detalhes. 