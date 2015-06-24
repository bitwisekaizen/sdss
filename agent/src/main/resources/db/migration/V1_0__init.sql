CREATE TABLE iscsi_target (
    target_name VARCHAR(128) NOT NULL UNIQUE,
    capacity_mb INTEGER NOT NULL,
    affinity_key VARCHAR(128) NOT NULL,
    PRIMARY KEY (target_name)
);

CREATE TABLE host_iqn (
    iqn VARCHAR(256) NOT NULL,
    iscsi_target_target_name VARCHAR(128),
    CONSTRAINT fk_iscsi_target FOREIGN KEY(iscsi_target_target_name) REFERENCES iscsi_target(target_name)
);
