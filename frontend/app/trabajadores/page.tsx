"use client";

import AppNav from "@/components/AppNav";

export default function TrabajadoresPage() {
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

        <div className="rounded-2xl bg-white p-6 shadow">
          <p className="text-sm text-slate-600">
            Esta sección permitirá consultar trabajadores y sus servicios asociados.
          </p>
        </div>
      </section>
    </main>
  );
}