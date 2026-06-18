"use client";

import { FormEvent, useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import AppNav from "@/components/AppNav";
import BajaDialog from "@/components/BajaDialog";
import CredentialsNotice from "@/components/CredentialsNotice";
import { canAccessAdminPanel, getUser } from "@/services/authService";
import {
  createSocio,
  darDeBajaSocio,
  getSocios,
  regenerateSocioAccessCode,
  reactivarSocio,
  updateSocio,
  type SocioFormData,
} from "@/services/socioService";
import type { IssuedAccessCredentials } from "@/types/access";
import type { Socio } from "@/types/socio";

function createEmptyForm(): SocioFormData {
  return {
    name: "",
    surname: "",
    email: "",
    phoneNumber: "",
    dni: "",
    active: true,
    familyModel: "",
    entryDate: new Date().toISOString().slice(0, 10),
  };
}

export default function SociosPage() {
  const router = useRouter();

  const [socios, setSocios] = useState<Socio[]>([]);
  const [statusFilter, setStatusFilter] = useState<"active" | "inactive">("active");
  const [formData, setFormData] = useState<SocioFormData>(() => createEmptyForm());
  const [editingSocioId, setEditingSocioId] = useState<number | null>(null);

  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [deletingId, setDeletingId] = useState<number | null>(null);
  const [reactivatingId, setReactivatingId] = useState<number | null>(null);
  const [regeneratingId, setRegeneratingId] = useState<number | null>(null);
  const [error, setError] = useState("");
  const [successMessage, setSuccessMessage] = useState("");
  const [issuedCredentials, setIssuedCredentials] =
    useState<IssuedAccessCredentials | null>(null);
  const [bajaTarget, setBajaTarget] = useState<Socio | null>(null);
  const [bajaReason, setBajaReason] = useState("");
  const [bajaError, setBajaError] = useState("");

  async function loadSocios(nextFilter: "active" | "inactive" = statusFilter) {
    setLoading(true);
    setError("");

    try {
      const data = await getSocios(nextFilter === "active");
      setSocios(data);
    } catch (error) {
      console.error(error);
      setError("No se han podido cargar los socios.");
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
      loadSocios();
    }, 0);

    return () => window.clearTimeout(timeoutId);
  }, [router, statusFilter]);

  function handleInputChange(
    field: keyof SocioFormData,
    value: string | boolean
  ) {
    setFormData((current) => ({
      ...current,
      [field]: value,
    }));
  }

  function handleEdit(socio: Socio) {
    setEditingSocioId(socio.id);
    setSuccessMessage("");
    setError("");
    setIssuedCredentials(null);

    setFormData({
      name: socio.name ?? "",
      surname: socio.surname ?? "",
      email: socio.email ?? "",
      phoneNumber: socio.phoneNumber ?? "",
      dni: socio.dni ?? "",
      active: socio.active ?? true,
      familyModel: socio.familyModel ?? "",
      entryDate: socio.entryDate ?? new Date().toISOString().slice(0, 10),
    });

    window.scrollTo({ top: 0, behavior: "smooth" });
  }

  function handleCancelEdit() {
    setEditingSocioId(null);
    setFormData(createEmptyForm());
    setError("");
    setSuccessMessage("");
    setIssuedCredentials(null);
  }

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setSaving(true);
    setError("");
    setSuccessMessage("");
    setIssuedCredentials(null);

    try {
      if (editingSocioId) {
        await updateSocio(editingSocioId, formData);
        setSuccessMessage("Socio actualizado correctamente.");
      } else {
        const createdSocio = await createSocio(formData);
        setIssuedCredentials(createdSocio);
        setSuccessMessage("Socio creado correctamente.");
      }

      setFormData(createEmptyForm());
      setEditingSocioId(null);
      await loadSocios();
    } catch (error) {
      console.error(error);
      setError(
        error instanceof Error
          ? error.message
          : "No se ha podido guardar el socio. Revisa los campos."
      );
    } finally {
      setSaving(false);
    }
  }

  async function handleRegenerateAccessCode(socio: Socio) {
    if (!window.confirm("Se generara un nuevo codigo de acceso para este socio. El anterior dejara de ser valido.")) {
      return;
    }

    setRegeneratingId(socio.id);
    setError("");
    setSuccessMessage("");

    try {
      const credentials = await regenerateSocioAccessCode(socio.id);
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

  async function handleDeleteSocio(socio: Socio) {
    setBajaTarget(socio);
    setBajaReason("");
    setBajaError("");
    setError("");
    setSuccessMessage("");
    setIssuedCredentials(null);
  }

  function closeBajaDialog() {
    setBajaTarget(null);
    setBajaReason("");
    setBajaError("");
  }

  async function confirmBajaSocio() {
    if (!bajaTarget) {
      return;
    }

    const trimmedReason = bajaReason.trim();

    if (!trimmedReason) {
      setBajaError("Debes indicar el motivo de la baja.");
      return;
    }

    setDeletingId(bajaTarget.id);
    setError("");
    setBajaError("");
    setSuccessMessage("");
    setIssuedCredentials(null);

    try {
      await darDeBajaSocio(bajaTarget.id, { reason: trimmedReason });
      if (editingSocioId === bajaTarget.id) {
        handleCancelEdit();
      }
      setSuccessMessage("Socio dado de baja correctamente.");
      closeBajaDialog();
      await loadSocios();
    } catch (error) {
      console.error(error);
      const message =
        error instanceof Error
          ? error.message
          : "No se ha podido dar de baja al socio.";
      setError(message);
      setBajaError(message);
    } finally {
      setDeletingId(null);
    }
  }

  async function handleReactivateSocio(socio: Socio) {
    if (!window.confirm(`Se reactivara al socio ${socio.name ?? socio.id}.`)) {
      return;
    }

    setReactivatingId(socio.id);
    setError("");
    setSuccessMessage("");
    setIssuedCredentials(null);

    try {
      await reactivarSocio(socio.id);
      setSuccessMessage("Socio reactivado correctamente.");
      await loadSocios();
    } catch (error) {
      console.error(error);
      setError(
        error instanceof Error
          ? error.message
          : "No se ha podido reactivar al socio."
      );
    } finally {
      setReactivatingId(null);
    }
  }

  return (
    <main className="min-h-screen bg-slate-100">
      <AppNav />

      <section className="mx-auto max-w-6xl px-6 py-8">
        <div className="mb-6">
          <h2 className="text-2xl font-bold text-slate-900">Socios</h2>
          <p className="mt-2 text-slate-600">
            Listado y gestión básica de socios registrados en la plataforma.
          </p>
          <div className="mt-4 flex items-center gap-3">
            <label className="text-sm font-medium text-slate-700" htmlFor="socios-status-filter">
              Vista
            </label>
            <select
              id="socios-status-filter"
              value={statusFilter}
              onChange={(event) =>
                setStatusFilter(event.target.value as "active" | "inactive")
              }
              className="rounded-lg border border-slate-300 bg-white px-3 py-2 text-sm text-slate-900 outline-none focus:border-indigo-500"
            >
              <option value="active">Activos</option>
              <option value="inactive">Inactivos</option>
            </select>
          </div>
        </div>

        <form
          onSubmit={handleSubmit}
          className="mb-8 rounded-2xl bg-white p-6 shadow"
        >
          <div className="mb-5">
            <h3 className="text-lg font-semibold text-slate-900">
              {editingSocioId ? "Editar socio" : "Nuevo socio"}
            </h3>
            <p className="mt-1 text-sm text-slate-600">
              Completa los datos principales del socio.
            </p>
          </div>

          <div className="grid gap-4 md:grid-cols-2">
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
              />
            </label>

           <label className="block">
             <span className="mb-1 block text-sm font-medium text-slate-700">
               Teléfono
             </span>
             <input
               className="w-full rounded-lg border border-slate-300 px-3 py-2 text-slate-900 outline-none focus:border-indigo-500"
               value={formData.phoneNumber}
               onChange={(event) =>
                 handleInputChange("phoneNumber", event.target.value)
               }
               placeholder="600-123-456"
               pattern="\d{3}-\d{3}-\d{3}"
               title="El teléfono debe tener el formato 600-123-456"
             />
               <p className="mt-1 text-xs text-slate-500">
                 Formato requerido: 600-123-456
               </p>
           </label>

            <label className="block">
              <span className="mb-1 block text-sm font-medium text-slate-700">
                DNI
              </span>
              <input
                className="w-full rounded-lg border border-slate-300 px-3 py-2 text-slate-900 outline-none focus:border-indigo-500"
                value={formData.dni}
                onChange={(event) =>
                  handleInputChange("dni", event.target.value)
                }
              />
            </label>

            <label className="block">
              <span className="mb-1 block text-sm font-medium text-slate-700">
                Modelo familiar
              </span>
              <input
                className="w-full rounded-lg border border-slate-300 px-3 py-2 text-slate-900 outline-none focus:border-indigo-500"
                value={formData.familyModel}
                onChange={(event) =>
                  handleInputChange("familyModel", event.target.value)
                }
              />
            </label>

            <label className="block">
              <span className="mb-1 block text-sm font-medium text-slate-700">
                Fecha de alta
              </span>
              <input
                type="date"
                className="w-full rounded-lg border border-slate-300 px-3 py-2 text-slate-900 outline-none focus:border-indigo-500"
                value={formData.entryDate}
                onChange={(event) =>
                  handleInputChange("entryDate", event.target.value)
                }
                required
              />
            </label>

            <label className="flex items-center gap-2 text-sm text-slate-700">
              <input
                type="checkbox"
                checked={formData.active}
                onChange={(event) =>
                  handleInputChange("active", event.target.checked)
                }
              />
              Socio activo
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
              entityLabel="socio"
              onDismiss={() => setIssuedCredentials(null)}
            />
          )}

          <div className="mt-6 flex gap-3">
            <button
              type="submit"
              disabled={saving}
              className="rounded-lg bg-indigo-600 px-4 py-2 text-sm font-medium text-white hover:bg-indigo-700 disabled:cursor-not-allowed disabled:bg-indigo-300"
            >
              {saving
                ? "Guardando..."
                : editingSocioId
                  ? "Guardar cambios"
                  : "Crear socio"}
            </button>

            {editingSocioId && (
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

        <div className="overflow-x-auto rounded-2xl bg-white shadow">
          {loading && (
            <p className="p-6 text-sm text-slate-600">Cargando socios...</p>
          )}

          {!loading && !error && socios.length === 0 && (
            <p className="p-6 text-sm text-slate-600">
              No hay socios registrados todavía.
            </p>
          )}

          {!loading && socios.length > 0 && (
            <table className="min-w-[1060px] w-full border-collapse text-left text-sm">
              <thead className="bg-slate-50 text-slate-700">
                <tr>
                  <th className="px-4 py-3 font-semibold">ID</th>
                  <th className="px-4 py-3 font-semibold">Nombre</th>
                  <th className="px-4 py-3 font-semibold">Email</th>
                  <th className="px-4 py-3 font-semibold">Teléfono</th>
                  <th className="px-4 py-3 font-semibold">Estado</th>
                  <th className="px-4 py-3 font-semibold">Fecha baja</th>
                  <th className="px-4 py-3 font-semibold">Motivo</th>
                  <th className="px-4 py-3 font-semibold">Acciones</th>
                </tr>
              </thead>

              <tbody className="divide-y divide-slate-100">
                {socios.map((socio) => (
                  <tr key={socio.id}>
                    <td className="px-4 py-3 text-slate-600">{socio.id}</td>
                    <td className="px-4 py-3 font-medium text-slate-900">
                      {[socio.name, socio.surname].filter(Boolean).join(" ") ||
                        "Sin nombre"}
                    </td>
                    <td className="px-4 py-3 text-slate-600">
                      {socio.email ?? "—"}
                    </td>
                    <td className="px-4 py-3 text-slate-600">
                      {socio.phoneNumber ?? "—"}
                    </td>
                    <td className="px-4 py-3 text-slate-600">
                      {socio.active === false ? "Inactivo" : "Activo"}
                    </td>
                    <td className="px-4 py-3 text-slate-600">
                      {socio.outDate ?? "â€”"}
                    </td>
                    <td className="px-4 py-3 text-slate-600">
                      {socio.reason ?? "-"}
                    </td>
                    <td className="px-4 py-3">
                      <div className="flex flex-wrap gap-2">
                        <button
                          onClick={() => handleEdit(socio)}
                          className="rounded-lg border border-slate-300 px-3 py-1 text-xs font-medium text-slate-700 hover:bg-slate-50"
                        >
                          Editar
                        </button>
                        <button
                          type="button"
                          onClick={() => handleRegenerateAccessCode(socio)}
                          disabled={regeneratingId === socio.id}
                          className="rounded-lg border border-amber-300 px-3 py-1 text-xs font-medium text-amber-800 hover:bg-amber-50 disabled:cursor-not-allowed disabled:opacity-60"
                        >
                          {regeneratingId === socio.id ? "Regenerando..." : "Regenerar acceso"}
                        </button>
                        {socio.active !== false && (
                          <button
                            type="button"
                            onClick={() => handleDeleteSocio(socio)}
                            disabled={deletingId === socio.id}
                            className="rounded-lg border border-red-300 px-3 py-1 text-xs font-medium text-red-700 hover:bg-red-50 disabled:cursor-not-allowed disabled:opacity-60"
                          >
                            {deletingId === socio.id ? "Dando de baja..." : "Dar de baja"}
                          </button>
                        )}
                        {socio.active === false && (
                          <button
                            type="button"
                            onClick={() => handleReactivateSocio(socio)}
                            disabled={reactivatingId === socio.id}
                            className="rounded-lg border border-emerald-300 px-3 py-1 text-xs font-medium text-emerald-700 hover:bg-emerald-50 disabled:cursor-not-allowed disabled:opacity-60"
                          >
                            {reactivatingId === socio.id ? "Reactivando..." : "Reactivar"}
                          </button>
                        )}
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </div>
      </section>

      <BajaDialog
        open={bajaTarget !== null}
        title="Registrar baja de socio"
        description="La baja deja inactivo al socio y desactiva a sus participantes asociados. El motivo quedara registrado."
        reason={bajaReason}
        error={bajaError}
        busy={deletingId !== null}
        onReasonChange={setBajaReason}
        onClose={closeBajaDialog}
        onConfirm={confirmBajaSocio}
      />
    </main>
  );
}
