-- ==========================
-- Amenities
-- ==========================
INSERT INTO amenities (name, description, scope) VALUES
('Ascensor', 'Edificio equipado con ascensor', 'PROPERTY'),
('Garaje', 'Plaza de aparcamiento disponible', 'PROPERTY'),
('Trastero', 'Espacio adicional para almacenamiento', 'PROPERTY'),
('Piscina comunitaria', 'Piscina compartida por los residentes', 'PROPERTY'),
('Jardín', 'Zona ajardinada privada o comunitaria', 'PROPERTY'),
('Gimnasio', 'Gimnasio comunitario en el edificio', 'PROPERTY'),
('Portero físico', 'Servicio de conserjería presencial', 'PROPERTY'),
('Portero automático', 'Sistema automático de acceso al edificio', 'PROPERTY'),
('Zona infantil', 'Área de juegos para niños', 'PROPERTY'),
('Acceso PMR', 'Acceso adaptado para personas con movilidad reducida', 'PROPERTY'),
('Placas solares', 'Sistema de energía solar en el edificio', 'PROPERTY'),
('Zona comunitaria', 'Espacios comunes del edificio o urbanización', 'PROPERTY'),
('Cama individual', 'Habitación equipada con cama individual', 'ROOM'),
('Cama doble', 'Habitación equipada con cama doble', 'ROOM'),
('Armario', 'Armario disponible en la habitación', 'ROOM'),
('Escritorio', 'Escritorio para estudio o trabajo', 'ROOM'),
('Silla', 'Silla incluida en la habitación', 'ROOM'),
('Llave propia', 'La habitación dispone de cerradura independiente', 'ROOM'),
('Baño privado', 'Baño de uso exclusivo de la habitación', 'ROOM'),
('Balcón privado', 'Balcón de uso exclusivo de la habitación', 'ROOM'),
('Televisión', 'Televisión instalada en la habitación', 'ROOM'),
('Ropa de cama incluida', 'Incluye sábanas y ropa de cama', 'ROOM'),
('Aire acondicionado', 'Sistema de climatización', 'BOTH'),
('Calefacción', 'Sistema de calefacción', 'BOTH'),
('WiFi', 'Conexión a internet incluida', 'BOTH'),
('Amueblado', 'Totalmente amueblado', 'BOTH'),
('Cocina equipada', 'Cocina con electrodomésticos', 'PROPERTY'),
('Electrodomésticos', 'Incluye electrodomésticos básicos', 'PROPERTY'),
('Lavadora', 'Lavadora disponible', 'PROPERTY'),
('Lavavajillas', 'Lavavajillas disponible', 'PROPERTY'),
('Horno', 'Horno instalado en la cocina', 'PROPERTY'),
('Microondas', 'Microondas disponible', 'PROPERTY'),
('Vistas exteriores', 'Vistas al exterior', 'BOTH'),
('Terraza', 'Espacio exterior disponible', 'BOTH');

-- ==========================
-- Countries
-- ==========================
INSERT INTO countries (name, iso_code)
VALUES ('España', 'ESP');

-- ==========================
-- Provinces
-- ==========================
INSERT INTO provinces (country_id, name) VALUES
((SELECT id FROM countries WHERE iso_code = 'ESP'), 'Andalucía'),
((SELECT id FROM countries WHERE iso_code = 'ESP'), 'Aragón'),
((SELECT id FROM countries WHERE iso_code = 'ESP'), 'Asturias'),
((SELECT id FROM countries WHERE iso_code = 'ESP'), 'Islas Baleares'),
((SELECT id FROM countries WHERE iso_code = 'ESP'), 'Canarias'),
((SELECT id FROM countries WHERE iso_code = 'ESP'), 'Cantabria'),
((SELECT id FROM countries WHERE iso_code = 'ESP'), 'Castilla-La Mancha'),
((SELECT id FROM countries WHERE iso_code = 'ESP'), 'Castilla y León'),
((SELECT id FROM countries WHERE iso_code = 'ESP'), 'Cataluña'),
((SELECT id FROM countries WHERE iso_code = 'ESP'), 'Comunidad Valenciana'),
((SELECT id FROM countries WHERE iso_code = 'ESP'), 'Extremadura'),
((SELECT id FROM countries WHERE iso_code = 'ESP'), 'Galicia'),
((SELECT id FROM countries WHERE iso_code = 'ESP'), 'La Rioja'),
((SELECT id FROM countries WHERE iso_code = 'ESP'), 'Comunidad de Madrid'),
((SELECT id FROM countries WHERE iso_code = 'ESP'), 'Región de Murcia'),
((SELECT id FROM countries WHERE iso_code = 'ESP'), 'Navarra'),
((SELECT id FROM countries WHERE iso_code = 'ESP'), 'País Vasco'),
((SELECT id FROM countries WHERE iso_code = 'ESP'), 'Ceuta'),
((SELECT id FROM countries WHERE iso_code = 'ESP'), 'Melilla');

