"use client";

import { useEffect, useState } from "react";
import { getUser, logout } from "@/services/authService";
import type { LoginResponse } from "@/types/auth";
import { useRouter } from "next/navigation";

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

  function handleLogout() {
    logout();
    router.push("/login");
  }

  if (!user) {
    return (
      <main className="flex min-h-screen items-center justify-center bg-slate-100">
        <p className="text-sm text-slate-600">Redirigiendo al login...</p>
      </main>
    );
  }

  return (
    <main className="min-h-screen bg-slate-100">
      <header className="border-b bg-white">
        <div className="mx-auto flex max-w-6xl items-center justify-between px-6 py-4">
          <div>
            <p className="text-sm font-medium text-indigo-600">TEA Gestión</p>
            <h1 className="text-xl font-bold text-slate-900">Dashboard</h1>
          </div>

          <button
            onClick={handleLogout}
            className="rounded-lg border border-slate-300 px-4 py-2 text-sm font-medium text-slate-700 hover:bg-slate-50"
          >
            Cerrar sesión
          </button>
        </div>
      </header>

      <section className="mx-auto max-w-6xl px-6 py-8">
        <div className="rounded-2xl bg-white p-6 shadow">
          <h2 className="text-lg font-semibold text-slate-900">
            Bienvenido, {user.name}
          </h2>
          <p className="mt-2 text-slate-600">
            Has accedido correctamente a la zona privada de gestión.
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
      </section>
    </main>
  );
}