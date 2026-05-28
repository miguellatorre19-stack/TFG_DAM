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