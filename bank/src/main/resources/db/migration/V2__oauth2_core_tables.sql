-- oauth2_registered_client
CREATE TABLE IF NOT EXISTS oauth2_registered_client (
                                                        id                            VARCHAR(100)  NOT NULL,
                                                        client_id                     VARCHAR(100)  NOT NULL,
                                                        client_id_issued_at           DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                                        client_secret                 VARCHAR(200),
                                                        client_secret_expires_at      DATETIME,
                                                        client_name                   VARCHAR(200)  NOT NULL,

                                                        client_authentication_methods VARCHAR(1000) NOT NULL,
                                                        authorization_grant_types     VARCHAR(1000) NOT NULL,
                                                        redirect_uris                 VARCHAR(1000),
                                                        post_logout_redirect_uris     VARCHAR(1000),
                                                        scopes                        VARCHAR(1000) NOT NULL,

                                                        client_settings               TEXT          NOT NULL,
                                                        token_settings                TEXT          NOT NULL,

                                                        PRIMARY KEY (id),
                                                        UNIQUE KEY uk_oauth2_registered_client_client_id (client_id)
) ENGINE=InnoDB;

-- oauth2_authorization_consent (그대로 OK)
CREATE TABLE IF NOT EXISTS oauth2_authorization_consent (
                                                            registered_client_id VARCHAR(100) NOT NULL,
                                                            principal_name       VARCHAR(200) NOT NULL,
                                                            authorities          VARCHAR(1000) NOT NULL,
                                                            PRIMARY KEY (registered_client_id, principal_name)
) ENGINE=InnoDB;

-- (권장) 토큰/코드 저장 테이블도 함께 생성
CREATE TABLE IF NOT EXISTS oauth2_authorization (
                                                    id                            VARCHAR(100)  NOT NULL,
                                                    registered_client_id          VARCHAR(100)  NOT NULL,
                                                    principal_name                VARCHAR(200)  NOT NULL,
                                                    authorization_grant_type      VARCHAR(100)  NOT NULL,

                                                    authorized_scopes             VARCHAR(1000),
                                                    attributes                    LONGTEXT,

                                                    state                         VARCHAR(500),

                                                    authorization_code_value      LONGTEXT,
                                                    authorization_code_issued_at  DATETIME,
                                                    authorization_code_expires_at DATETIME,
                                                    authorization_code_metadata   LONGTEXT,

                                                    access_token_value            LONGTEXT,
                                                    access_token_issued_at        DATETIME,
                                                    access_token_expires_at       DATETIME,
                                                    access_token_metadata         LONGTEXT,
                                                    access_token_type             VARCHAR(100),
                                                    access_token_scopes           VARCHAR(1000),

                                                    oidc_id_token_value           LONGTEXT,
                                                    oidc_id_token_issued_at       DATETIME,
                                                    oidc_id_token_expires_at      DATETIME,
                                                    oidc_id_token_metadata        LONGTEXT,
                                                    oidc_id_token_claims          LONGTEXT,

                                                    refresh_token_value           LONGTEXT,
                                                    refresh_token_issued_at       DATETIME,
                                                    refresh_token_expires_at      DATETIME,
                                                    refresh_token_metadata        LONGTEXT,

                                                    user_code_value               LONGTEXT,
                                                    user_code_issued_at           DATETIME,
                                                    user_code_expires_at          DATETIME,
                                                    user_code_metadata            LONGTEXT,

                                                    device_code_value             LONGTEXT,
                                                    device_code_issued_at         DATETIME,
                                                    device_code_expires_at        DATETIME,
                                                    device_code_metadata          LONGTEXT,

                                                    PRIMARY KEY (id),
                                                    KEY ix_oauth2_auth_principal (principal_name),
                                                    KEY ix_oauth2_auth_client (registered_client_id)
) ENGINE=InnoDB;