"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import AppNav from "@/components/AppNav";
import { getUser } from "@/services/authService";
import { getActividades } from "@/services/actividadService";
import type { Actividad } from "@/types/actividad";

export default function ActividadesPage() {
  const router = useRouter();

  const [actividades, setActividades] = useState<Actividad[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    const user = getUser();

    if (!user) {
      router.push("/login");
      return;
    }

    async function loadActividades() {
      try {
        const data = await getActividades();
        setActividades(data);
      } catch {
        setError("No se han podido cargar las actividades.");
      } finally {
        setLoading(false);
      }
    }

    loadActividades();
  }, [router]);

  return (
    <main className="min-h-screen bg-slate-100">
      <AppNav />

      <section className="mx-auto max-w-6xl px-6 py-8">
        <div className="mb-6">
          <h2 className="text-2xl font-bold text-slate-900">Actividades</h2>
          <p className="mt-2 text-slate-600">
            Gestión de talleres, eventos, reuniones y actividades ofertadas por la asociación.
          </p>
        </div>

        <div className="overflow-hidden rounded-2xl bg-white shadow">
          {loading && (
            <p className="p-6 text-sm text-slate-600">Cargando actividades...</p>
          )}

          {error && <p className="p-6 text-sm text-red-700">{error}</p>}

          {!loading && !error && actividades.length === 0 && (
            <p className="p-6 text-sm text-slate-600">
              No hay actividades registradas todavía.
            </p>
          )}

          {!loading && !error && actividades.length > 0 && (
            <table className="w-full border-collapse text-left text-sm">
              <thead className="bg-slate-50 text-slate-700">
                <tr>
                  <th className="px-4 py-3 font-semibold">ID</th>
                  <th className="px-4 py-3 font-semibold">Descripción</th>
                  <th className="px-4 py-3 font-semibold">Tipo</th>
                  <th className="px-4 py-3 font-semibold">Fecha</th>
                  <th className="px-4 py-3 font-semibold">Duración</th>
                  <th className="px-4 py-3 font-semibold">Capacidad</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {actividades.map((actividad) => (
                  <tr key={actividad.id}>
                    <td className="px-4 py-3 text-slate-600">{actividad.id}</td>
                    <td className="px-4 py-3 font-medium text-slate-900">
                      {actividad.description ?? "Sin descripción"}
                    </td>
                    <td className="px-4 py-3 text-slate-600">
                      {actividad.typeActivity ?? "—"}
                    </td>
                    <td className="px-4 py-3 text-slate-600">
                      {actividad.dayActivity ?? "—"}
                    </td>
                    <td className="px-4 py-3 text-slate-600">
                      {actividad.duration ? `${actividad.duration} h` : "—"}
                    </td>
                    <td className="px-4 py-3 text-slate-600">
                      {actividad.capacity ?? "—"}
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