"use client";

import AppNav from "@/components/AppNav";

export default function ServiciosPage() {
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

        <div className="rounded-2xl bg-white p-6 shadow">
          <p className="text-sm text-slate-600">
            En esta sección solo los perfiles autorizados podrán asignar socios a servicios.
          </p>
        </div>
      </section>
    </main>
  );
}