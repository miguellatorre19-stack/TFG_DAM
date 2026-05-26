export interface Actividad {
  id: number;
  description?: string;
  dayActivity?: string;
  typeActivity?: string;
  duration?: number;
  canJoin?: boolean;
  capacity?: number;
  longitude?: number;
  latitude?: number;
}