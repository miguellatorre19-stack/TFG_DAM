import { apiFetch } from "./api";
import type { Trabajador } from "@/types/trabajador";

export async function getTrabajadores(): Promise<Trabajador[]> {
  return apiFetch<Trabajador[]>("/trabajadores");
}