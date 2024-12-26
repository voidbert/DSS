Como configurar:

 - Instalar o `mariadb`;
 - Começar o seu servidor (depende do sistema opeartivo);
 - Iniciar o `mariadb` como `root` (`sudo mariadb -u root`);
 - Executar os seguintes comandos SQL:

```sql
CREATE DATABASE dsshorarios;
CREATE USER 'user'@localhost IDENTIFIED BY 'secretpassword';
GRANT ALL PRIVILEGES ON *.* TO 'user'@localhost;
```

Como reiniciar a base de dados:

```sql
DROP DATABASE dsshorarios;
```
