import { apiFetch } from "./api";
import type { Socio } from "@/types/socio";

export interface SocioFormData {
  name: string;
  surname: string;
  email: string;
  phoneNumber: string;
  dni: string;
  active: boolean;
  familyModel: string;
  entryDate: string;
}

export async function getSocios(): Promise<Socio[]> {
  try {
    return await apiFetch<Socio[]>("/socios");
  } catch (error) {
    if (error instanceof Error && error.message.includes("Error HTTP 404")) {
      return [];
    }

    throw error;
  }
}

export async function createSocio(data: SocioFormData): Promise<Socio> {
  return apiFetch<Socio>("/socios", {
    method: "POST",
    body: JSON.stringify(data),
  });
}

export async function updateSocio(id: number, data: SocioFormData): Promise<Socio> {
  return apiFetch<Socio>(`/socios/${id}`, {
    method: "PUT",
    body: JSON.stringify(data),
  });
}

export async function deleteSocio(id: number): Promise<void> {
  return apiFetch<void>(`/socios/${id}`, {
    method: "DELETE",
  });
}