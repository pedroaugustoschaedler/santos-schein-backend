-- 1. Tabela de Usuários (Clientes e Administradores)
CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) DEFAULT 'CLIENT', -- Roles: 'CLIENT', 'ADMIN'
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 2. Tabela de Quadras do Complexo
CREATE TABLE IF NOT EXISTS courts (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL, -- Ex: 'Quadra 1 - Principal'
    sport_type VARCHAR(50) NOT NULL, -- Ex: 'Futebol 7', 'Vôlei'
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 3. Tabela de Reservas
CREATE TABLE IF NOT EXISTS reservations (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    court_id UUID NOT NULL REFERENCES courts(id) ON DELETE CASCADE,
    reservation_date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    status VARCHAR(20) DEFAULT 'PENDING', -- Status: 'PENDING', 'CONFIRMED', 'CANCELLED'
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    
    -- Regra crítica de negócio: A mesma quadra não pode ter dois horários idênticos
    CONSTRAINT unique_court_reservation UNIQUE (court_id, reservation_date, start_time)
);
