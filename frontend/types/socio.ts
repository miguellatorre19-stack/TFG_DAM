export interface Socio {
  id: number;
  name?: string;
  surname?: string;
  email?: string;
  phoneNumber?: string;
  dni?: string;
  active?: boolean;
  entryDate?: string;
  outDate?: string | null;
  reason?: string | null;
  familyModel?: string;
}
