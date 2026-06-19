USE studiomuda;

CREATE TABLE IF NOT EXISTS doca (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    capacidade_paletes_diaria INT NOT NULL,
    ativa BOOLEAN DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS distribuidora (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(120) NOT NULL,
    nivel_prioridade VARCHAR(10) NOT NULL DEFAULT 'MEDIA',
    ativa BOOLEAN DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS calendario_excecao (
    id INT AUTO_INCREMENT PRIMARY KEY,
    data DATE NOT NULL,
    motivo VARCHAR(180) NOT NULL,
    ativa BOOLEAN DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS agendamento_remessa (
    id INT AUTO_INCREMENT PRIMARY KEY,
    doca_id INT NOT NULL,
    distribuidora_id INT NOT NULL,
    data DATE NOT NULL,
    horario VARCHAR(5) NOT NULL,
    volume_paletes INT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'CONFIRMADO',
    FOREIGN KEY (doca_id) REFERENCES doca(id),
    FOREIGN KEY (distribuidora_id) REFERENCES distribuidora(id)
);

INSERT INTO doca (nome, capacidade_paletes_diaria, ativa)
SELECT 'Doca Norte', 18, true
WHERE NOT EXISTS (SELECT 1 FROM doca WHERE nome = 'Doca Norte');

INSERT INTO doca (nome, capacidade_paletes_diaria, ativa)
SELECT 'Doca Sul', 12, true
WHERE NOT EXISTS (SELECT 1 FROM doca WHERE nome = 'Doca Sul');

INSERT INTO doca (nome, capacidade_paletes_diaria, ativa)
SELECT 'Doca Express', 8, true
WHERE NOT EXISTS (SELECT 1 FROM doca WHERE nome = 'Doca Express');

INSERT INTO distribuidora (nome, nivel_prioridade, ativa)
SELECT 'Verde Express', 'ALTA', true
WHERE NOT EXISTS (SELECT 1 FROM distribuidora WHERE nome = 'Verde Express');

INSERT INTO distribuidora (nome, nivel_prioridade, ativa)
SELECT 'Jardim Log', 'MEDIA', true
WHERE NOT EXISTS (SELECT 1 FROM distribuidora WHERE nome = 'Jardim Log');

INSERT INTO distribuidora (nome, nivel_prioridade, ativa)
SELECT 'Rota Paisagismo', 'BAIXA', true
WHERE NOT EXISTS (SELECT 1 FROM distribuidora WHERE nome = 'Rota Paisagismo');
