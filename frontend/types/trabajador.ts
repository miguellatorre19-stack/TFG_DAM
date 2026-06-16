export interface Trabajador {
  id: number;
  name?: string;
  surname?: string;
  email?: string;
  phoneNumber?: string;
  dni?: string;
  birthDate?: string;
  entryDate?: string;
  contractType?: string;
  active?: boolean;
  outDate?: string | null;
  reason?: string | null;
  servicioId?: number;
  servicioOutDto?: {
    id: number;
    description?: string;
  };
}
