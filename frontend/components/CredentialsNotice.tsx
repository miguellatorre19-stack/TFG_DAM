"use client";

import { useState } from "react";
import type { IssuedAccessCredentials } from "@/types/access";

interface CredentialsNoticeProps {
  credentials: IssuedAccessCredentials;
  entityLabel: string;
  onDismiss?: () => void;
}

export default function CredentialsNotice({
  credentials,
  entityLabel,
  onDismiss,
}: CredentialsNoticeProps) {
  const [copied, setCopied] = useState(false);

  async function handleCopy() {
    const content = `Email: ${credentials.email}\nCodigo: ${credentials.initialPassword}`;
    await navigator.clipboard.writeText(content);
    setCopied(true);
    window.setTimeout(() => setCopied(false), 2000);
  }

  return (
    <div className="mt-4 rounded-2xl border border-amber-200 bg-amber-50 p-4 text-slate-900">
      <div className="flex flex-col gap-3 md:flex-row md:items-start md:justify-between">
        <div>
          <h4 className="text-sm font-semibold uppercase tracking-[0.18em] text-amber-800">
            Credenciales generadas
          </h4>
          <p className="mt-1 text-sm text-amber-900">
            Entrega estas credenciales al {entityLabel}. La contrasena no volvera a poder consultarse; si se pierde, tendras que regenerar un nuevo codigo.
          </p>
        </div>

        <div className="flex gap-2">
          <button
            type="button"
            onClick={handleCopy}
            className="rounded-lg bg-amber-600 px-3 py-2 text-sm font-medium text-white hover:bg-amber-700"
          >
            {copied ? "Copiado" : "Copiar"}
          </button>
          {onDismiss && (
            <button
              type="button"
              onClick={onDismiss}
              className="rounded-lg border border-amber-300 px-3 py-2 text-sm font-medium text-amber-900 hover:bg-amber-100"
            >
              Cerrar
            </button>
          )}
        </div>
      </div>

      <dl className="mt-4 grid gap-3 md:grid-cols-2">
        <div className="rounded-xl bg-white px-4 py-3">
          <dt className="text-xs font-semibold uppercase tracking-[0.16em] text-slate-500">
            Email de acceso
          </dt>
          <dd className="mt-1 text-base font-semibold text-slate-900">
            {credentials.email}
          </dd>
        </div>
        <div className="rounded-xl bg-white px-4 py-3">
          <dt className="text-xs font-semibold uppercase tracking-[0.16em] text-slate-500">
            Codigo inicial
          </dt>
          <dd className="mt-1 text-base font-semibold text-slate-900">
            {credentials.initialPassword}
          </dd>
        </div>
      </dl>
    </div>
  );
}
