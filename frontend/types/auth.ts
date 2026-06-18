export interface LoginRequest {
  email: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  type: string;
  id: number;
  name: string;
  email: string;
  roles: string[];
}

export interface MeResponse {
  userId: number;
  name: string;
  email: string;
  roles: string[];
  profileType: "SOCIO" | "PARTICIPANTE" | "TRABAJADOR" | "USUARIO";
  socioId?: number | null;
  participanteId?: number | null;
  participanteIds: number[];
  trabajadorId?: number | null;
}
