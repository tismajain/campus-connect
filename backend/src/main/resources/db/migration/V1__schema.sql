CREATE EXTENSION IF NOT EXISTS pg_trgm;

CREATE TABLE buildings (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(20) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL
);

CREATE TABLE labs (
    id BIGSERIAL PRIMARY KEY,
    building_id BIGINT NOT NULL REFERENCES buildings(id) ON DELETE RESTRICT,
    room_number VARCHAR(20) NOT NULL,
    department_code VARCHAR(20),
    name VARCHAR(120),
    floor INTEGER,
    description TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE'
        CHECK (status IN ('AVAILABLE','IN_CLASS','RESTRICTED','MAINTENANCE')),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE(building_id, room_number)
);

CREATE TABLE lab_systems (
    id BIGSERIAL PRIMARY KEY,
    lab_id BIGINT NOT NULL REFERENCES labs(id) ON DELETE CASCADE,
    system_summary VARCHAR(200) NOT NULL,
    min_ram_gb INTEGER,
    max_ram_gb INTEGER,
    has_gpu BOOLEAN NOT NULL DEFAULT FALSE,
    gpu_memory_gb INTEGER,
    source_configuration TEXT NOT NULL
);

CREATE TABLE equipment (
    id BIGSERIAL PRIMARY KEY,
    lab_id BIGINT REFERENCES labs(id) ON DELETE SET NULL,
    name VARCHAR(200) NOT NULL,
    category VARCHAR(80) NOT NULL DEFAULT 'Embedded Systems',
    quantity INTEGER,
    description TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE'
        CHECK (status IN ('AVAILABLE','RESTRICTED','MAINTENANCE'))
);

CREATE TABLE subjects (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(30) NOT NULL UNIQUE,
    name VARCHAR(150)
);

CREATE TABLE software (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(120) NOT NULL UNIQUE,
    category VARCHAR(80),
    description TEXT
);

CREATE TABLE subject_software (
    subject_id BIGINT NOT NULL REFERENCES subjects(id) ON DELETE CASCADE,
    software_id BIGINT NOT NULL REFERENCES software(id) ON DELETE CASCADE,
    notes VARCHAR(200),
    PRIMARY KEY(subject_id, software_id)
);

CREATE TABLE lab_software (
    lab_id BIGINT NOT NULL REFERENCES labs(id) ON DELETE CASCADE,
    software_id BIGINT NOT NULL REFERENCES software(id) ON DELETE CASCADE,
    version VARCHAR(60),
    installed BOOLEAN NOT NULL DEFAULT TRUE,
    notes TEXT,
    PRIMARY KEY(lab_id, software_id)
);

CREATE INDEX idx_labs_building ON labs(building_id);
CREATE INDEX idx_labs_status ON labs(status);
CREATE INDEX idx_lab_systems_lab ON lab_systems(lab_id);
CREATE INDEX idx_lab_systems_ram ON lab_systems(max_ram_gb);
CREATE INDEX idx_lab_systems_gpu ON lab_systems(has_gpu, gpu_memory_gb);
CREATE INDEX idx_software_name_trgm ON software USING gin (name gin_trgm_ops);
CREATE INDEX idx_equipment_name_trgm ON equipment USING gin (name gin_trgm_ops);
CREATE INDEX idx_subjects_code_trgm ON subjects USING gin (code gin_trgm_ops);
