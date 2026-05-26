import { apiFetch } from "./api";
import type { Participante } from "@/types/participante";

export async function getParticipantes(): Promise<Participante[]> {
  return apiFetch<Participante[]>("/participantes");
}