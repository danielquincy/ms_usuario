-- Insertar Usuario (ID generado manualmente para el ejemplo: 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11')
INSERT INTO usuario (id, username, email, password, created, modified, last_login, token, is_active)
VALUES (
           'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11',
           'admin',
           'admin@dominio.cl',
           'biTyqEKGVhS1o1WSd7AYjLfLZXgk1MmfGDvCsojfu3FoWpzvJ37cRqgA77xxAMFF',
           CURRENT_TIMESTAMP,
           CURRENT_TIMESTAMP,
           CURRENT_TIMESTAMP,
           'token-inicial-ejemplo-jwt',
           true
       );

-- Insertar Teléfonos asociados al usuario anterior
INSERT INTO telefono (id,number, citycode, contrycode, user_id)
VALUES ('4b2bb5bf-f0c3-4978-b457-dc1a667a2f17','77889965', '55', '505', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11');

INSERT INTO telefono (id,number, citycode, contrycode, user_id)
VALUES ('a2e44aac-1565-474d-93e0-030c302d5f51','987654321', '55', '505', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11');