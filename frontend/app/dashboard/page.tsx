"use client";

import { useEffect, useState } from "react";
import Link from "next/link";
import { useRouter } from "next/navigation";
import AppNav from "@/components/AppNav";
import { getUser } from "@/services/authService";
import type { LoginResponse } from "@/types/auth";

const modules = [
  {
    title: "Socios",
    description: "Consulta y gestión de socios de la asociación.",
    href: "/socios",
  },
  {
    title: "Participantes",
    description: "Personas participantes vinculadas a socios, actividades o servicios.",
    href: "/participantes",
  },
  {
    title: "Actividades",
    description: "Talleres, eventos y actividades ofertadas por la asociación.",
    href: "/actividades",
  },
  {
    title: "Servicios",
    description: "Servicios especializados asignados por trabajadores cualificados.",
    href: "/servicios",
  },
  {
    title: "Trabajadores",
    description: "Personal profesional y perfiles internos de la entidad.",
    href: "/trabajadores",
  },
];

export default function DashboardPage() {
  const router = useRouter();

  const [user] = useState<LoginResponse | null>(() => {
    if (typeof window === "undefined") {
      return null;
    }

    return getUser();
  });

  useEffect(() => {
    if (!user) {
      router.push("/login");
    }
  }, [router, user]);

  if (!user) {
    return (
      <main className="flex min-h-screen items-center justify-center bg-slate-100">
        <p className="text-sm text-slate-600">Redirigiendo al login...</p>
      </main>
    );
  }

  return (
    <main className="min-h-screen bg-slate-100">
      <AppNav />

      <section className="mx-auto max-w-6xl px-6 py-8">git
        <div className="mb-8 rounded-2xl bg-white p-6 shadow">
          <p className="text-sm font-medium text-indigo-600">Panel de administración</p>

          <h2 className="mt-2 text-2xl font-bold text-slate-900">
            Bienvenido, {user.name}
          </h2>

          <p className="mt-2 text-slate-600">
            Accede a los módulos principales de gestión interna de la asociación.
          </p>

          <div className="mt-6 rounded-xl bg-slate-50 p-4 text-sm text-slate-700">
            <p>
              <strong>Email:</strong> {user.email}
            </p>
            <p>
              <strong>Roles:</strong> {user.roles.join(", ")}
            </p>
          </div>
        </div>

        <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
          {modules.map((module) => (
            <Link
              key={module.href}
              href={module.href}
              className="rounded-2xl bg-white p-6 shadow transition hover:-translate-y-1 hover:shadow-md"
            >
              <h3 className="text-lg font-semibold text-slate-900">
                {module.title}
              </h3>
              <p className="mt-2 text-sm text-slate-600">
                {module.description}
              </p>
              <p className="mt-4 text-sm font-medium text-indigo-600">
                Entrar →
              </p>
            </Link>
          ))}
        </div>
      </section>
    </main>
  );
}