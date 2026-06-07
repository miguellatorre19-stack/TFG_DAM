export interface InscripcionPayload {
  participanteId: number;
  state: string;
  price: number;
}

export interface InscripcionActividad {
  id: number;
  createdAt?: string;
  state?: string;
  price?: number;
  participanteId: number;
}

export interface InscripcionServicio {
  id: number;
  createdAt?: string;
  state?: string;
  price?: number;
  participanteId: number;
}
