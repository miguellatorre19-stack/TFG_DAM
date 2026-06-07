export interface Servicio {
  id: number;
  description?: string;
  duration?: number;
  capacity?: number;
  periodicity?: string;
  requisites?: string;
  active?: boolean;
}
