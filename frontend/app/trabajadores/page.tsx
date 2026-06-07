"use client";

import { FormEvent, useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import AppNav from "@/components/AppNav";
import CredentialsNotice from "@/components/CredentialsNotice";
import { canAccessAdminPanel, getUser } from "@/services/authService";
import { getServicios } from "@/services/servicioService";
import {
  createTrabajador,
  getTrabajadores,
  regenerateTrabajadorAccessCode,
  updateTrabajador,
  type TrabajadorFormData,
} from "@/services/trabajadorService";
import type { IssuedAccessCredentials } from "@/types/access";
import type { Servicio } from "@/types/servicio";
import type { Trabajador } from "@/types/trabajador";

function createEmptyForm(): TrabajadorFormData {
  return {
    dni: "",
    name: "",
    surname: "",
    email: "",
    phoneNumber: "",
    birthDate: "",
    contractType: "",
    servicioId: 0,
  };
}

export default function TrabajadoresPage() {
  const router = useRouter();

  const [trabajadores, setTrabajadores] = useState<Trabajador[]>([]);
  const [servicios, setServicios] = useState<Servicio[]>([]);
  const [formData, setFormData] = useState<TrabajadorFormData>(() =>
    createEmptyForm()
  );
  const [editingTrabajadorId, setEditingTrabajadorId] = useState<number | null>(null);

  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [regeneratingId, setRegeneratingId] = useState<number | null>(null);
  const [error, setError] = useState("");
  const [successMessage, setSuccessMessage] = useState("");
  const [issuedCredentials, setIssuedCredentials] =
    useState<IssuedAccessCredentials | null>(null);

  async function loadData() {
    setLoading(true);
    setError("");

    try {
      const [trabajadoresData, serviciosData] = await Promise.all([
        getTrabajadores(),
        getServicios(),
      ]);
      setTrabajadores(trabajadoresData);
      setServicios(serviciosData);
    } catch (error) {
      console.error(error);
      setError("No se han podido cargar los trabajadores.");
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
      loadData();
    }, 0);

    return () => window.clearTimeout(timeoutId);
  }, [router]);

  function handleInputChange(
    field: keyof TrabajadorFormData,
    value: string | number
  ) {
    setFormData((current) => ({
      ...current,
      [field]: value,
    }));
  }

  function handleEdit(trabajador: Trabajador) {
    setEditingTrabajadorId(trabajador.id);
    setSuccessMessage("");
    setError("");
    setIssuedCredentials(null);
    setFormData({
      dni: trabajador.dni ?? "",
      name: trabajador.name ?? "",
      surname: trabajador.surname ?? "",
      email: trabajador.email ?? "",
      phoneNumber: trabajador.phoneNumber ?? "",
      birthDate: trabajador.birthDate ?? "",
      contractType: trabajador.contractType ?? "",
      servicioId: trabajador.servicioOutDto?.id ?? trabajador.servicioId ?? 0,
    });

    window.scrollTo({ top: 0, behavior: "smooth" });
  }

  function handleCancelEdit() {
    setEditingTrabajadorId(null);
    setFormData(createEmptyForm());
    setError("");
    setSuccessMessage("");
    setIssuedCredentials(null);
  }

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();

    if (!formData.servicioId) {
      setError("Debes seleccionar un servicio para el trabajador.");
      return;
    }

    setSaving(true);
    setError("");
    setSuccessMessage("");
    setIssuedCredentials(null);

    try {
      if (editingTrabajadorId) {
        await updateTrabajador(editingTrabajadorId, formData);
        setSuccessMessage("Trabajador actualizado correctamente.");
      } else {
        const createdTrabajador = await createTrabajador(formData.servicioId, formData);
        setIssuedCredentials(createdTrabajador);
        setSuccessMessage("Trabajador creado correctamente.");
      }

      setFormData(createEmptyForm());
      setEditingTrabajadorId(null);
      await loadData();
    } catch (error) {
      console.error(error);
      setError(
        error instanceof Error
          ? error.message
          : "No se ha podido guardar el trabajador. Revisa los campos."
      );
    } finally {
      setSaving(false);
    }
  }

  async function handleRegenerateAccessCode(trabajador: Trabajador) {
    if (
      !window.confirm(
        "Se generara un nuevo codigo de acceso para este trabajador. El anterior dejara de ser valido."
      )
    ) {
      return;
    }

    setRegeneratingId(trabajador.id);
    setError("");
    setSuccessMessage("");

    try {
      const credentials = await regenerateTrabajadorAccessCode(trabajador.id);
      setIssuedCredentials(credentials);
      setSuccessMessage("Codigo de acceso regenerado correctamente.");
    } catch (error) {
      console.error(error);
      setError(
        error instanceof Error
          ? error.message
          : "No se ha podido regenerar el codigo de acceso."
      );
    } finally {
      setRegeneratingId(null);
    }
  }

  return (
    <main className="min-h-screen bg-slate-100">
      <AppNav />

      <section className="mx-auto max-w-6xl px-6 py-8">
        <div className="mb-6">
          <h2 className="text-2xl font-bold text-slate-900">Trabajadores</h2>
          <p className="mt-2 text-slate-600">
            Alta, edicion y baja del personal profesional, junto con sus credenciales de acceso.
          </p>
        </div>

        <form
          onSubmit={handleSubmit}
          className="mb-8 rounded-2xl bg-white p-6 shadow"
        >
          <div className="mb-5">
            <h3 className="text-lg font-semibold text-slate-900">
              {editingTrabajadorId ? "Editar trabajador" : "Nuevo trabajador"}
            </h3>
            <p className="mt-1 text-sm text-slate-600">
              {editingTrabajadorId
                ? "Actualiza el perfil profesional y el servicio asignado."
                : "Cada alta genera un usuario con email y codigo inicial."}
            </p>
          </div>

          <div className="grid gap-4 md:grid-cols-2">
            <label className="block">
              <span className="mb-1 block text-sm font-medium text-slate-700">
                Servicio asignado
              </span>
              <select
                className="w-full rounded-lg border border-slate-300 px-3 py-2 text-slate-900 outline-none focus:border-indigo-500"
                value={formData.servicioId}
                onChange={(event) =>
                  handleInputChange("servicioId", Number(event.target.value))
                }
                required
              >
                <option value={0}>Selecciona un servicio</option>
                {servicios.map((servicio) => (
                  <option key={servicio.id} value={servicio.id}>
                    {servicio.description || `Servicio ${servicio.id}`}
                  </option>
                ))}
              </select>
            </label>

            <label className="block">
              <span className="mb-1 block text-sm font-medium text-slate-700">
                Tipo de contrato
              </span>
              <input
                className="w-full rounded-lg border border-slate-300 px-3 py-2 text-slate-900 outline-none focus:border-indigo-500"
                value={formData.contractType}
                onChange={(event) =>
                  handleInputChange("contractType", event.target.value)
                }
                placeholder="Tiempo completo, parcial..."
                required
              />
            </label>

            <label className="block">
              <span className="mb-1 block text-sm font-medium text-slate-700">
                Nombre
              </span>
              <input
                className="w-full rounded-lg border border-slate-300 px-3 py-2 text-slate-900 outline-none focus:border-indigo-500"
                value={formData.name}
                onChange={(event) =>
                  handleInputChange("name", event.target.value)
                }
                required
              />
            </label>

            <label className="block">
              <span className="mb-1 block text-sm font-medium text-slate-700">
                Apellidos
              </span>
              <input
                className="w-full rounded-lg border border-slate-300 px-3 py-2 text-slate-900 outline-none focus:border-indigo-500"
                value={formData.surname}
                onChange={(event) =>
                  handleInputChange("surname", event.target.value)
                }
                required
              />
            </label>

            <label className="block">
              <span className="mb-1 block text-sm font-medium text-slate-700">
                Email
              </span>
              <input
                type="email"
                className="w-full rounded-lg border border-slate-300 px-3 py-2 text-slate-900 outline-none focus:border-indigo-500"
                value={formData.email}
                onChange={(event) =>
                  handleInputChange("email", event.target.value)
                }
                required
              />
            </label>

            <label className="block">
              <span className="mb-1 block text-sm font-medium text-slate-700">
                Telefono
              </span>
              <input
                className="w-full rounded-lg border border-slate-300 px-3 py-2 text-slate-900 outline-none focus:border-indigo-500"
                value={formData.phoneNumber}
                onChange={(event) =>
                  handleInputChange("phoneNumber", event.target.value)
                }
                placeholder="600-123-456"
                pattern="\d{3}-\d{3}-\d{3}"
                required
              />
            </label>

            <label className="block">
              <span className="mb-1 block text-sm font-medium text-slate-700">
                DNI
              </span>
              <input
                className="w-full rounded-lg border border-slate-300 px-3 py-2 uppercase text-slate-900 outline-none focus:border-indigo-500"
                value={formData.dni}
                onChange={(event) =>
                  handleInputChange("dni", event.target.value.toUpperCase())
                }
                placeholder="12345678A"
                pattern="\d{8}[A-Z]"
                required
              />
            </label>

            <label className="block">
              <span className="mb-1 block text-sm font-medium text-slate-700">
                Fecha de nacimiento
              </span>
              <input
                type="date"
                className="w-full rounded-lg border border-slate-300 px-3 py-2 text-slate-900 outline-none focus:border-indigo-500"
                value={formData.birthDate}
                onChange={(event) =>
                  handleInputChange("birthDate", event.target.value)
                }
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

          {issuedCredentials && (
            <CredentialsNotice
              credentials={issuedCredentials}
              entityLabel="trabajador"
              onDismiss={() => setIssuedCredentials(null)}
            />
          )}

          <div className="mt-6 flex gap-3">
            <button
              type="submit"
              disabled={saving || servicios.length === 0}
              className="rounded-lg bg-indigo-600 px-4 py-2 text-sm font-medium text-white hover:bg-indigo-700 disabled:cursor-not-allowed disabled:bg-indigo-300"
            >
              {saving
                ? "Guardando..."
                : editingTrabajadorId
                  ? "Guardar cambios"
                  : "Crear trabajador"}
            </button>

            {editingTrabajadorId && (
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
            <p className="p-6 text-sm text-slate-600">Cargando trabajadores...</p>
          )}

          {!loading && !error && trabajadores.length === 0 && (
            <p className="p-6 text-sm text-slate-600">
              No hay trabajadores registrados todavia.
            </p>
          )}

          {!loading && !error && trabajadores.length > 0 && (
            <table className="w-full border-collapse text-left text-sm">
              <thead className="bg-slate-50 text-slate-700">
                <tr>
                  <th className="px-4 py-3 font-semibold">ID</th>
                  <th className="px-4 py-3 font-semibold">Nombre</th>
                  <th className="px-4 py-3 font-semibold">Email</th>
                  <th className="px-4 py-3 font-semibold">Telefono</th>
                  <th className="px-4 py-3 font-semibold">Servicio</th>
                  <th className="px-4 py-3 font-semibold">Acciones</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {trabajadores.map((trabajador) => (
                  <tr key={trabajador.id}>
                    <td className="px-4 py-3 text-slate-600">{trabajador.id}</td>
                    <td className="px-4 py-3 font-medium text-slate-900">
                      {[trabajador.name, trabajador.surname]
                        .filter(Boolean)
                        .join(" ") || "Sin nombre"}
                    </td>
                    <td className="px-4 py-3 text-slate-600">
                      {trabajador.email ?? "-"}
                    </td>
                    <td className="px-4 py-3 text-slate-600">
                      {trabajador.phoneNumber ?? "-"}
                    </td>
                    <td className="px-4 py-3 text-slate-600">
                      {trabajador.servicioOutDto?.description ?? "-"}
                    </td>
                    <td className="px-4 py-3">
                      <div className="flex gap-2">
                        <button
                          type="button"
                          onClick={() => handleEdit(trabajador)}
                          className="rounded-lg border border-slate-300 px-3 py-1 text-xs font-medium text-slate-700 hover:bg-slate-50"
                        >
                          Editar
                        </button>
                        <button
                          type="button"
                          onClick={() => handleRegenerateAccessCode(trabajador)}
                          disabled={regeneratingId === trabajador.id}
                          className="rounded-lg border border-amber-300 px-3 py-1 text-xs font-medium text-amber-800 hover:bg-amber-50 disabled:cursor-not-allowed disabled:opacity-60"
                        >
                          {regeneratingId === trabajador.id
                            ? "Regenerando..."
                            : "Regenerar acceso"}
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
