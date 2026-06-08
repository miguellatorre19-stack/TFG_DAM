"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import AppNav from "@/components/AppNav";
import {
  deleteInscripcionActividad,
  getActividades,
  getInscripcionesActividad,
} from "@/services/actividadService";
import { canAccessAdminPanel, getUser } from "@/services/authService";
import { getParticipantes } from "@/services/participanteService";
import {
  deleteInscripcionServicio,
  getInscripcionesServicio,
  getServicios,
} from "@/services/servicioService";
import type { Actividad } from "@/types/actividad";
import type { InscripcionActividad, InscripcionServicio } from "@/types/inscripcion";
import type { Participante } from "@/types/participante";
import type { Servicio } from "@/types/servicio";

export default function InscripcionesPage() {
  const router = useRouter();

  const [actividades, setActividades] = useState<Actividad[]>([]);
  const [servicios, setServicios] = useState<Servicio[]>([]);
  const [participantes, setParticipantes] = useState<Participante[]>([]);

  const [selectedActividadId, setSelectedActividadId] = useState(0);
  const [selectedServicioId, setSelectedServicioId] = useState(0);

  const [inscripcionesActividad, setInscripcionesActividad] = useState<InscripcionActividad[]>([]);
  const [inscripcionesServicio, setInscripcionesServicio] = useState<InscripcionServicio[]>([]);

  const [loading, setLoading] = useState(true);
  const [loadingActividadInscripciones, setLoadingActividadInscripciones] = useState(false);
  const [loadingServicioInscripciones, setLoadingServicioInscripciones] = useState(false);
  const [error, setError] = useState("");
  const [actividadError, setActividadError] = useState("");
  const [servicioError, setServicioError] = useState("");
  const [actividadSuccess, setActividadSuccess] = useState("");
  const [servicioSuccess, setServicioSuccess] = useState("");

  const actividadSeleccionada = actividades.find((actividad) => actividad.id === selectedActividadId);
  const servicioSeleccionado = servicios.find((servicio) => servicio.id === selectedServicioId);

  useEffect(() => {
    const user = getUser();

    if (!user) {
      router.push("/login");
      return;
    }

    if (!canAccessAdminPanel(user)) {
      router.push("/area-privada");
      return;
    }

    const timeoutId = window.setTimeout(() => {
      loadBaseData();
    }, 0);

    return () => window.clearTimeout(timeoutId);
  }, [router]);

  useEffect(() => {
    if (selectedActividadId > 0) {
      void loadActividadInscripciones(selectedActividadId);
    }
  }, [selectedActividadId]);

  useEffect(() => {
    if (selectedServicioId > 0) {
      void loadServicioInscripciones(selectedServicioId);
    }
  }, [selectedServicioId]);

  async function loadBaseData() {
    setLoading(true);
    setError("");

    try {
      const [actividadesData, serviciosData, participantesData] = await Promise.all([
        getActividades(),
        getServicios(),
        getParticipantes(),
      ]);

      setActividades(actividadesData);
      setServicios(serviciosData);
      setParticipantes(participantesData);
      setSelectedActividadId((current) => current || actividadesData[0]?.id || 0);
      setSelectedServicioId((current) => current || serviciosData[0]?.id || 0);
    } catch (error) {
      console.error(error);
      setError("No se han podido cargar los datos base de inscripciones.");
    } finally {
      setLoading(false);
    }
  }

  async function loadActividadInscripciones(actividadId: number) {
    setLoadingActividadInscripciones(true);
    setActividadError("");

    try {
      const data = await getInscripcionesActividad(actividadId);
      setInscripcionesActividad(data);
    } catch (error) {
      console.error(error);
      setActividadError(
        error instanceof Error
          ? error.message
          : "No se han podido cargar las inscripciones de esta actividad."
      );
    } finally {
      setLoadingActividadInscripciones(false);
    }
  }

  async function loadServicioInscripciones(servicioId: number) {
    setLoadingServicioInscripciones(true);
    setServicioError("");

    try {
      const data = await getInscripcionesServicio(servicioId);
      setInscripcionesServicio(data);
    } catch (error) {
      console.error(error);
      setServicioError(
        error instanceof Error
          ? error.message
          : "No se han podido cargar las inscripciones de este servicio."
      );
    } finally {
      setLoadingServicioInscripciones(false);
    }
  }

  function getParticipanteLabel(participanteId: number) {
    const participante = participantes.find((item) => item.id === participanteId);

    if (!participante) {
      return `Participante ${participanteId}`;
    }

    return [participante.name, participante.surname].filter(Boolean).join(" ") || `Participante ${participante.id}`;
  }

  async function handleDeleteActividadInscripcion(inscripcionId: number) {
    if (!selectedActividadId) {
      return;
    }

    if (!window.confirm("Se eliminara esta inscripcion de actividad.")) {
      return;
    }

    setActividadError("");
    setActividadSuccess("");

    try {
      await deleteInscripcionActividad(selectedActividadId, inscripcionId);
      setActividadSuccess("Inscripcion de actividad eliminada correctamente.");
      await loadActividadInscripciones(selectedActividadId);
    } catch (error) {
      console.error(error);
      setActividadError(
        error instanceof Error
          ? error.message
          : "No se ha podido eliminar la inscripcion de actividad."
      );
    }
  }

  async function handleDeleteServicioInscripcion(inscripcionId: number) {
    if (!selectedServicioId) {
      return;
    }

    if (!window.confirm("Se eliminara esta inscripcion de servicio.")) {
      return;
    }

    setServicioError("");
    setServicioSuccess("");

    try {
      await deleteInscripcionServicio(selectedServicioId, inscripcionId);
      setServicioSuccess("Inscripcion de servicio eliminada correctamente.");
      await loadServicioInscripciones(selectedServicioId);
    } catch (error) {
      console.error(error);
      setServicioError(
        error instanceof Error
          ? error.message
          : "No se ha podido eliminar la inscripcion de servicio."
      );
    }
  }

  if (loading) {
    return (
      <main className="flex min-h-screen items-center justify-center bg-slate-100">
        <p className="text-sm text-slate-600">Cargando panel de inscripciones...</p>
      </main>
    );
  }

  return (
    <main className="min-h-screen bg-slate-100">
      <AppNav />

      <section className="mx-auto max-w-7xl px-6 py-8">
        <div className="mb-6">
          <h2 className="text-2xl font-bold text-slate-900">Inscripciones</h2>
          <p className="mt-2 text-slate-600">
            El panel de administracion solo permite consultar y eliminar inscripciones ya registradas.
          </p>
        </div>

        {error && (
          <p className="mb-6 rounded-lg bg-red-50 px-3 py-2 text-sm text-red-700">
            {error}
          </p>
        )}

        <div className="grid gap-6 xl:grid-cols-2">
          <section className="min-w-0 rounded-2xl bg-white p-6 shadow">
            <div className="mb-5 flex items-start justify-between gap-4">
              <div>
                <h3 className="text-lg font-semibold text-slate-900">Inscripciones a actividades</h3>
                <p className="mt-1 text-sm text-slate-600">
                  Selecciona una actividad para ver las solicitudes registradas.
                </p>
              </div>
              <select
                className="min-w-56 rounded-lg border border-slate-300 px-3 py-2 text-sm text-slate-900"
                value={selectedActividadId}
                onChange={(event) => {
                  const actividadId = Number(event.target.value);
                  setSelectedActividadId(actividadId);
                  setActividadError("");
                  setActividadSuccess("");
                  if (!actividadId) {
                    setInscripcionesActividad([]);
                  }
                }}
              >
                <option value={0}>Selecciona una actividad</option>
                {actividades.map((actividad) => (
                  <option key={actividad.id} value={actividad.id}>
                    {actividad.description || `Actividad ${actividad.id}`}
                  </option>
                ))}
              </select>
            </div>

            {actividadSeleccionada && (
              <div className="mb-5 rounded-xl bg-slate-50 p-4 text-sm text-slate-700">
                <p><strong>Tipo:</strong> {actividadSeleccionada.typeActivity ?? "-"}</p>
                <p><strong>Fecha:</strong> {actividadSeleccionada.dayActivity ?? "-"}</p>
                <p><strong>Capacidad:</strong> {actividadSeleccionada.capacity ?? "-"}</p>
              </div>
            )}

            {actividadError && (
              <p className="mb-4 rounded-lg bg-red-50 px-3 py-2 text-sm text-red-700">
                {actividadError}
              </p>
            )}

            {actividadSuccess && (
              <p className="mb-4 rounded-lg bg-emerald-50 px-3 py-2 text-sm text-emerald-700">
                {actividadSuccess}
              </p>
            )}

            <div className="overflow-hidden rounded-xl border border-slate-200">
              {loadingActividadInscripciones && (
                <p className="p-4 text-sm text-slate-600">Cargando inscripciones...</p>
              )}

              {!loadingActividadInscripciones && inscripcionesActividad.length === 0 && (
                <p className="p-4 text-sm text-slate-600">
                  No hay inscripciones registradas para esta actividad.
                </p>
              )}

              {!loadingActividadInscripciones && inscripcionesActividad.length > 0 && (
                <table className="w-full border-collapse text-left text-sm">
                  <thead className="bg-slate-50 text-slate-700">
                    <tr>
                      <th className="px-4 py-3 font-semibold">ID</th>
                      <th className="px-4 py-3 font-semibold">Participante</th>
                      <th className="px-4 py-3 font-semibold">Estado</th>
                      <th className="px-4 py-3 font-semibold">Precio</th>
                      <th className="px-4 py-3 font-semibold">Fecha</th>
                      <th className="px-4 py-3 font-semibold">Acciones</th>
                    </tr>
                  </thead>
                  <tbody className="divide-y divide-slate-100">
                    {inscripcionesActividad.map((inscripcion) => (
                      <tr key={inscripcion.id}>
                        <td className="px-4 py-3 text-slate-600">{inscripcion.id}</td>
                        <td className="px-4 py-3 text-slate-900">{getParticipanteLabel(inscripcion.participanteId)}</td>
                        <td className="px-4 py-3 text-slate-600">{inscripcion.state ?? "-"}</td>
                        <td className="px-4 py-3 text-slate-600">{inscripcion.price ?? 0} €</td>
                        <td className="px-4 py-3 text-slate-600">{inscripcion.createdAt ?? "-"}</td>
                        <td className="px-4 py-3">
                          <button
                            type="button"
                            onClick={() => handleDeleteActividadInscripcion(inscripcion.id)}
                            className="rounded-lg border border-red-200 px-3 py-1 text-xs font-medium text-red-700 hover:bg-red-50"
                          >
                            Eliminar
                          </button>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              )}
            </div>
          </section>

          <section className="min-w-0 rounded-2xl bg-white p-6 shadow">
            <div className="mb-5 flex items-start justify-between gap-4">
              <div>
                <h3 className="text-lg font-semibold text-slate-900">Inscripciones a servicios</h3>
                <p className="mt-1 text-sm text-slate-600">
                  Selecciona un servicio para ver las solicitudes registradas.
                </p>
              </div>
              <select
                className="min-w-56 rounded-lg border border-slate-300 px-3 py-2 text-sm text-slate-900"
                value={selectedServicioId}
                onChange={(event) => {
                  const servicioId = Number(event.target.value);
                  setSelectedServicioId(servicioId);
                  setServicioError("");
                  setServicioSuccess("");
                  if (!servicioId) {
                    setInscripcionesServicio([]);
                  }
                }}
              >
                <option value={0}>Selecciona un servicio</option>
                {servicios.map((servicio) => (
                  <option key={servicio.id} value={servicio.id}>
                    {servicio.description || `Servicio ${servicio.id}`}
                  </option>
                ))}
              </select>
            </div>

            {servicioSeleccionado && (
              <div className="mb-5 rounded-xl bg-slate-50 p-4 text-sm text-slate-700">
                <p><strong>Periodicidad:</strong> {servicioSeleccionado.periodicity ?? "-"}</p>
                <p><strong>Requisitos:</strong> {servicioSeleccionado.requisites ?? "-"}</p>
                <p><strong>Capacidad:</strong> {servicioSeleccionado.capacity ?? "-"}</p>
              </div>
            )}

            {servicioError && (
              <p className="mb-4 rounded-lg bg-red-50 px-3 py-2 text-sm text-red-700">
                {servicioError}
              </p>
            )}

            {servicioSuccess && (
              <p className="mb-4 rounded-lg bg-emerald-50 px-3 py-2 text-sm text-emerald-700">
                {servicioSuccess}
              </p>
            )}

            <div className="overflow-hidden rounded-xl border border-slate-200">
              {loadingServicioInscripciones && (
                <p className="p-4 text-sm text-slate-600">Cargando inscripciones...</p>
              )}

              {!loadingServicioInscripciones && inscripcionesServicio.length === 0 && (
                <p className="p-4 text-sm text-slate-600">
                  No hay inscripciones registradas para este servicio.
                </p>
              )}

              {!loadingServicioInscripciones && inscripcionesServicio.length > 0 && (
                <table className="w-full border-collapse text-left text-sm">
                  <thead className="bg-slate-50 text-slate-700">
                    <tr>
                      <th className="px-4 py-3 font-semibold">ID</th>
                      <th className="px-4 py-3 font-semibold">Participante</th>
                      <th className="px-4 py-3 font-semibold">Estado</th>
                      <th className="px-4 py-3 font-semibold">Precio</th>
                      <th className="px-4 py-3 font-semibold">Fecha</th>
                      <th className="px-4 py-3 font-semibold">Acciones</th>
                    </tr>
                  </thead>
                  <tbody className="divide-y divide-slate-100">
                    {inscripcionesServicio.map((inscripcion) => (
                      <tr key={inscripcion.id}>
                        <td className="px-4 py-3 text-slate-600">{inscripcion.id}</td>
                        <td className="px-4 py-3 text-slate-900">{getParticipanteLabel(inscripcion.participanteId)}</td>
                        <td className="px-4 py-3 text-slate-600">{inscripcion.state ?? "-"}</td>
                        <td className="px-4 py-3 text-slate-600">{inscripcion.price ?? 0} €</td>
                        <td className="px-4 py-3 text-slate-600">{inscripcion.createdAt ?? "-"}</td>
                        <td className="px-4 py-3">
                          <button
                            type="button"
                            onClick={() => handleDeleteServicioInscripcion(inscripcion.id)}
                            className="rounded-lg border border-red-200 px-3 py-1 text-xs font-medium text-red-700 hover:bg-red-50"
                          >
                            Eliminar
                          </button>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              )}
            </div>
          </section>
        </div>
      </section>
    </main>
  );
}
