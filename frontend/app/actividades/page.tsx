"use client";

import AppNav from "@/components/AppNav";

export default function ActividadesPage() {
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

        <div className="rounded-2xl bg-white p-6 shadow">
          <p className="text-sm text-slate-600">
            Esta sección mostrará el listado de actividades y permitirá gestionar inscripciones.
          </p>
        </div>
      </section>
    </main>
  );
}