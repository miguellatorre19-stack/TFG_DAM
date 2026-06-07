"use client";

import { FormEvent, useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import AppNav from "@/components/AppNav";
import { canAccessAdminPanel, getUser } from "@/services/authService";
import {
  createActividad,
  getActividades,
  updateActividad,
  type ActividadFormData,
} from "@/services/actividadService";
import type { Actividad } from "@/types/actividad";

function createEmptyForm(): ActividadFormData {
  return {
    description: "",
    dayActivity: new Date().toISOString().slice(0, 10),
    typeActivity: "",
    duration: 1,
    canJoin: true,
    capacity: 1,
    longitude: 0,
    latitude: 0,
  };
}

export default function ActividadesPage() {
  const router = useRouter();

  const [actividades, setActividades] = useState<Actividad[]>([]);
  const [formData, setFormData] = useState<ActividadFormData>(() => createEmptyForm());
  const [editingActividadId, setEditingActividadId] = useState<number | null>(null);

  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");
  const [successMessage, setSuccessMessage] = useState("");

  async function loadActividades() {
    setLoading(true);
    setError("");

    try {
      const data = await getActividades();
      setActividades(data);
    } catch (error) {
      console.error(error);
      setError("No se han podido cargar las actividades.");
    } finally {
      setLoading(false);
    }
  }

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
      loadActividades();
    }, 0);

    return () => window.clearTimeout(timeoutId);
  }, [router]);

  function handleInputChange(
    field: keyof ActividadFormData,
    value: string | number | boolean
  ) {
    setFormData((current) => ({
      ...current,
      [field]: value,
    }));
  }

  function handleEdit(actividad: Actividad) {
    setEditingActividadId(actividad.id);
    setSuccessMessage("");
    setError("");
    setFormData({
      description: actividad.description ?? "",
      dayActivity: actividad.dayActivity ?? new Date().toISOString().slice(0, 10),
      typeActivity: actividad.typeActivity ?? "",
      duration: actividad.duration ?? 1,
      canJoin: actividad.canJoin ?? true,
      capacity: actividad.capacity ?? 1,
      longitude: actividad.longitude ?? 0,
      latitude: actividad.latitude ?? 0,
    });

    window.scrollTo({ top: 0, behavior: "smooth" });
  }

  function handleCancelEdit() {
    setEditingActividadId(null);
    setFormData(createEmptyForm());
    setError("");
    setSuccessMessage("");
  }

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setSaving(true);
    setError("");
    setSuccessMessage("");

    try {
      if (editingActividadId) {
        await updateActividad(editingActividadId, formData);
        setSuccessMessage("Actividad actualizada correctamente.");
      } else {
        await createActividad(formData);
        setSuccessMessage("Actividad creada correctamente.");
      }

      setFormData(createEmptyForm());
      setEditingActividadId(null);
      await loadActividades();
    } catch (error) {
      console.error(error);
      setError(
        error instanceof Error
          ? error.message
          : "No se ha podido guardar la actividad. Revisa los campos."
      );
    } finally {
      setSaving(false);
    }
  }

  return (
    <main className="min-h-screen bg-slate-100">
      <AppNav />

      <section className="mx-auto max-w-6xl px-6 py-8">
        <div className="mb-6">
          <h2 className="text-2xl font-bold text-slate-900">Actividades</h2>
          <p className="mt-2 text-slate-600">
            Gestion completa de talleres, eventos y actividades ofertadas por la asociacion.
          </p>
        </div>

        <form
          onSubmit={handleSubmit}
          className="mb-8 rounded-2xl bg-white p-6 shadow"
        >
          <div className="mb-5">
            <h3 className="text-lg font-semibold text-slate-900">
              {editingActividadId ? "Editar actividad" : "Nueva actividad"}
            </h3>
            <p className="mt-1 text-sm text-slate-600">
              {editingActividadId
                ? "Actualiza la ficha completa de la actividad."
                : "Crea una nueva actividad desde el panel de administracion."}
            </p>
          </div>

          <div className="grid gap-4 md:grid-cols-2">
            <label className="block md:col-span-2">
              <span className="mb-1 block text-sm font-medium text-slate-700">
                Descripcion
              </span>
              <input
                className="w-full rounded-lg border border-slate-300 px-3 py-2 text-slate-900 outline-none focus:border-indigo-500"
                value={formData.description}
                onChange={(event) =>
                  handleInputChange("description", event.target.value)
                }
                required
              />
            </label>

            <label className="block">
              <span className="mb-1 block text-sm font-medium text-slate-700">
                Tipo de actividad
              </span>
              <input
                className="w-full rounded-lg border border-slate-300 px-3 py-2 text-slate-900 outline-none focus:border-indigo-500"
                value={formData.typeActivity}
                onChange={(event) =>
                  handleInputChange("typeActivity", event.target.value)
                }
                required
              />
            </label>

            <label className="block">
              <span className="mb-1 block text-sm font-medium text-slate-700">
                Fecha
              </span>
              <input
                type="date"
                className="w-full rounded-lg border border-slate-300 px-3 py-2 text-slate-900 outline-none focus:border-indigo-500"
                value={formData.dayActivity}
                onChange={(event) =>
                  handleInputChange("dayActivity", event.target.value)
                }
                required
              />
            </label>

            <label className="block">
              <span className="mb-1 block text-sm font-medium text-slate-700">
                Duracion
              </span>
              <input
                type="number"
                min="0.5"
                step="0.5"
                className="w-full rounded-lg border border-slate-300 px-3 py-2 text-slate-900 outline-none focus:border-indigo-500"
                value={formData.duration}
                onChange={(event) =>
                  handleInputChange("duration", Number(event.target.value))
                }
                required
              />
            </label>

            <label className="block">
              <span className="mb-1 block text-sm font-medium text-slate-700">
                Capacidad
              </span>
              <input
                type="number"
                min="1"
                className="w-full rounded-lg border border-slate-300 px-3 py-2 text-slate-900 outline-none focus:border-indigo-500"
                value={formData.capacity}
                onChange={(event) =>
                  handleInputChange("capacity", Number(event.target.value))
                }
                required
              />
            </label>

            <label className="block">
              <span className="mb-1 block text-sm font-medium text-slate-700">
                Longitud
              </span>
              <input
                type="number"
                step="any"
                className="w-full rounded-lg border border-slate-300 px-3 py-2 text-slate-900 outline-none focus:border-indigo-500"
                value={formData.longitude}
                onChange={(event) =>
                  handleInputChange("longitude", Number(event.target.value))
                }
              />
            </label>

            <label className="block">
              <span className="mb-1 block text-sm font-medium text-slate-700">
                Latitud
              </span>
              <input
                type="number"
                step="any"
                className="w-full rounded-lg border border-slate-300 px-3 py-2 text-slate-900 outline-none focus:border-indigo-500"
                value={formData.latitude}
                onChange={(event) =>
                  handleInputChange("latitude", Number(event.target.value))
                }
              />
            </label>

            <label className="flex items-center gap-2 text-sm text-slate-700">
              <input
                type="checkbox"
                checked={formData.canJoin}
                onChange={(event) =>
                  handleInputChange("canJoin", event.target.checked)
                }
              />
              Permite inscripcion
            </label>
          </div>

          {error && (
            <p className="mt-4 rounded-lg bg-red-50 px-3 py-2 text-sm text-red-700">
              {error}
            </p>
          )}

          {successMessage && (
            <p className="mt-4 rounded-lg bg-emerald-50 px-3 py-2 text-sm text-emerald-700">
              {successMessage}
            </p>
          )}

          <div className="mt-6 flex gap-3">
            <button
              type="submit"
              disabled={saving}
              className="rounded-lg bg-indigo-600 px-4 py-2 text-sm font-medium text-white hover:bg-indigo-700 disabled:cursor-not-allowed disabled:bg-indigo-300"
            >
              {saving
                ? "Guardando..."
                : editingActividadId
                  ? "Guardar cambios"
                  : "Crear actividad"}
            </button>

            {editingActividadId && (
              <button
                type="button"
                onClick={handleCancelEdit}
                className="rounded-lg border border-slate-300 px-4 py-2 text-sm font-medium text-slate-700 hover:bg-slate-50"
              >
                Cancelar
              </button>
            )}
          </div>
        </form>

        <div className="overflow-hidden rounded-2xl bg-white shadow">
          {loading && (
            <p className="p-6 text-sm text-slate-600">Cargando actividades...</p>
          )}

          {!loading && !error && actividades.length === 0 && (
            <p className="p-6 text-sm text-slate-600">
              No hay actividades registradas todavia.
            </p>
          )}

          {!loading && !error && actividades.length > 0 && (
            <table className="w-full border-collapse text-left text-sm">
              <thead className="bg-slate-50 text-slate-700">
                <tr>
                  <th className="px-4 py-3 font-semibold">ID</th>
                  <th className="px-4 py-3 font-semibold">Descripcion</th>
                  <th className="px-4 py-3 font-semibold">Tipo</th>
                  <th className="px-4 py-3 font-semibold">Fecha</th>
                  <th className="px-4 py-3 font-semibold">Capacidad</th>
                  <th className="px-4 py-3 font-semibold">Acciones</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {actividades.map((actividad) => (
                  <tr key={actividad.id}>
                    <td className="px-4 py-3 text-slate-600">{actividad.id}</td>
                    <td className="px-4 py-3 font-medium text-slate-900">
                      {actividad.description ?? "Sin descripcion"}
                    </td>
                    <td className="px-4 py-3 text-slate-600">
                      {actividad.typeActivity ?? "-"}
                    </td>
                    <td className="px-4 py-3 text-slate-600">
                      {actividad.dayActivity ?? "-"}
                    </td>
                    <td className="px-4 py-3 text-slate-600">
                      {actividad.capacity ?? "-"}
                    </td>
                    <td className="px-4 py-3">
                      <div className="flex gap-2">
                        <button
                          type="button"
                          onClick={() => handleEdit(actividad)}
                          className="rounded-lg border border-slate-300 px-3 py-1 text-xs font-medium text-slate-700 hover:bg-slate-50"
                        >
                          Editar
                        </button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </div>
      </section>
    </main>
  );
}
