import { apiFetch } from "./api";
import type { Servicio } from "@/types/servicio";

export async function getServicios(): Promise<Servicio[]> {
  return apiFetch<Servicio[]>("/servicios");
}