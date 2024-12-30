CREATE USER IF NOT EXISTS 'user'@localhost IDENTIFIED BY 'secretpassword';
GRANT ALL PRIVILEGES ON *.* TO 'user'@localhost;

DROP DATABASE IF EXISTS dsshorarios;
CREATE DATABASE dsshorarios;
USE dsshorarios;

CREATE TABLE alunos(
    numero VARCHAR(10) NOT NULL PRIMARY KEY
);

CREATE TABLE cursos(
    id VARCHAR(10) NOT NULL PRIMARY KEY
);

CREATE TABLE ucs(
    nome VARCHAR(75) NOT NULL PRIMARY KEY
);

CREATE TABLE salas(
    nome       VARCHAR(20) NOT NULL PRIMARY KEY,
    capacidade INTEGER     NOT NULL
);

CREATE TABLE turnos(
    uc         VARCHAR(75) NOT NULL,
    nome       VARCHAR(75) NOT NULL,
    tipo       CHAR(1)     NOT NULL,
    dia        INT         NOT NULL,
    comeco     VARCHAR(10) NOT NULL,
    fim        VARCHAR(10) NOT NULL,
    sala       VARCHAR(20) NOT NULL,
    capacidade INTEGER,

    PRIMARY KEY (uc, nome),
    FOREIGN KEY (uc)   REFERENCES ucs (nome)   ON DELETE CASCADE,
    FOREIGN KEY (sala) REFERENCES salas (nome) ON DELETE CASCADE,
    CONSTRAINT chkTipo CHECK (tipo in ('T', 'P'))
);

CREATE TABLE alunoUcs(
    aluno VARCHAR(10) NOT NULL,
    uc    VARCHAR(75) NOT NULL,

    FOREIGN KEY (aluno) REFERENCES alunos (numero) ON DELETE CASCADE,
    FOREIGN KEY (uc) REFERENCES ucs (nome) ON DELETE CASCADE
);

CREATE TABLE cursoUcs(
    curso VARCHAR(10) NOT NULL,
    uc    VARCHAR(75) NOT NULL PRIMARY KEY,

    FOREIGN KEY (curso) REFERENCES cursos (id) ON DELETE CASCADE,
    FOREIGN KEY (uc) REFERENCES ucs (nome) ON DELETE CASCADE
);

CREATE TABLE cursoAlunos(
    curso VARCHAR(10) NOT NULL,
    aluno VARCHAR(10) NOT NULL PRIMARY KEY,

    FOREIGN KEY (curso) REFERENCES cursos (id) ON DELETE CASCADE,
    FOREIGN KEY (aluno) REFERENCES alunos (numero) ON DELETE CASCADE
);

CREATE TABLE utilizadores(
    tipo        CHAR(1)     NOT NULL,
    email       VARCHAR(75) NOT NULL PRIMARY KEY,
    password    VARCHAR(75) NOT NULL,
    numeroAluno VARCHAR(10),
    idCurso     VARCHAR(10),

    CONSTRAINT chkTipo CHECK (tipo in ('A', 'D'))

    -- Don't assert referential intergrity, because these two tables are in different subsystems
    -- (maybe they can be ported to micro services one day).
    --
    -- FOREIGN KEY (numeroAluno) REFERENCES alunos (numero),
    -- FOREIGN KEY (idCurso) REFERENCES cursos (id)
);

-- Povoamento inicial

INSERT INTO cursos(id) VALUES
    ('LEI');

INSERT INTO salas(nome, capacidade) VALUES
    ('S80', 80),
    ('S100', 100),
    ('S300', 300);

INSERT INTO utilizadores(tipo, email, password, numeroAluno, idCurso) VALUES
    ('D', 'als@di.uminho.pt', 'dclei', NULL, 'LEI');
