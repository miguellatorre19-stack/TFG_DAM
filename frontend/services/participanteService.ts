import { apiFetch } from "./api";
import type { Participante } from "@/types/participante";
import type {
  IssuedAccessCredentials,
  ParticipanteAccessResponse,
} from "@/types/access";

export async function getParticipantes(): Promise<Participante[]> {
  try {
    return await apiFetch<Participante[]>("/participantes");
  } catch (error) {
    if (error instanceof Error && error.message.includes("Error HTTP 404")) {
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
  return apiFetch<ParticipanteAccessResponse>(`/socios/${socioId}/participante`, {
    method: "POST",
    body: JSON.stringify(data),
  });
}

export async function regenerateParticipanteAccessCode(
  id: number
): Promise<IssuedAccessCredentials> {
  return apiFetch<IssuedAccessCredentials>(`/participantes/${id}/access-code`, {
    method: "POST",
  });
}
