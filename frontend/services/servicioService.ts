import { apiFetch } from "./api";
import type { Servicio } from "@/types/servicio";

export async function getServicios(): Promise<Servicio[]> {
  try {
    return await apiFetch<Servicio[]>("/servicios");
  } catch (error) {
    if (error instanceof Error && error.message.includes("Error HTTP 404")) {
      return [];
    }

    throw error;
  }
}

export async function inscribirServicio(
  servicioId: number,
  participanteId: number
): Promise<void> {
  await apiFetch<void>(`/servicios/${servicioId}/inscripciones`, {
    method: "POST",
    body: JSON.stringify({
      participanteId,
      state: "ENVIADA",
      price: 0,
    }),
  });
}
