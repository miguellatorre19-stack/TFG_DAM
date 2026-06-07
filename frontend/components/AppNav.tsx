"use client";

import Link from "next/link";
import { useRouter } from "next/navigation";
import {
  canAccessAdminPanel,
  canAccessPrivateArea,
  getUser,
  logout,
} from "@/services/authService";

export default function AppNav() {
  const router = useRouter();
  const user = getUser();
  const showAdminLinks = canAccessAdminPanel(user);
  const showPrivateAreaLink = canAccessPrivateArea(user);

  function handleLogout() {
    logout();
    router.push("/login");
  }

  return (
    <header className="border-b bg-white">
      <div className="mx-auto flex max-w-6xl items-center justify-between px-6 py-4">
        <div>
          <p className="text-sm font-medium text-indigo-600">TEA Gestión</p>
          <h1 className="text-xl font-bold text-slate-900">Panel interno</h1>
        </div>

        <nav className="flex items-center gap-4 text-sm">
          {showAdminLinks && (
            <>
              <Link
                href="/dashboard"
                className="font-medium text-slate-700 hover:text-indigo-600"
              >
                Dashboard
              </Link>

              <Link
                href="/socios"
                className="font-medium text-slate-700 hover:text-indigo-600"
              >
                Socios
              </Link>

              <Link
                href="/participantes"
                className="font-medium text-slate-700 hover:text-indigo-600"
              >
                Participantes
              </Link>

              <Link
                href="/actividades"
                className="font-medium text-slate-700 hover:text-indigo-600"
              >
                Actividades
              </Link>

              <Link
                href="/servicios"
                className="font-medium text-slate-700 hover:text-indigo-600"
              >
                Servicios
              </Link>

              <Link
                href="/trabajadores"
                className="font-medium text-slate-700 hover:text-indigo-600"
              >
                Trabajadores
              </Link>

              <Link
                href="/inscripciones"
                className="font-medium text-slate-700 hover:text-indigo-600"
              >
                Inscripciones
              </Link>
            </>
          )}

          {showPrivateAreaLink && (
            <Link
              href="/area-privada"
              className="font-medium text-slate-700 hover:text-indigo-600"
            >
              Area privada
            </Link>
          )}

          <button
            onClick={handleLogout}
            className="rounded-lg border border-slate-300 px-4 py-2 font-medium text-slate-700 hover:bg-slate-50"
          >
            Cerrar sesión
          </button>
        </nav>
      </div>
    </header>
  );
}
