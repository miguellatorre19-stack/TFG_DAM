"use client";

import { FormEvent, useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import AppNav from "@/components/AppNav";
import CredentialsNotice from "@/components/CredentialsNotice";
import { getUser } from "@/services/authService";
import {
  createParticipante,
  getParticipantes,
  regenerateParticipanteAccessCode,
  type ParticipanteFormData,
} from "@/services/participanteService";
import { getSocios } from "@/services/socioService";
import type { IssuedAccessCredentials } from "@/types/access";
import type { Participante } from "@/types/participante";
import type { Socio } from "@/types/socio";

function createEmptyForm(): ParticipanteFormData {
  return {
    dni: "",
    name: "",
    surname: "",
    email: "",
    phoneNumber: "",
    birthDate: "",
    needs: "",
    typeRel: "",
    socioID: 0,
  };
}

export default function ParticipantesPage() {
  const router = useRouter();

  const [participantes, setParticipantes] = useState<Participante[]>([]);
  const [socios, setSocios] = useState<Socio[]>([]);
  const [formData, setFormData] = useState<ParticipanteFormData>(() =>
    createEmptyForm()
  );
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
      const [participantesData, sociosData] = await Promise.all([
        getParticipantes(),
        getSocios(),
      ]);
      setParticipantes(participantesData);
      setSocios(sociosData);
    } catch (error) {
      console.error(error);
      setError("No se han podido cargar los participantes.");
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

    const timeoutId = window.setTimeout(() => {
      loadData();
    }, 0);

    return () => window.clearTimeout(timeoutId);
  }, [router]);

  function handleInputChange(
    field: keyof ParticipanteFormData,
    value: string | number
  ) {
    setFormData((current) => ({
      ...current,
      [field]: value,
    }));
  }

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();

    if (!formData.socioID) {
      setError("Debes seleccionar un socio para vincular el participante.");
      return;
    }

    setSaving(true);
    setError("");
    setSuccessMessage("");
    setIssuedCredentials(null);

    try {
      const createdParticipante = await createParticipante(formData.socioID, formData);
      setIssuedCredentials(createdParticipante);
      setSuccessMessage("Participante creado correctamente.");
      setFormData(createEmptyForm());
      await loadData();
    } catch (error) {
      console.error(error);
      setError("No se ha podido guardar el participante. Revisa los campos.");
    } finally {
      setSaving(false);
    }
  }

  async function handleRegenerateAccessCode(participante: Participante) {
    if (
      !window.confirm(
        "Se generara un nuevo codigo de acceso para este participante. El anterior dejara de ser valido."
      )
    ) {
      return;
    }

    setRegeneratingId(participante.id);
    setError("");
    setSuccessMessage("");

    try {
      const credentials = await regenerateParticipanteAccessCode(participante.id);
      setIssuedCredentials(credentials);
      setSuccessMessage("Codigo de acceso regenerado correctamente.");
    } catch (error) {
      console.error(error);
      setError("No se ha podido regenerar el codigo de acceso.");
    } finally {
      setRegeneratingId(null);
    }
  }

  return (
    <main className="min-h-screen bg-slate-100">
      <AppNav />

      <section className="mx-auto max-w-6xl px-6 py-8">
        <div className="mb-6">
          <h2 className="text-2xl font-bold text-slate-900">Participantes</h2>
          <p className="mt-2 text-slate-600">
            Alta de participantes vinculados a socios y gestion de sus credenciales de acceso.
          </p>
        </div>

        <form
          onSubmit={handleSubmit}
          className="mb-8 rounded-2xl bg-white p-6 shadow"
        >
          <div className="mb-5">
            <h3 className="text-lg font-semibold text-slate-900">
              Nuevo participante
            </h3>
            <p className="mt-1 text-sm text-slate-600">
              Cada alta genera un usuario con email y codigo inicial.
            </p>
          </div>

          <div className="grid gap-4 md:grid-cols-2">
            <label className="block">
              <span className="mb-1 block text-sm font-medium text-slate-700">
                Socio tutor
              </span>
              <select
                className="w-full rounded-lg border border-slate-300 px-3 py-2 text-slate-900 outline-none focus:border-indigo-500"
                value={formData.socioID}
                onChange={(event) =>
                  handleInputChange("socioID", Number(event.target.value))
                }
                required
              >
                <option value={0}>Selecciona un socio</option>
                {socios.map((socio) => (
                  <option key={socio.id} value={socio.id}>
                    {`${socio.name ?? ""} ${socio.surname ?? ""}`.trim() ||
                      `Socio ${socio.id}`}
                  </option>
                ))}
              </select>
            </label>

            <label className="block">
              <span className="mb-1 block text-sm font-medium text-slate-700">
                Relacion
              </span>
              <input
                className="w-full rounded-lg border border-slate-300 px-3 py-2 text-slate-900 outline-none focus:border-indigo-500"
                value={formData.typeRel}
                onChange={(event) =>
                  handleInputChange("typeRel", event.target.value)
                }
                placeholder="Hijo, tutelado, familiar..."
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

            <label className="block md:col-span-2">
              <span className="mb-1 block text-sm font-medium text-slate-700">
                Necesidades
              </span>
              <textarea
                className="min-h-28 w-full rounded-lg border border-slate-300 px-3 py-2 text-slate-900 outline-none focus:border-indigo-500"
                value={formData.needs}
                onChange={(event) =>
                  handleInputChange("needs", event.target.value)
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
              entityLabel="participante"
              onDismiss={() => setIssuedCredentials(null)}
            />
          )}

          <div className="mt-6">
            <button
              type="submit"
              disabled={saving || socios.length === 0}
              className="rounded-lg bg-indigo-600 px-4 py-2 text-sm font-medium text-white hover:bg-indigo-700 disabled:cursor-not-allowed disabled:bg-indigo-300"
            >
              {saving ? "Guardando..." : "Crear participante"}
            </button>
          </div>
        </form>

        <div className="overflow-hidden rounded-2xl bg-white shadow">
          {loading && (
            <p className="p-6 text-sm text-slate-600">Cargando participantes...</p>
          )}

          {!loading && !error && participantes.length === 0 && (
            <p className="p-6 text-sm text-slate-600">
              No hay participantes registrados todavia.
            </p>
          )}

          {!loading && !error && participantes.length > 0 && (
            <table className="w-full border-collapse text-left text-sm">
              <thead className="bg-slate-50 text-slate-700">
                <tr>
                  <th className="px-4 py-3 font-semibold">ID</th>
                  <th className="px-4 py-3 font-semibold">Nombre</th>
                  <th className="px-4 py-3 font-semibold">Email</th>
                  <th className="px-4 py-3 font-semibold">Telefono</th>
                  <th className="px-4 py-3 font-semibold">Relacion</th>
                  <th className="px-4 py-3 font-semibold">Acciones</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {participantes.map((participante) => (
                  <tr key={participante.id}>
                    <td className="px-4 py-3 text-slate-600">{participante.id}</td>
                    <td className="px-4 py-3 font-medium text-slate-900">
                      {[participante.name, participante.surname]
                        .filter(Boolean)
                        .join(" ") || "Sin nombre"}
                    </td>
                    <td className="px-4 py-3 text-slate-600">
                      {participante.email ?? "-"}
                    </td>
                    <td className="px-4 py-3 text-slate-600">
                      {participante.phoneNumber ?? "-"}
                    </td>
                    <td className="px-4 py-3 text-slate-600">
                      {participante.typeRel ?? "-"}
                    </td>
                    <td className="px-4 py-3">
                      <button
                        type="button"
                        onClick={() => handleRegenerateAccessCode(participante)}
                        disabled={regeneratingId === participante.id}
                        className="rounded-lg border border-amber-300 px-3 py-1 text-xs font-medium text-amber-800 hover:bg-amber-50 disabled:cursor-not-allowed disabled:opacity-60"
                      >
                        {regeneratingId === participante.id
                          ? "Regenerando..."
                          : "Regenerar acceso"}
                      </button>
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
