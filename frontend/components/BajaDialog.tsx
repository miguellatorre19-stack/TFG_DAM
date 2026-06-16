"use client";

interface BajaDialogProps {
  open: boolean;
  title: string;
  description: string;
  reason: string;
  error?: string;
  busy?: boolean;
  onReasonChange: (value: string) => void;
  onClose: () => void;
  onConfirm: () => void;
}

export default function BajaDialog({
  open,
  title,
  description,
  reason,
  error = "",
  busy = false,
  onReasonChange,
  onClose,
  onConfirm,
}: BajaDialogProps) {
  if (!open) {
    return null;
  }

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-slate-950/50 px-4">
      <div className="w-full max-w-lg rounded-2xl bg-white p-6 shadow-2xl">
        <div className="mb-4">
          <h3 className="text-lg font-semibold text-slate-900">{title}</h3>
          <p className="mt-1 text-sm text-slate-600">{description}</p>
        </div>

        <label className="block">
          <span className="mb-1 block text-sm font-medium text-slate-700">
            Motivo de baja
          </span>
          <textarea
            className="min-h-28 w-full rounded-lg border border-slate-300 px-3 py-2 text-sm text-slate-900 outline-none focus:border-indigo-500"
            value={reason}
            onChange={(event) => onReasonChange(event.target.value)}
            placeholder="Indica el motivo comunicado al equipo administrativo."
            disabled={busy}
          />
        </label>

        {error && (
          <p className="mt-4 rounded-lg bg-red-50 px-3 py-2 text-sm text-red-700">
            {error}
          </p>
        )}

        <div className="mt-6 flex justify-end gap-3">
          <button
            type="button"
            onClick={onClose}
            disabled={busy}
            className="rounded-lg border border-slate-300 px-4 py-2 text-sm font-medium text-slate-700 hover:bg-slate-50 disabled:cursor-not-allowed disabled:opacity-60"
          >
            Cancelar
          </button>
          <button
            type="button"
            onClick={onConfirm}
            disabled={busy || reason.trim().length === 0}
            className="rounded-lg bg-red-600 px-4 py-2 text-sm font-medium text-white hover:bg-red-700 disabled:cursor-not-allowed disabled:bg-red-300"
          >
            {busy ? "Registrando baja..." : "Confirmar baja"}
          </button>
        </div>
      </div>
    </div>
  );
}
