"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import AppNav from "@/components/AppNav";
import { getUser } from "@/services/authService";
import { getSocios } from "@/services/socioService";
import type { Socio } from "@/types/socio";

export default function SociosPage() {
  const router = useRouter();

  const [socios, setSocios] = useState<Socio[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    const user = getUser();

    if (!user) {
      router.push("/login");
      return;
    }

    async function loadSocios() {
      try {
        const data = await getSocios();
        setSocios(data);
      } catch {
        setError("No se han podido cargar los socios.");
      } finally {
        setLoading(false);
      }
    }

    loadSocios();
  }, [router]);

  return (
    <main className="min-h-screen bg-slate-100">
      <AppNav />

      <section className="mx-auto max-w-6xl px-6 py-8">
        <div className="mb-6">
          <h2 className="text-2xl font-bold text-slate-900">Socios</h2>
          <p className="mt-2 text-slate-600">
            Listado de socios registrados en la plataforma.
          </p>
        </div>

        <div className="overflow-hidden rounded-2xl bg-white shadow">
          {loading && (
            <p className="p-6 text-sm text-slate-600">Cargando socios...</p>
          )}

          {error && (
            <p className="p-6 text-sm text-red-700">{error}</p>
          )}

          {!loading && !error && socios.length === 0 && (
            <p className="p-6 text-sm text-slate-600">
              No hay socios registrados todavía.
            </p>
          )}

          {!loading && !error && socios.length > 0 && (
            <table className="w-full border-collapse text-left text-sm">
              <thead className="bg-slate-50 text-slate-700">
                <tr>
                  <th className="px-4 py-3 font-semibold">ID</th>
                  <th className="px-4 py-3 font-semibold">Nombre</th>
                  <th className="px-4 py-3 font-semibold">Email</th>
                  <th className="px-4 py-3 font-semibold">Teléfono</th>
                  <th className="px-4 py-3 font-semibold">Estado</th>
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