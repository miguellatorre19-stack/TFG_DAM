import { ApiError, apiFetch } from "./api";
import type { Servicio } from "@/types/servicio";
import type {
  InscripcionPayload,
  InscripcionServicio,
} from "@/types/inscripcion";

export interface ServicioFormData {
  description: string;
  periodicity: string;
  requisites: string;
  duration: number;
  capacity: number;
}

export async function getServicios(): Promise<Servicio[]> {
  try {
    return await apiFetch<Servicio[]>("/servicios");
  } catch (error) {
    if (error instanceof ApiError && error.status === 404) {
      return [];
    }

    throw error;
  }
}

export async function createServicio(data: ServicioFormData): Promise<Servicio> {
  return apiFetch<Servicio>("/servicios", {
    method: "POST",
    body: JSON.stringify(data),
  });
}

export async function updateServicio(
  id: number,
  data: ServicioFormData
): Promise<Servicio> {
  return apiFetch<Servicio>(`/servicios/${id}`, {
    method: "PUT",
    body: JSON.stringify(data),
  });
}

export async function deleteServicio(id: number): Promise<void> {
  await apiFetch<void>(`/servicios/${id}`, {
    method: "DELETE",
  });
}

export async function getInscripcionesServicio(
  servicioId: number
): Promise<InscripcionServicio[]> {
  return apiFetch<InscripcionServicio[]>(`/servicios/${servicioId}/inscripciones`);
}

export async function inscribirServicio(
  servicioId: number,
  participanteId: number
): Promise<void> {
  return createInscripcionServicio(servicioId, {
    participanteId,
    state: "ENVIADA",
    price: 0,
  });
}

export async function createInscripcionServicio(
  servicioId: number,
  data: InscripcionPayload
): Promise<void> {
  await apiFetch<void>(`/servicios/${servicioId}/inscripciones`, {
    method: "POST",
    body: JSON.stringify(data),
  });
}

export async function updateInscripcionServicio(
  servicioId: number,
  inscripcionId: number,
  data: InscripcionPayload
): Promise<InscripcionServicio> {
  return apiFetch<InscripcionServicio>(
    `/servicios/${servicioId}/inscripciones/${inscripcionId}`,
    {
      method: "PUT",
      body: JSON.stringify(data),
    }
  );
}

export async function deleteInscripcionServicio(
  servicioId: number,
  inscripcionId: number
): Promise<void> {
  await apiFetch<void>(`/servicios/${servicioId}/inscripciones/${inscripcionId}`, {
    method: "DELETE",
  });
}
