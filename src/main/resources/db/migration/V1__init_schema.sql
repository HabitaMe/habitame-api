-- ==========================
-- Usuarios
-- ==========================
CREATE TABLE users (
    id SERIAL,
    username VARCHAR(50) NOT NULL,
    photo_url VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(150),
    phone VARCHAR(20),
    role VARCHAR(20) NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_use_id PRIMARY KEY(id),
    CONSTRAINT uk_use_use UNIQUE (username),
    CONSTRAINT uq_use_ema UNIQUE (email),
    CONSTRAINT ch_use_rol CHECK (role IN ('ARRENDADOR','ARRENDATARIO','ADMIN'))
);

-- ==========================
-- Países
-- ==========================
CREATE TABLE countries (
    id SERIAL,
    name VARCHAR(100) NOT NULL,
    iso_code VARCHAR(3),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_cou_id PRIMARY KEY (id),
    CONSTRAINT uk_cou_nam UNIQUE (name),
    CONSTRAINT uk_cou_iso UNIQUE (iso_code)
);

-- ==========================
-- Provincias
-- ==========================
CREATE TABLE provinces (
    id SERIAL,
    country_id INT NOT NULL,
    name VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_prv_id PRIMARY KEY (id),
    CONSTRAINT fk_prv_cou_id FOREIGN KEY (country_id) REFERENCES countries(id) ON DELETE CASCADE,
    CONSTRAINT uk_prv_cou_nam UNIQUE (country_id, name)
);

-- ==========================
-- Ciudades
-- ==========================
CREATE TABLE cities (
    id SERIAL,
    province_id INT NOT NULL,
    name VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_cit_id PRIMARY KEY (id),
    CONSTRAINT fk_cit_pro FOREIGN KEY (province_id) REFERENCES provinces(id) ON DELETE CASCADE,
    CONSTRAINT uk_cit_pro_nam UNIQUE (province_id, name)
);

-- ==========================
-- Propiedades
-- ==========================




CREATE TABLE properties (
    id SERIAL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    type VARCHAR(50),
    address VARCHAR(255) NOT NULL,
    city_id INT NOT NULL,
    floor INT DEFAULT 1,
    area_m2 NUMERIC(8,2),
    bathrooms_total INT,
    owner_in_house BOOLEAN DEFAULT FALSE,
    owner_id INT,
    status VARCHAR(20) DEFAULT 'IN_REVIEW',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by INT,

    CONSTRAINT pk_prp_id PRIMARY KEY (id),

    CONSTRAINT fk_prp_own FOREIGN KEY (owner_id) REFERENCES users(id) ON DELETE SET NULL,
    CONSTRAINT fk_prp_upd_by FOREIGN KEY (updated_by) REFERENCES users(id) ON DELETE SET NULL,
    CONSTRAINT fk_prp_cit FOREIGN KEY (city_id) REFERENCES cities(id) ON DELETE RESTRICT,

    CONSTRAINT ch_pro_are CHECK(area_m2 > 0),
    CONSTRAINT ch_pro_bat CHECK(bathrooms_total >= 0),
    CONSTRAINT ch_pro_sta CHECK(status IN ('ACTIVE','INACTIVE','IN_REVIEW'))
);

CREATE INDEX idx_properties_city ON properties(city_id);

-- ==========================
-- Habitaciones
-- ==========================
CREATE TABLE rooms (
    id SERIAL,
    property_id INT NOT NULL,
    title VARCHAR(100) NOT NULL,
    description TEXT,
    area_m2 NUMERIC(6,2),
    max_occupants INT DEFAULT 1,
    price_per_month NUMERIC(8,2),
    floor INT DEFAULT 1,
    status VARCHAR(20) DEFAULT 'IN_REVIEW',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by INT,

    CONSTRAINT pk_roo_id PRIMARY KEY (id),
    
    CONSTRAINT fk_roo_pro FOREIGN KEY (property_id) REFERENCES properties(id) ON DELETE CASCADE,
    CONSTRAINT fk_pro_upd_by FOREIGN KEY (updated_by) REFERENCES users(id) ON DELETE SET NULL,

    CONSTRAINT ch_roo_are CHECK(area_m2 > 0),
    CONSTRAINT ch_roo_max_occ CHECK(max_occupants > 0),
    CONSTRAINT ch_roo_pri CHECK(price_per_month >= 0),
    CONSTRAINT ch_roo_sta CHECK(status IN ('ACTIVE','INACTIVE','IN_REVIEW'))
);

CREATE INDEX idx_rooms_price ON rooms(price_per_month);
CREATE INDEX idx_rooms_area ON rooms(area_m2);
CREATE INDEX idx_rooms_status ON rooms(status);

-- ==========================
-- Alquileres
-- ==========================
CREATE TABLE rentals (
    id SERIAL,
    room_id INT NOT NULL,
    tenant_id INT NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE,
    price_paid NUMERIC(8,2),
    status VARCHAR(20) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_ren_id PRIMARY KEY (id),

    CONSTRAINT fk_ren_roo FOREIGN KEY (room_id) REFERENCES rooms(id) ON DELETE CASCADE,
    CONSTRAINT fk_ren_ten FOREIGN KEY (tenant_id) REFERENCES users(id) ON DELETE CASCADE,

    CONSTRAINT ch_ren_pri CHECK(price_paid >= 0),
    CONSTRAINT ch_ren_sta CHECK(status IN ('ACTIVE','COMPLETED','UPCOMING')),
    CONSTRAINT ch_ren_dat CHECK(end_date IS NULL OR end_date >= start_date)
);

