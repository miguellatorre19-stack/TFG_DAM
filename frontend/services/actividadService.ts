import { apiFetch } from "./api";
import type { Actividad } from "@/types/actividad";

export async function getActividades(): Promise<Actividad[]> {
  try {
    return await apiFetch<Actividad[]>("/actividades");
  } catch (error) {
    if (error instanceof Error && error.message.includes("Error HTTP 404")) {
      return [];
    }

    throw error;
  }
}

export async function inscribirActividad(
  actividadId: number,
  participanteId: number
): Promise<void> {
  await apiFetch<void>(`/actividades/${actividadId}/inscripciones`, {
    method: "POST",
    body: JSON.stringify({
      participanteId,
      state: "ENVIADA",
      price: 0,
    }),
  });
}
