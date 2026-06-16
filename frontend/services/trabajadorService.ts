import { ApiError, apiFetch } from "./api";
import type { Trabajador } from "@/types/trabajador";
import type {
  IssuedAccessCredentials,
  TrabajadorAccessResponse,
} from "@/types/access";
import type { BajaRequestData } from "@/types/lifecycle";

export async function getTrabajadores(active?: boolean): Promise<Trabajador[]> {
  try {
    const searchParams = new URLSearchParams();

    if (typeof active === "boolean") {
      searchParams.set("active", String(active));
    }

    const query = searchParams.toString();
    return await apiFetch<Trabajador[]>(query ? `/trabajadores?${query}` : "/trabajadores");
  } catch (error) {
    if (error instanceof ApiError && error.status === 404) {
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
  const payload = buildTrabajadorPayload(data);

  return apiFetch<TrabajadorAccessResponse>(`/servicios/${servicioId}/trabajadores`, {
    method: "POST",
    body: JSON.stringify(payload),
  });
}

export async function updateTrabajador(
  id: number,
  data: TrabajadorFormData
): Promise<Trabajador> {
  return apiFetch<Trabajador>(`/trabajadores/${id}`, {
    method: "PUT",
    body: JSON.stringify(buildTrabajadorPayload(data)),
  });
}

export async function darDeBajaTrabajador(
  id: number,
  data: BajaRequestData
): Promise<void> {
  await apiFetch<void>(`/trabajadores/${id}/baja`, {
    method: "POST",
    body: JSON.stringify(data),
  });
}

export async function reactivarTrabajador(id: number): Promise<void> {
  await apiFetch<void>(`/trabajadores/${id}/reactivar`, {
    method: "POST",
  });
}

export async function regenerateTrabajadorAccessCode(
  id: number
): Promise<IssuedAccessCredentials> {
  return apiFetch<IssuedAccessCredentials>(`/trabajadores/${id}/access-code`, {
    method: "POST",
  });
}

function buildTrabajadorPayload(data: TrabajadorFormData) {
  return {
    ...data,
    birthDate: data.birthDate || undefined,
  };
}
