export interface IssuedAccessCredentials {
  usuarioId: number;
  email: string;
  initialPassword: string;
}

export interface SocioAccessResponse extends IssuedAccessCredentials {
  socio: {
    id: number;
  };
}

export interface ParticipanteAccessResponse extends IssuedAccessCredentials {
  participante: {
    id: number;
    socioID: number;
  };
}

export interface TrabajadorAccessResponse extends IssuedAccessCredentials {
  trabajador: {
    id: number;
  };
}
