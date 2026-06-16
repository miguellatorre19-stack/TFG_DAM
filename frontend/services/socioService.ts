import { ApiError, apiFetch } from "./api";
import type { Socio } from "@/types/socio";
import type { IssuedAccessCredentials, SocioAccessResponse } from "@/types/access";
import type { BajaRequestData } from "@/types/lifecycle";

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

export async function getSocios(active?: boolean): Promise<Socio[]> {
  try {
    const searchParams = new URLSearchParams();

    if (typeof active === "boolean") {
      searchParams.set("active", String(active));
    }

    const query = searchParams.toString();
    return await apiFetch<Socio[]>(query ? `/socios?${query}` : "/socios");
  } catch (error) {
    if (error instanceof ApiError && error.status === 404) {
      return [];
    }

    throw error;
  }
}

export async function createSocio(data: SocioFormData): Promise<SocioAccessResponse> {
  return apiFetch<SocioAccessResponse>("/socios", {
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

export async function darDeBajaSocio(
  id: number,
  data: BajaRequestData
): Promise<void> {
  return apiFetch<void>(`/socios/${id}/baja`, {
    method: "POST",
    body: JSON.stringify(data),
  });
}

export async function reactivarSocio(id: number): Promise<void> {
  return apiFetch<void>(`/socios/${id}/reactivar`, {
    method: "POST",
  });
}

export async function regenerateSocioAccessCode(
  id: number
): Promise<IssuedAccessCredentials> {
  return apiFetch<IssuedAccessCredentials>(`/socios/${id}/access-code`, {
    method: "POST",
  });
}
