export interface PrivateRequest {
  id: number;
  kind: "actividad" | "servicio";
  itemId: number;
  title: string;
  createdAt?: string;
  state?: string;
  price?: number;
  participanteId: number;
}
