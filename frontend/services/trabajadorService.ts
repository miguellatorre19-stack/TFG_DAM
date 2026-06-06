import { apiFetch } from "./api";
import type { Trabajador } from "@/types/trabajador";
import type {
  IssuedAccessCredentials,
  TrabajadorAccessResponse,
} from "@/types/access";

export async function getTrabajadores(): Promise<Trabajador[]> {
  try {
    return await apiFetch<Trabajador[]>("/trabajadores");
  } catch (error) {
    if (error instanceof Error && error.message.includes("Error HTTP 404")) {
      return [];
    }

    throw error;
  }
}

export interface TrabajadorFormData {
  dni: string;
  name: string;
  surname: string;
  email: string;
  phoneNumber: string;
  birthDate: string;
  contractType: string;
  servicioId: number;
}

export async function createTrabajador(
  servicioId: number,
  data: TrabajadorFormData
): Promise<TrabajadorAccessResponse> {
  return apiFetch<TrabajadorAccessResponse>(`/servicios/${servicioId}/trabajadores`, {
    method: "POST",
    body: JSON.stringify(data),
  });
}

export async function regenerateTrabajadorAccessCode(
  id: number
): Promise<IssuedAccessCredentials> {
  return apiFetch<IssuedAccessCredentials>(`/trabajadores/${id}/access-code`, {
    method: "POST",
  });
}
