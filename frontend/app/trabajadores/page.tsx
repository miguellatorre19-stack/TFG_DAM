"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import AppNav from "@/components/AppNav";
import { getUser } from "@/services/authService";
import { getTrabajadores } from "@/services/trabajadorService";
import type { Trabajador } from "@/types/trabajador";

export default function TrabajadoresPage() {
  const router = useRouter();

  const [trabajadores, setTrabajadores] = useState<Trabajador[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    const user = getUser();

    if (!user) {
      router.push("/login");
      return;
    }

    async function loadTrabajadores() {
      try {
        const data = await getTrabajadores();
        setTrabajadores(data);
      } catch {
        setError("No se han podido cargar los trabajadores.");
      } finally {
        setLoading(false);
      }
    }

    loadTrabajadores();
  }, [router]);

  return (
    <main className="min-h-screen bg-slate-100">
      <AppNav />

      <section className="mx-auto max-w-6xl px-6 py-8">
        <div className="mb-6">
          <h2 className="text-2xl font-bold text-slate-900">Trabajadores</h2>
          <p className="mt-2 text-slate-600">
            Gestión del personal profesional asociado a servicios y actividades.
          </p>
        </div>

        <div className="overflow-hidden rounded-2xl bg-white shadow">
          {loading && (
            <p className="p-6 text-sm text-slate-600">Cargando trabajadores...</p>
          )}

          {error && <p className="p-6 text-sm text-red-700">{error}</p>}

          {!loading && !error && trabajadores.length === 0 && (
            <p className="p-6 text-sm text-slate-600">
              No hay trabajadores registrados todavía.
            </p>
          )}

          {!loading && !error && trabajadores.length > 0 && (
            <table className="w-full border-collapse text-left text-sm">
              <thead className="bg-slate-50 text-slate-700">
                <tr>
                  <th className="px-4 py-3 font-semibold">ID</th>
                  <th className="px-4 py-3 font-semibold">Nombre</th>
                  <th className="px-4 py-3 font-semibold">Email</th>
                  <th className="px-4 py-3 font-semibold">Teléfono</th>
                  <th className="px-4 py-3 font-semibold">Contrato</th>
                  <th className="px-4 py-3 font-semibold">Estado</th>
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
                      {trabajador.email ?? "—"}
                    </td>
                    <td className="px-4 py-3 text-slate-600">
                      {trabajador.phoneNumber ?? "—"}
                    </td>
                    <td className="px-4 py-3 text-slate-600">
                      {trabajador.contractType ?? "—"}
                    </td>
                    <td className="px-4 py-3 text-slate-600">
                      {trabajador.active === false ? "Inactivo" : "Activo"}
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