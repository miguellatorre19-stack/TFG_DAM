import { apiFetch } from "./api";
import type { Socio } from "@/types/socio";

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