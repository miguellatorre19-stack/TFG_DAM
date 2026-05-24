import { apiFetch } from "./api";
import type { Socio } from "@/types/socio";

export async function getSocios(): Promise<Socio[]> {
  return apiFetch<Socio[]>("/socios");
}