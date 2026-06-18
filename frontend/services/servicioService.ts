import { ApiError, apiFetch } from "./api";
import type { Servicio } from "@/types/servicio";
import type {
  InscripcionPayload,
  SolicitudServicio,
} from "@/types/inscripcion";

export interface ServicioFormData {
  description: string;
  periodicity: string;
  requisites: string;
  duration: number;
  capacity: number;
}

export async function getServicios(archived?: boolean): Promise<Servicio[]> {
  try {
    const searchParams = new URLSearchParams();

    if (typeof archived === "boolean") {
      searchParams.set("archived", String(archived));
    }

    const query = searchParams.toString();
    return await apiFetch<Servicio[]>(query ? `/servicios?${query}` : "/servicios");
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

export async function getSolicitudesServicio(
  servicioId: number
): Promise<SolicitudServicio[]> {
  return apiFetch<SolicitudServicio[]>(`/servicios/${servicioId}/solicitudes`);
}

export async function solicitarServicio(
  servicioId: number,
  participanteId: number
): Promise<void> {
  return createSolicitudServicio(servicioId, {
    participanteId,
    state: "PENDING",
    price: 0,
  });
}

export async function createSolicitudServicio(
  servicioId: number,
  data: InscripcionPayload
): Promise<void> {
  await apiFetch<void>(`/servicios/${servicioId}/solicitudes`, {
    method: "POST",
    body: JSON.stringify(data),
  });
}

export async function updateSolicitudServicio(
  servicioId: number,
  solicitudId: number,
  data: InscripcionPayload
): Promise<SolicitudServicio> {
  return apiFetch<SolicitudServicio>(
    `/servicios/${servicioId}/solicitudes/${solicitudId}`,
    {
      method: "PUT",
      body: JSON.stringify(data),
    }
  );
}

export async function cancelarSolicitudServicio(
  servicioId: number,
  solicitudId: number
): Promise<void> {
  await apiFetch<void>(`/servicios/${servicioId}/solicitudes/${solicitudId}`, {
    method: "DELETE",
  });
}
