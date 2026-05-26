"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import AppNav from "@/components/AppNav";
import { getUser } from "@/services/authService";
import { getServicios } from "@/services/servicioService";
import type { Servicio } from "@/types/servicio";

export default function ServiciosPage() {
  const router = useRouter();

  const [servicios, setServicios] = useState<Servicio[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    const user = getUser();

    if (!user) {
      router.push("/login");
      return;
    }

    async function loadServicios() {
      try {
        const data = await getServicios();
        setServicios(data);
      } catch {
        setError("No se han podido cargar los servicios.");
      } finally {
        setLoading(false);
      }
    }

    loadServicios();
  }, [router]);

  return (
    <main className="min-h-screen bg-slate-100">
      <AppNav />

      <section className="mx-auto max-w-6xl px-6 py-8">
        <div className="mb-6">
          <h2 className="text-2xl font-bold text-slate-900">Servicios</h2>
          <p className="mt-2 text-slate-600">
            Gestión de servicios especializados ofrecidos por trabajadores cualificados.
          </p>
        </div>

        <div className="overflow-hidden rounded-2xl bg-white shadow">
          {loading && (
            <p className="p-6 text-sm text-slate-600">Cargando servicios...</p>
          )}

          {error && <p className="p-6 text-sm text-red-700">{error}</p>}

          {!loading && !error && servicios.length === 0 && (
            <p className="p-6 text-sm text-slate-600">
              No hay servicios registrados todavía.
            </p>
          )}

          {!loading && !error && servicios.length > 0 && (
            <table className="w-full border-collapse text-left text-sm">
              <thead className="bg-slate-50 text-slate-700">
                <tr>
                  <th className="px-4 py-3 font-semibold">ID</th>
                  <th className="px-4 py-3 font-semibold">Descripción</th>
                  <th className="px-4 py-3 font-semibold">Tipo</th>
                  <th className="px-4 py-3 font-semibold">Periodicidad</th>
                  <th className="px-4 py-3 font-semibold">Duración</th>
                  <th className="px-4 py-3 font-semibold">Capacidad</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {servicios.map((servicio) => (
                  <tr key={servicio.id}>
                    <td className="px-4 py-3 text-slate-600">{servicio.id}</td>
                    <td className="px-4 py-3 font-medium text-slate-900">
                      {servicio.description ?? "Sin descripción"}
                    </td>
                    <td className="px-4 py-3 text-slate-600">
                      {servicio.typeService ?? "—"}
                    </td>
                    <td className="px-4 py-3 text-slate-600">
                      {servicio.periodicity ?? "—"}
                    </td>
                    <td className="px-4 py-3 text-slate-600">
                      {servicio.duration ? `${servicio.duration} h` : "—"}
                    </td>
                    <td className="px-4 py-3 text-slate-600">
                      {servicio.capacity ?? "—"}
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