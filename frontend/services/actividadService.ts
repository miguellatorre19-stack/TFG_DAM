import { ApiError, apiFetch } from "./api";
import type { Actividad } from "@/types/actividad";
import type {
  InscripcionActividad,
  InscripcionPayload,
} from "@/types/inscripcion";

export interface ActividadFormData {
  description: string;
  dayActivity: string;
  typeActivity: string;
  duration: number;
  canJoin: boolean;
  capacity: number;
  longitude: number;
  latitude: number;
}

export async function getActividades(): Promise<Actividad[]> {
  try {
    return await apiFetch<Actividad[]>("/actividades");
  } catch (error) {
    if (error instanceof ApiError && error.status === 404) {
      return [];
    }

    throw error;
  }
}

export async function createActividad(
  data: ActividadFormData
): Promise<Actividad> {
  return apiFetch<Actividad>("/actividades", {
    method: "POST",
    body: JSON.stringify(data),
  });
}

export async function updateActividad(
  id: number,
  data: ActividadFormData
): Promise<Actividad> {
  return apiFetch<Actividad>(`/actividades/${id}`, {
    method: "PUT",
    body: JSON.stringify(data),
  });
}

export async function deleteActividad(id: number): Promise<void> {
  await apiFetch<void>(`/actividades/${id}`, {
    method: "DELETE",
  });
}

export async function getInscripcionesActividad(
  actividadId: number
): Promise<InscripcionActividad[]> {
  return apiFetch<InscripcionActividad[]>(
    `/actividades/${actividadId}/inscripciones`
  );
}

export async function inscribirActividad(
  actividadId: number,
  participanteId: number
): Promise<void> {
  return createInscripcionActividad(actividadId, {
    participanteId,
    state: "ENVIADA",
    price: 0,
  });
}

export async function createInscripcionActividad(
  actividadId: number,
  data: InscripcionPayload
): Promise<void> {
  await apiFetch<void>(`/actividades/${actividadId}/inscripciones`, {
    method: "POST",
    body: JSON.stringify(data),
  });
}

export async function updateInscripcionActividad(
  actividadId: number,
  inscripcionId: number,
  data: InscripcionPayload
): Promise<InscripcionActividad> {
  return apiFetch<InscripcionActividad>(
    `/actividades/${actividadId}/inscripciones/${inscripcionId}`,
    {
      method: "PUT",
      body: JSON.stringify(data),
    }
  );
}

export async function deleteInscripcionActividad(
  actividadId: number,
  inscripcionId: number
): Promise<void> {
  await apiFetch<void>(`/actividades/${actividadId}/inscripciones/${inscripcionId}`, {
    method: "DELETE",
  });
}