-- ==========================
-- Cities
-- ==========================
INSERT INTO cities (province_id, name) VALUES
((SELECT p.id FROM provinces p WHERE p.name='Andalucía'), 'Sevilla'),
((SELECT p.id FROM provinces p WHERE p.name='Andalucía'), 'Málaga'),
((SELECT p.id FROM provinces p WHERE p.name='Andalucía'), 'Córdoba'),
((SELECT p.id FROM provinces p WHERE p.name='Andalucía'), 'Granada'),
((SELECT p.id FROM provinces p WHERE p.name='Andalucía'), 'Almería'),
((SELECT p.id FROM provinces p WHERE p.name='Andalucía'), 'Cádiz'),
((SELECT p.id FROM provinces p WHERE p.name='Andalucía'), 'Huelva'),
((SELECT p.id FROM provinces p WHERE p.name='Andalucía'), 'Jaén'),
((SELECT p.id FROM provinces p WHERE p.name='Comunidad de Madrid'), 'Madrid'),
((SELECT p.id FROM provinces p WHERE p.name='Comunidad de Madrid'), 'Alcalá de Henares'),
((SELECT p.id FROM provinces p WHERE p.name='Comunidad de Madrid'), 'Getafe'),
((SELECT p.id FROM provinces p WHERE p.name='Comunidad de Madrid'), 'Leganés'),
((SELECT p.id FROM provinces p WHERE p.name='Comunidad de Madrid'), 'Móstoles'),
((SELECT p.id FROM provinces p WHERE p.name='Cataluña'), 'Barcelona'),
((SELECT p.id FROM provinces p WHERE p.name='Cataluña'), 'Hospitalet de Llobregat'),
((SELECT p.id FROM provinces p WHERE p.name='Cataluña'), 'Badalona'),
((SELECT p.id FROM provinces p WHERE p.name='Cataluña'), 'Terrassa'),
((SELECT p.id FROM provinces p WHERE p.name='Cataluña'), 'Sabadell'),
((SELECT p.id FROM provinces p WHERE p.name='Comunidad Valenciana'), 'Valencia'),
((SELECT p.id FROM provinces p WHERE p.name='Comunidad Valenciana'), 'Alicante'),
((SELECT p.id FROM provinces p WHERE p.name='Comunidad Valenciana'), 'Castellón de la Plana'),
((SELECT p.id FROM provinces p WHERE p.name='Comunidad Valenciana'), 'Elche'),
((SELECT p.id FROM provinces p WHERE p.name='Comunidad Valenciana'), 'Torrevieja'),
((SELECT p.id FROM provinces p WHERE p.name='País Vasco'), 'Bilbao'),
((SELECT p.id FROM provinces p WHERE p.name='País Vasco'), 'Vitoria-Gasteiz'),
((SELECT p.id FROM provinces p WHERE p.name='País Vasco'), 'San Sebastián'),
((SELECT p.id FROM provinces p WHERE p.name='Galicia'), 'A Coruña'),
((SELECT p.id FROM provinces p WHERE p.name='Galicia'), 'Vigo'),
((SELECT p.id FROM provinces p WHERE p.name='Galicia'), 'Santiago de Compostela'),
((SELECT p.id FROM provinces p WHERE p.name='Galicia'), 'Lugo'),
((SELECT p.id FROM provinces p WHERE p.name='Galicia'), 'Ourense');
