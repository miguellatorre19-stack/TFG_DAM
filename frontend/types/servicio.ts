export interface Servicio {
  id: number;
  description?: string;
  typeService?: string;
  duration?: number;
  capacity?: number;
  periodicity?: string;
  active?: boolean;
}