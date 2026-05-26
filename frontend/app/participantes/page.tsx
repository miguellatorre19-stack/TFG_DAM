"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import AppNav from "@/components/AppNav";
import { getUser } from "@/services/authService";
import { getParticipantes } from "@/services/participanteService";
import type { Participante } from "@/types/participante";

export default function ParticipantesPage() {
  const router = useRouter();

  const [participantes, setParticipantes] = useState<Participante[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    const user = getUser();

    if (!user) {
      router.push("/login");
      return;
    }

    async function loadParticipantes() {
      try {
        const data = await getParticipantes();
        setParticipantes(data);
      } catch {
        setError("No se han podido cargar los participantes.");
      } finally {
        setLoading(false);
      }
    }

    loadParticipantes();
  }, [router]);

  return (
    <main className="min-h-screen bg-slate-100">
      <AppNav />

      <section className="mx-auto max-w-6xl px-6 py-8">
        <div className="mb-6">
          <h2 className="text-2xl font-bold text-slate-900">Participantes</h2>
          <p className="mt-2 text-slate-600">
            Gestión de personas participantes vinculadas a socios, actividades o servicios.
          </p>
        </div>

        <div className="overflow-hidden rounded-2xl bg-white shadow">
          {loading && (
            <p className="p-6 text-sm text-slate-600">Cargando participantes...</p>
          )}

          {error && <p className="p-6 text-sm text-red-700">{error}</p>}

          {!loading && !error && participantes.length === 0 && (
            <p className="p-6 text-sm text-slate-600">
              No hay participantes registrados todavía.
            </p>
          )}

          {!loading && !error && participantes.length > 0 && (
            <table className="w-full border-collapse text-left text-sm">
              <thead className="bg-slate-50 text-slate-700">
                <tr>
                  <th className="px-4 py-3 font-semibold">ID</th>
                  <th className="px-4 py-3 font-semibold">Nombre</th>
                  <th className="px-4 py-3 font-semibold">Email</th>
                  <th className="px-4 py-3 font-semibold">Teléfono</th>
                  <th className="px-4 py-3 font-semibold">Relación</th>
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
                      {participante.email ?? "—"}
                    </td>
                    <td className="px-4 py-3 text-slate-600">
                      {participante.phoneNumber ?? "—"}
                    </td>
                    <td className="px-4 py-3 text-slate-600">
                      {participante.typeRel ?? "—"}
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