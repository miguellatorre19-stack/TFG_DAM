"use client";

import { FormEvent, useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import AppNav from "@/components/AppNav";
import { canAccessAdminPanel, getUser } from "@/services/authService";
import {
  createServicio,
  getServicios,
  updateServicio,
  type ServicioFormData,
} from "@/services/servicioService";
import type { Servicio } from "@/types/servicio";

function createEmptyForm(): ServicioFormData {
  return {
    description: "",
    periodicity: "",
    requisites: "",
    duration: 1,
    capacity: 1,
  };
}

export default function ServiciosPage() {
  const router = useRouter();

  const [servicios, setServicios] = useState<Servicio[]>([]);
  const [formData, setFormData] = useState<ServicioFormData>(() => createEmptyForm());
  const [editingServicioId, setEditingServicioId] = useState<number | null>(null);

  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");
  const [successMessage, setSuccessMessage] = useState("");

  async function loadServicios() {
    setLoading(true);
    setError("");

    try {
      const data = await getServicios();
      setServicios(data);
    } catch (error) {
      console.error(error);
      setError("No se han podido cargar los servicios.");
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
      loadServicios();
    }, 0);

    return () => window.clearTimeout(timeoutId);
  }, [router]);

  function handleInputChange(
    field: keyof ServicioFormData,
    value: string | number
  ) {
    setFormData((current) => ({
      ...current,
      [field]: value,
    }));
  }

  function handleEdit(servicio: Servicio) {
    setEditingServicioId(servicio.id);
    setSuccessMessage("");
    setError("");
    setFormData({
      description: servicio.description ?? "",
      periodicity: servicio.periodicity ?? "",
      requisites: servicio.requisites ?? "",
      duration: servicio.duration ?? 1,
      capacity: servicio.capacity ?? 1,
    });

    window.scrollTo({ top: 0, behavior: "smooth" });
  }

  function handleCancelEdit() {
    setEditingServicioId(null);
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
      if (editingServicioId) {
        await updateServicio(editingServicioId, formData);
        setSuccessMessage("Servicio actualizado correctamente.");
      } else {
        await createServicio(formData);
        setSuccessMessage("Servicio creado correctamente.");
      }

      setFormData(createEmptyForm());
      setEditingServicioId(null);
      await loadServicios();
    } catch (error) {
      console.error(error);
      setError(
        error instanceof Error
          ? error.message
          : "No se ha podido guardar el servicio. Revisa los campos."
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
          <h2 className="text-2xl font-bold text-slate-900">Servicios</h2>
          <p className="mt-2 text-slate-600">
            Gestion completa de servicios especializados ofrecidos por trabajadores cualificados.
          </p>
        </div>

        <form
          onSubmit={handleSubmit}
          className="mb-8 rounded-2xl bg-white p-6 shadow"
        >
          <div className="mb-5">
            <h3 className="text-lg font-semibold text-slate-900">
              {editingServicioId ? "Editar servicio" : "Nuevo servicio"}
            </h3>
            <p className="mt-1 text-sm text-slate-600">
              {editingServicioId
                ? "Actualiza la configuracion del servicio."
                : "Crea un nuevo servicio desde el panel de administracion."}
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
                Periodicidad
              </span>
              <input
                className="w-full rounded-lg border border-slate-300 px-3 py-2 text-slate-900 outline-none focus:border-indigo-500"
                value={formData.periodicity}
                onChange={(event) =>
                  handleInputChange("periodicity", event.target.value)
                }
              />
            </label>

            <label className="block">
              <span className="mb-1 block text-sm font-medium text-slate-700">
                Requisitos
              </span>
              <input
                className="w-full rounded-lg border border-slate-300 px-3 py-2 text-slate-900 outline-none focus:border-indigo-500"
                value={formData.requisites}
                onChange={(event) =>
                  handleInputChange("requisites", event.target.value)
                }
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
                : editingServicioId
                  ? "Guardar cambios"
                  : "Crear servicio"}
            </button>

            {editingServicioId && (
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
            <p className="p-6 text-sm text-slate-600">Cargando servicios...</p>
          )}

          {!loading && !error && servicios.length === 0 && (
            <p className="p-6 text-sm text-slate-600">
              No hay servicios registrados todavia.
            </p>
          )}

          {!loading && !error && servicios.length > 0 && (
            <table className="w-full border-collapse text-left text-sm">
              <thead className="bg-slate-50 text-slate-700">
                <tr>
                  <th className="px-4 py-3 font-semibold">ID</th>
                  <th className="px-4 py-3 font-semibold">Descripcion</th>
                  <th className="px-4 py-3 font-semibold">Periodicidad</th>
                  <th className="px-4 py-3 font-semibold">Capacidad</th>
                  <th className="px-4 py-3 font-semibold">Acciones</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {servicios.map((servicio) => (
                  <tr key={servicio.id}>
                    <td className="px-4 py-3 text-slate-600">{servicio.id}</td>
                    <td className="px-4 py-3 font-medium text-slate-900">
                      {servicio.description ?? "Sin descripcion"}
                    </td>
                    <td className="px-4 py-3 text-slate-600">
                      {servicio.periodicity ?? "-"}
                    </td>
                    <td className="px-4 py-3 text-slate-600">
                      {servicio.capacity ?? "-"}
                    </td>
                    <td className="px-4 py-3">
                      <div className="flex gap-2">
                        <button
                          type="button"
                          onClick={() => handleEdit(servicio)}
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
