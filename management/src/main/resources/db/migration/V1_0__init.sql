CREATE TABLE unique_iscsi_target (
    uuid VARCHAR_IGNORECASE(36) NOT NULL,
    capacity_mb INTEGER NOT NULL,
    target_name VARCHAR(128) NOT NULL UNIQUE,
    storage_host VARCHAR(64) NOT NULL,
    PRIMARY KEY (uuid)
);

CREATE TABLE initiator_iqn (
    uuid VARCHAR_IGNORECASE(36) NOT NULL,
    iqn VARCHAR(256) NOT NULL,
    unique_iscsi_target_uuid VARCHAR_IGNORECASE(36),
    PRIMARY KEY (uuid),
    CONSTRAINT fk_unique_iscsi_target FOREIGN KEY(unique_iscsi_target_uuid) REFERENCES unique_iscsi_target(uuid)
);
