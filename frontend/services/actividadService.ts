import { apiFetch } from "./api";
import type { Actividad } from "@/types/actividad";

export async function getActividades(): Promise<Actividad[]> {
  return apiFetch<Actividad[]>("/actividades");
}