import { ApiError, apiFetch } from "./api";
import type { Participante } from "@/types/participante";
import type {
  IssuedAccessCredentials,
  ParticipanteAccessResponse,
} from "@/types/access";
import type { BajaRequestData } from "@/types/lifecycle";

export async function getParticipantes(active?: boolean): Promise<Participante[]> {
  try {
    const searchParams = new URLSearchParams();

    if (typeof active === "boolean") {
      searchParams.set("active", String(active));
    }

    const query = searchParams.toString();
    return await apiFetch<Participante[]>(query ? `/participantes?${query}` : "/participantes");
  } catch (error) {
    if (error instanceof ApiError && error.status === 404) {
      return [];
    }

    throw error;
  }
}

export interface ParticipanteFormData {
  dni: string;
  name: string;
  surname: string;
  email: string;
  phoneNumber: string;
  birthDate: string;
  needs: string;
  typeRel: string;
  socioID: number;
}

export async function createParticipante(
  socioId: number,
  data: ParticipanteFormData
): Promise<ParticipanteAccessResponse> {
  const payload = buildParticipantePayload(data);

  return apiFetch<ParticipanteAccessResponse>(`/socios/${socioId}/participante`, {
    method: "POST",
    body: JSON.stringify(payload),
  });
}

export async function updateParticipante(
  id: number,
  data: ParticipanteFormData
): Promise<Participante> {
  return apiFetch<Participante>(`/participantes/${id}`, {
    method: "PUT",
    body: JSON.stringify(buildParticipantePayload(data)),
  });
}

export async function darDeBajaParticipante(
  id: number,
  data: BajaRequestData
): Promise<void> {
  await apiFetch<void>(`/participantes/${id}/baja`, {
    method: "POST",
    body: JSON.stringify(data),
  });
}

export async function reactivarParticipante(id: number): Promise<void> {
  await apiFetch<void>(`/participantes/${id}/reactivar`, {
    method: "POST",
  });
}

export async function regenerateParticipanteAccessCode(
  id: number
): Promise<IssuedAccessCredentials> {
  return apiFetch<IssuedAccessCredentials>(`/participantes/${id}/access-code`, {
    method: "POST",
  });
}

function buildParticipantePayload(data: ParticipanteFormData) {
  return {
    ...data,
    phoneNumber: data.phoneNumber || undefined,
    birthDate: data.birthDate || undefined,
  };
}