-- ==========================
-- Imágenes de propiedades
-- ==========================
CREATE TABLE property_images (
    id SERIAL,
    property_id INT NOT NULL,
    image_url VARCHAR(255) NOT NULL,
    is_main BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_pro_img_id PRIMARY KEY (id),

    CONSTRAINT fk_pro_img_pro FOREIGN KEY (property_id) REFERENCES properties(id) ON DELETE CASCADE
);

CREATE UNIQUE INDEX uq_property_main ON property_images(property_id) WHERE is_main = TRUE;

-- ==========================
-- Imágenes de habitaciones
-- ==========================
CREATE TABLE room_images (
    id SERIAL,
    room_id INT NOT NULL,
    image_url VARCHAR(255) NOT NULL,
    is_main BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_roo_img_id PRIMARY KEY (id),

    CONSTRAINT fk_roo_img FOREIGN KEY (room_id) REFERENCES rooms(id) ON DELETE CASCADE
);

CREATE UNIQUE INDEX uq_room_main ON room_images(room_id) WHERE is_main = TRUE;

-- ==========================
-- Amenities
-- ==========================
CREATE TABLE amenities (
    id SERIAL,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    scope VARCHAR(20) NOT NULL,
    CONSTRAINT pk_ame_id PRIMARY KEY (id),
    CONSTRAINT uk_ame_nam UNIQUE (name),
    CONSTRAINT ch_ame_sco CHECK (scope IN ('PROPERTY', 'ROOM', 'BOTH'))
);

CREATE TABLE property_amenities (
    property_id INT NOT NULL,
    amenity_id INT NOT NULL,
    CONSTRAINT pk_pro_ame PRIMARY KEY(property_id, amenity_id),
    CONSTRAINT fk_pro_ame_pro FOREIGN KEY(property_id) REFERENCES properties(id) ON DELETE CASCADE,
    CONSTRAINT fk_pro_ame_ame FOREIGN KEY(amenity_id) REFERENCES amenities(id) ON DELETE CASCADE
);

CREATE TABLE room_amenities (
    room_id INT NOT NULL,
    amenity_id INT NOT NULL,
    CONSTRAINT pk_roo_ame PRIMARY KEY(room_id, amenity_id),
    CONSTRAINT fk_roo_ame_roo FOREIGN KEY(room_id) REFERENCES rooms(id) ON DELETE CASCADE,
    CONSTRAINT fk_roo_ame_ame FOREIGN KEY(amenity_id) REFERENCES amenities(id) ON DELETE CASCADE
);

-- ==========================
-- Reviews (usuarios)
-- ==========================
CREATE TABLE reviews (
    id SERIAL,
    property_id INT,
    room_id INT,
    user_id INT NOT NULL,
    rating INT NOT NULL,
    comment TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_rev_id PRIMARY KEY (id),

    CONSTRAINT fk_rev_pro FOREIGN KEY(property_id) REFERENCES properties(id) ON DELETE CASCADE,
    CONSTRAINT fk_rev_roo FOREIGN KEY(room_id) REFERENCES rooms(id) ON DELETE CASCADE,
    CONSTRAINT fk_rev_use FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE,

    CONSTRAINT ch_rev_rat CHECK(rating BETWEEN 1 AND 5),
    CONSTRAINT ch_rev_tar CHECK ((property_id IS NOT NULL AND room_id IS NULL) OR (property_id IS NULL AND room_id IS NOT NULL))
);

-- ==========================
-- Revisión de propiedades (admin)
-- ==========================
CREATE TABLE property_reviews (
    id SERIAL,
    property_id INT NOT NULL,
    admin_id INT,
    status VARCHAR(20) NOT NULL,
    comment TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    reviewed_at TIMESTAMP,

    CONSTRAINT pk_pro_rev_id PRIMARY KEY (id),
    CONSTRAINT fk_pro_rev_pro FOREIGN KEY(property_id) REFERENCES properties(id) ON DELETE CASCADE,
    CONSTRAINT fk_pro_rev_adm FOREIGN KEY(admin_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT ch_pro_rev_sta CHECK(status IN ('APPROVED','REJECTED','PENDING'))
);

-- ==========================
-- Revisión de habitaciones (admin)
-- ==========================
CREATE TABLE room_reviews (
    id SERIAL,
    room_id INT NOT NULL,
    admin_id INT,
    status VARCHAR(20) NOT NULL,
    comment TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    reviewed_at TIMESTAMP,
    
    CONSTRAINT pk_roo_rev_id PRIMARY KEY (id),
    CONSTRAINT fk_roo_rev_roo FOREIGN KEY(room_id) REFERENCES rooms(id) ON DELETE CASCADE,
    CONSTRAINT fk_roo_rev_adm FOREIGN KEY(admin_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT ch_roo_rev_sta CHECK(status IN ('APPROVED','REJECTED','PENDING'))
);

-- ==========================
-- Refresh Tokens
-- ==========================
CREATE TABLE refresh_tokens (
    id BIGSERIAL,
    token VARCHAR(255) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    expiry_date TIMESTAMP NOT NULL,
    revoked BOOLEAN NOT NULL DEFAULT FALSE,

    CONSTRAINT pk_ref_tok_id PRIMARY KEY (id),
    CONSTRAINT fk_ref_tok_use FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_refresh_tokens_user ON refresh_tokens(user_id);
