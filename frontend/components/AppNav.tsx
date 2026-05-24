"use client";

import Link from "next/link";
import { useRouter } from "next/navigation";
import { logout } from "@/services/authService";

export default function AppNav() {
  const router = useRouter();

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