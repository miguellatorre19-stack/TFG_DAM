"use client";

import { FormEvent, useState } from "react";
import { useRouter } from "next/navigation";
import { login, saveSession } from "@/services/authService";

export default function LoginPage() {
  const router = useRouter();

  const [email, setEmail] = useState("admin@teagestion.local");
  const [password, setPassword] = useState("Admin1234");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setError("");
    setLoading(true);

    try {
      const response = await login({ email, password });
      saveSession(response);
      router.push("/dashboard");
    } catch {
      setError("No se ha podido iniciar sesión. Revisa el email y la contraseña.");
    } finally {
      setLoading(false);
    }
  }

  return (
    <main className="flex min-h-screen items-center justify-center bg-slate-100 px-4">
      <section className="w-full max-w-md rounded-2xl bg-white p-8 shadow">
        <div className="mb-6">
          <p className="text-sm font-medium text-indigo-600">TEA Gestión</p>
          <h1 className="mt-2 text-2xl font-bold text-slate-900">
            Acceso a la plataforma
          </h1>
          <p className="mt-2 text-sm text-slate-600">
            Inicia sesión para acceder al panel interno de gestión.
          </p>
        </div>

        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label className="mb-1 block text-sm font-medium text-slate-700">
              Email
            </label>
            <input
              type="email"
              className="w-full rounded-lg border border-slate-300 px-3 py-2 text-slate-900 outline-none focus:border-indigo-500"
              value={email}
              onChange={(event) => setEmail(event.target.value)}
              required
            />
          </div>

          <div>
            <label className="mb-1 block text-sm font-medium text-slate-700">
              Contraseña
            </label>
            <input
              type="password"
              className="w-full rounded-lg border border-slate-300 px-3 py-2 text-slate-900 outline-none focus:border-indigo-500"
              value={password}
              onChange={(event) => setPassword(event.target.value)}
              required
            />
          </div>

          {error && (
            <p className="rounded-lg bg-red-50 px-3 py-2 text-sm text-red-700">
              {error}
            </p>
          )}

          <button
            type="submit"
            disabled={loading}
            className="w-full rounded-lg bg-indigo-600 px-4 py-2 font-medium text-white hover:bg-indigo-700 disabled:cursor-not-allowed disabled:bg-indigo-300"
          >
            {loading ? "Accediendo..." : "Entrar"}
          </button>
        </form>
      </section>
    </main>
  );
}