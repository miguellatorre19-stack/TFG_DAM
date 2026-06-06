"use client";

import { FormEvent, useEffect, useMemo, useState } from "react";
import { useRouter } from "next/navigation";
import { getMe, getUser, logout } from "@/services/authService";
import { getActividades, inscribirActividad } from "@/services/actividadService";
import { getServicios, inscribirServicio } from "@/services/servicioService";
import type { LoginResponse, MeResponse } from "@/types/auth";
import type { Actividad } from "@/types/actividad";
import type { Servicio } from "@/types/servicio";

type SectionId = "inicio" | "actividades" | "servicios" | "solicitudes" | "ayuda";
type ItemKind = "actividad" | "servicio";
type WizardStep = 1 | 2 | 3;

interface SelectedItem {
  id: number;
  title: string;
  kind: ItemKind;
  date?: string;
  duration?: number;
  capacity?: number;
}

interface LocalRequest {
  id: string;
  kind: ItemKind;
  title: string;
  status: "Enviada" | "No enviada";
  createdAt: string;
}

const sections: Array<{ id: SectionId; label: string; helper: string }> = [
  { id: "inicio", label: "Inicio", helper: "Resumen y siguientes pasos" },
  { id: "actividades", label: "Actividades", helper: "Consultar e inscribirse" },
  { id: "servicios", label: "Servicios", helper: "Consultar y solicitar" },
  { id: "solicitudes", label: "Mis solicitudes", helper: "Estado de lo enviado" },
  { id: "ayuda", label: "Ayuda", helper: "Contacto y dudas" },
];

const statusDescription = {
  Enviada: "La asociacion ha recibido la solicitud.",
  "No enviada": "La solicitud no se pudo completar. Puedes intentarlo de nuevo.",
};

export default function PrivateAreaPage() {
  const router = useRouter();

  const [user, setUser] = useState<LoginResponse | null | undefined>(undefined);
  const [profile, setProfile] = useState<MeResponse | null>(null);
  const [activeSection, setActiveSection] = useState<SectionId>("inicio");
  const [showHelp, setShowHelp] = useState(true);
  const [lowStimulus, setLowStimulus] = useState(false);
  const [largeText, setLargeText] = useState(false);
  const [actividades, setActividades] = useState<Actividad[]>([]);
  const [servicios, setServicios] = useState<Servicio[]>([]);
  const [loading, setLoading] = useState(true);
  const [loadError, setLoadError] = useState("");
  const [activityFilter, setActivityFilter] = useState("");
  const [serviceFilter, setServiceFilter] = useState("");
  const [selectedItem, setSelectedItem] = useState<SelectedItem | null>(null);
  const [wizardStep, setWizardStep] = useState<WizardStep>(1);
  const [participantId, setParticipantId] = useState("");
  const [formError, setFormError] = useState("");
  const [submitting, setSubmitting] = useState(false);
  const [confirmation, setConfirmation] = useState("");
  const [requests, setRequests] = useState<LocalRequest[]>([]);

  useEffect(() => {
    const timeoutId = window.setTimeout(() => {
      const currentUser = getUser();

      if (!currentUser) {
        router.push("/login");
        return;
      }

      setUser(currentUser);
      const storedLowStimulus = localStorage.getItem("private-low-stimulus") === "true";
      const storedLargeText = localStorage.getItem("private-large-text") === "true";
      const storedRequests = localStorage.getItem("private-requests");

      setLowStimulus(storedLowStimulus);
      setLargeText(storedLargeText);
      setRequests(storedRequests ? JSON.parse(storedRequests) : []);

      async function loadContent() {
        try {
          const [meData, activityData, serviceData] = await Promise.all([
            getMe(),
            getActividades(),
            getServicios(),
          ]);
          setProfile(meData);
          if (meData.participanteIds.length === 1) {
            setParticipantId(String(meData.participanteIds[0]));
          }
          setActividades(activityData);
          setServicios(serviceData);
        } catch {
          setLoadError(
            "No se han podido cargar las actividades y servicios. Revisa la conexion y vuelve a intentarlo."
          );
        } finally {
          setLoading(false);
        }
      }

      loadContent();
    }, 0);

    return () => window.clearTimeout(timeoutId);
  }, [router]);

  useEffect(() => {
    document.documentElement.classList.toggle("low-stimulus", lowStimulus);
    localStorage.setItem("private-low-stimulus", String(lowStimulus));
  }, [lowStimulus]);

  useEffect(() => {
    localStorage.setItem("private-large-text", String(largeText));
  }, [largeText]);

  const filteredActivities = useMemo(() => {
    const filter = activityFilter.trim().toLowerCase();
    return actividades.filter((actividad) =>
      `${actividad.description ?? ""} ${actividad.typeActivity ?? ""} ${actividad.dayActivity ?? ""}`
        .toLowerCase()
        .includes(filter)
    );
  }, [actividades, activityFilter]);

  const filteredServices = useMemo(() => {
    const filter = serviceFilter.trim().toLowerCase();
    return servicios.filter((servicio) =>
      `${servicio.description ?? ""} ${servicio.typeService ?? ""} ${servicio.periodicity ?? ""}`
        .toLowerCase()
        .includes(filter)
    );
  }, [servicios, serviceFilter]);

  function closeSession() {
    logout();
    router.push("/login");
  }

  function startWizard(item: SelectedItem) {
    setSelectedItem(item);
    setWizardStep(1);
    setFormError("");
    setConfirmation("");
    setActiveSection(item.kind === "actividad" ? "actividades" : "servicios");
    window.setTimeout(() => {
      document.getElementById("inscription-panel")?.scrollIntoView({
        behavior: lowStimulus ? "auto" : "smooth",
      });
    }, 0);
  }

  function cancelWizard() {
    setSelectedItem(null);
    setWizardStep(1);
    setParticipantId("");
    setFormError("");
  }

  async function submitInscription(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setFormError("");

    if (!selectedItem) {
      setFormError("Selecciona una actividad o servicio antes de enviar.");
      return;
    }

    const parsedParticipantId = Number(participantId);
    if (!Number.isInteger(parsedParticipantId) || parsedParticipantId <= 0) {
      setFormError("Selecciona el participante que va a usar esta actividad o servicio.");
      setWizardStep(2);
      return;
    }

    setSubmitting(true);
    try {
      if (selectedItem.kind === "actividad") {
        await inscribirActividad(selectedItem.id, parsedParticipantId);
      } else {
        await inscribirServicio(selectedItem.id, parsedParticipantId);
      }

      const request: LocalRequest = {
        id: `SOL-${Date.now().toString().slice(-6)}`,
        kind: selectedItem.kind,
        title: selectedItem.title,
        status: "Enviada",
        createdAt: new Date().toLocaleDateString("es-ES"),
      };
      const nextRequests = [request, ...requests];
      setRequests(nextRequests);
      localStorage.setItem("private-requests", JSON.stringify(nextRequests));
      setConfirmation(
        `Solicitud ${request.id} enviada. La asociacion revisara la informacion.`
      );
      setActiveSection("solicitudes");
      cancelWizard();
    } catch {
      setFormError(
        "No se ha podido enviar la solicitud. Revisa el ID de participante y vuelve a pulsar Enviar solicitud."
      );
    } finally {
      setSubmitting(false);
    }
  }

  if (user === undefined || user === null) {
    return (
      <main className="flex min-h-screen items-center justify-center bg-[#f4f1ea] px-6">
        <p className="rounded-2xl bg-white px-6 py-4 text-sm text-[#3d4b47] shadow-sm">
          Cargando area privada...
        </p>
      </main>
    );
  }

  return (
    <main
      className={`min-h-screen bg-[#f4f1ea] text-[#23312f] ${
        largeText ? "text-lg" : "text-base"
      }`}
    >
      <header className="sticky top-0 z-20 border-b border-[#d8d1c2] bg-[#fffdf7]/95 backdrop-blur">
        <div className="mx-auto flex max-w-7xl flex-col gap-4 px-5 py-4 lg:flex-row lg:items-center lg:justify-between">
          <div>
            <p className="text-sm font-semibold text-[#23675b]">Area privada</p>
            <h1 className="text-2xl font-bold tracking-tight text-[#23312f]">
              Hola, {user.name}
            </h1>
          </div>

          <nav aria-label="Menu principal del area privada" className="flex flex-wrap gap-2">
            {sections.map((section) => (
              <button
                key={section.id}
                type="button"
                onClick={() => setActiveSection(section.id)}
                className={`rounded-full border px-4 py-2 text-sm font-semibold ${
                  activeSection === section.id
                    ? "border-[#23675b] bg-[#23675b] text-white"
                    : "border-[#c9c0ad] bg-white text-[#35433f] hover:bg-[#f1eadc]"
                }`}
                aria-current={activeSection === section.id ? "page" : undefined}
              >
                {section.label}
              </button>
            ))}
            <button
              type="button"
              onClick={closeSession}
              className="rounded-full border border-[#c9c0ad] bg-white px-4 py-2 text-sm font-semibold text-[#35433f] hover:bg-[#f1eadc]"
            >
              Cerrar sesion
            </button>
          </nav>
        </div>
      </header>

      <div className="mx-auto grid max-w-7xl gap-6 px-5 py-6 lg:grid-cols-[280px_1fr]">
        <aside className="space-y-4">
          <section className="rounded-3xl border border-[#d8d1c2] bg-[#fffdf7] p-5 shadow-sm">
            <h2 className="text-lg font-bold">Preferencias</h2>
            <p className="mt-2 text-sm leading-6 text-[#52615c]">
              Puedes ajustar la pantalla sin cambiar tus datos.
            </p>

            <div className="mt-4 space-y-3">
              <label className="flex items-start gap-3 rounded-2xl bg-[#f7f2e8] p-3">
                <input
                  type="checkbox"
                  checked={lowStimulus}
                  onChange={(event) => setLowStimulus(event.target.checked)}
                  className="mt-1 h-5 w-5"
                />
                <span>
                  <span className="block font-semibold">Modo bajo estimulo</span>
                  <span className="text-sm text-[#52615c]">
                    Reduce movimiento y decoracion.
                  </span>
                </span>
              </label>

              <label className="flex items-start gap-3 rounded-2xl bg-[#f7f2e8] p-3">
                <input
                  type="checkbox"
                  checked={largeText}
                  onChange={(event) => setLargeText(event.target.checked)}
                  className="mt-1 h-5 w-5"
                />
                <span>
                  <span className="block font-semibold">Texto grande</span>
                  <span className="text-sm text-[#52615c]">
                    Aumenta el tamano de lectura.
                  </span>
                </span>
              </label>
            </div>
          </section>

          <section className="rounded-3xl border border-[#d8d1c2] bg-[#fffdf7] p-5 shadow-sm">
            <button
              type="button"
              onClick={() => setShowHelp((value) => !value)}
              className="flex w-full items-center justify-between text-left text-lg font-bold"
              aria-expanded={showHelp}
            >
              Ayuda rapida
              <span className="text-sm font-semibold text-[#23675b]">
                {showHelp ? "Ocultar" : "Mostrar"}
              </span>
            </button>

            {showHelp && (
              <ol className="mt-4 space-y-3 text-sm leading-6 text-[#3d4b47]">
                <li>
                  <strong>1.</strong> Elige Actividades o Servicios.
                </li>
                <li>
                  <strong>2.</strong> Pulsa Iniciar inscripcion o Solicitar servicio.
                </li>
                <li>
                  <strong>3.</strong> Revisa el resumen antes de enviar.
                </li>
              </ol>
            )}
          </section>
        </aside>

        <section className="space-y-6">
          <div className="rounded-3xl border border-[#d8d1c2] bg-[#fffdf7] p-6 shadow-sm">
            <p className="text-sm font-semibold text-[#23675b]">
              Estas en: {sections.find((section) => section.id === activeSection)?.label}
            </p>
            <h2 className="mt-2 text-3xl font-bold text-[#23312f]">
              {sections.find((section) => section.id === activeSection)?.helper}
            </h2>
            <p className="mt-3 max-w-3xl leading-7 text-[#52615c]">
              La pantalla muestra una tarea principal cada vez. Las acciones indican
              que ocurrira despues de pulsarlas.
            </p>
            {profile && (
              <p className="mt-3 rounded-2xl bg-[#f7f2e8] px-4 py-3 text-sm text-[#3d4b47]">
                Perfil detectado: <strong>{profile.profileType}</strong>
                {profile.socioId ? ` | Socio #${profile.socioId}` : ""}
                {profile.participanteIds.length > 0
                  ? ` | Participantes disponibles: ${profile.participanteIds.join(", ")}`
                  : ""}
              </p>
            )}
          </div>

          {loadError && (
            <div className="rounded-3xl border border-[#bf6b5b] bg-[#fff4ef] p-5 text-[#703729]">
              <strong>No se ha podido cargar todo el contenido.</strong>
              <p className="mt-2">{loadError}</p>
            </div>
          )}

          {activeSection === "inicio" && (
            <HomePanel
              actividadesCount={actividades.length}
              serviciosCount={servicios.length}
              requestsCount={requests.length}
              loading={loading}
              onGoToActivities={() => setActiveSection("actividades")}
              onGoToServices={() => setActiveSection("servicios")}
            />
          )}

          {activeSection === "actividades" && (
            <ContentList
              kind="actividad"
              title="Actividades disponibles"
              intro="Consulta la actividad, revisa fecha y plazas, y empieza la inscripcion cuando lo tengas claro."
              filter={activityFilter}
              onFilterChange={setActivityFilter}
              loading={loading}
              items={filteredActivities.map((actividad) => ({
                id: actividad.id,
                title: actividad.description ?? "Actividad sin descripcion",
                kind: "actividad",
                date: actividad.dayActivity,
                duration: actividad.duration,
                capacity: actividad.capacity,
              }))}
              onStart={startWizard}
            />
          )}

          {activeSection === "servicios" && (
            <ContentList
              kind="servicio"
              title="Servicios disponibles"
              intro="Revisa para que sirve cada servicio y solicita el que necesites."
              filter={serviceFilter}
              onFilterChange={setServiceFilter}
              loading={loading}
              items={filteredServices.map((servicio) => ({
                id: servicio.id,
                title: servicio.description ?? "Servicio sin descripcion",
                kind: "servicio",
                duration: servicio.duration,
                capacity: servicio.capacity,
              }))}
              onStart={startWizard}
            />
          )}

          {(activeSection === "actividades" || activeSection === "servicios") && (
            <InscriptionWizard
              selectedItem={selectedItem}
              step={wizardStep}
              participantId={participantId}
              participantIds={profile?.participanteIds ?? []}
              formError={formError}
              submitting={submitting}
              onStepChange={setWizardStep}
              onParticipantIdChange={setParticipantId}
              onCancel={cancelWizard}
              onSubmit={submitInscription}
            />
          )}

          {activeSection === "solicitudes" && (
            <RequestsPanel confirmation={confirmation} requests={requests} />
          )}

          {activeSection === "ayuda" && <HelpPanel />}
        </section>
      </div>
    </main>
  );
}

function HomePanel({
  actividadesCount,
  serviciosCount,
  requestsCount,
  loading,
  onGoToActivities,
  onGoToServices,
}: {
  actividadesCount: number;
  serviciosCount: number;
  requestsCount: number;
  loading: boolean;
  onGoToActivities: () => void;
  onGoToServices: () => void;
}) {
  return (
    <div className="grid gap-4 md:grid-cols-3">
      <InfoCard label="Actividades" value={loading ? "..." : String(actividadesCount)} text="Consulta talleres y eventos." />
      <InfoCard label="Servicios" value={loading ? "..." : String(serviciosCount)} text="Solicita apoyo especializado." />
      <InfoCard label="Solicitudes" value={String(requestsCount)} text="Revisa lo enviado desde este navegador." />

      <div className="rounded-3xl border border-[#d8d1c2] bg-[#fffdf7] p-6 shadow-sm md:col-span-3">
        <h3 className="text-xl font-bold">Que puedes hacer ahora</h3>
        <div className="mt-5 grid gap-4 md:grid-cols-2">
          <button
            type="button"
            onClick={onGoToActivities}
            className="rounded-2xl border-2 border-[#23675b] bg-[#23675b] px-5 py-4 text-left font-bold text-white"
          >
            Ver actividades disponibles
            <span className="mt-2 block text-sm font-normal text-[#e6f2ee]">
              Despues podras iniciar una inscripcion guiada.
            </span>
          </button>
          <button
            type="button"
            onClick={onGoToServices}
            className="rounded-2xl border-2 border-[#23675b] bg-white px-5 py-4 text-left font-bold text-[#23675b]"
          >
            Ver servicios disponibles
            <span className="mt-2 block text-sm font-normal text-[#52615c]">
              Despues podras solicitar un servicio.
            </span>
          </button>
        </div>
      </div>
    </div>
  );
}

function InfoCard({ label, value, text }: { label: string; value: string; text: string }) {
  return (
    <article className="rounded-3xl border border-[#d8d1c2] bg-[#fffdf7] p-6 shadow-sm">
      <p className="text-sm font-semibold text-[#23675b]">{label}</p>
      <p className="mt-2 text-4xl font-bold">{value}</p>
      <p className="mt-3 leading-6 text-[#52615c]">{text}</p>
    </article>
  );
}

function ContentList({
  kind,
  title,
  intro,
  filter,
  onFilterChange,
  loading,
  items,
  onStart,
}: {
  kind: ItemKind;
  title: string;
  intro: string;
  filter: string;
  onFilterChange: (value: string) => void;
  loading: boolean;
  items: SelectedItem[];
  onStart: (item: SelectedItem) => void;
}) {
  const actionText = kind === "actividad" ? "Iniciar inscripcion" : "Solicitar servicio";

  return (
    <section className="rounded-3xl border border-[#d8d1c2] bg-[#fffdf7] p-6 shadow-sm">
      <div className="flex flex-col gap-4 md:flex-row md:items-start md:justify-between">
        <div>
          <h3 className="text-2xl font-bold">{title}</h3>
          <p className="mt-2 max-w-3xl leading-7 text-[#52615c]">{intro}</p>
        </div>
        <label className="block min-w-64">
          <span className="mb-2 block text-sm font-semibold">Buscar por texto</span>
          <input
            type="search"
            value={filter}
            onChange={(event) => onFilterChange(event.target.value)}
            className="w-full rounded-2xl border border-[#b9ad98] bg-white px-4 py-3 text-[#23312f]"
            placeholder="Ejemplo: taller"
          />
        </label>
      </div>

      {loading && <p className="mt-6 rounded-2xl bg-[#f7f2e8] p-4">Cargando contenido...</p>}

      {!loading && items.length === 0 && (
        <p className="mt-6 rounded-2xl bg-[#f7f2e8] p-4">
          No hay resultados para mostrar ahora.
        </p>
      )}

      <div className="mt-6 grid gap-4 lg:grid-cols-2">
        {items.map((item) => (
          <article
            key={`${item.kind}-${item.id}`}
            className="rounded-3xl border border-[#d8d1c2] bg-white p-5 shadow-sm"
          >
            <p className="text-sm font-semibold text-[#23675b]">
              {kind === "actividad" ? "Actividad" : "Servicio"} #{item.id}
            </p>
            <h4 className="mt-2 text-xl font-bold">{item.title}</h4>
            <dl className="mt-4 grid gap-3 text-sm text-[#3d4b47] sm:grid-cols-3">
              <div className="rounded-2xl bg-[#f7f2e8] p-3">
                <dt className="font-semibold">Fecha</dt>
                <dd>{item.date ?? "Por confirmar"}</dd>
              </div>
              <div className="rounded-2xl bg-[#f7f2e8] p-3">
                <dt className="font-semibold">Duracion</dt>
                <dd>{item.duration ? `${item.duration} h` : "No indicada"}</dd>
              </div>
              <div className="rounded-2xl bg-[#f7f2e8] p-3">
                <dt className="font-semibold">Plazas</dt>
                <dd>{item.capacity ?? "No indicado"}</dd>
              </div>
            </dl>
            <button
              type="button"
              onClick={() => onStart(item)}
              className="mt-5 w-full rounded-2xl bg-[#23675b] px-5 py-3 font-bold text-white hover:bg-[#1c554b]"
            >
              {actionText}
            </button>
          </article>
        ))}
      </div>
    </section>
  );
}

function InscriptionWizard({
  selectedItem,
  step,
  participantId,
  participantIds,
  formError,
  submitting,
  onStepChange,
  onParticipantIdChange,
  onCancel,
  onSubmit,
}: {
  selectedItem: SelectedItem | null;
  step: WizardStep;
  participantId: string;
  participantIds: number[];
  formError: string;
  submitting: boolean;
  onStepChange: (step: WizardStep) => void;
  onParticipantIdChange: (value: string) => void;
  onCancel: () => void;
  onSubmit: (event: FormEvent<HTMLFormElement>) => void;
}) {
  return (
    <section
      id="inscription-panel"
      className="rounded-3xl border border-[#c9c0ad] bg-[#fffdf7] p-6 shadow-sm"
    >
      <h3 className="text-2xl font-bold">Proceso de solicitud</h3>
      {!selectedItem ? (
        <p className="mt-3 leading-7 text-[#52615c]">
          Selecciona una actividad o servicio para iniciar el proceso guiado.
        </p>
      ) : (
        <form onSubmit={onSubmit} className="mt-5 space-y-5">
          <div className="grid gap-3 md:grid-cols-3">
            {[1, 2, 3].map((itemStep) => (
              <button
                key={itemStep}
                type="button"
                onClick={() => onStepChange(itemStep as WizardStep)}
                className={`rounded-2xl border p-4 text-left ${
                  step === itemStep
                    ? "border-[#23675b] bg-[#e8f1ed]"
                    : "border-[#d8d1c2] bg-white"
                }`}
              >
                <span className="block text-sm font-semibold text-[#23675b]">
                  Paso {itemStep}
                </span>
                <span className="mt-1 block font-bold">
                  {itemStep === 1 && "Preparar"}
                  {itemStep === 2 && "Completar"}
                  {itemStep === 3 && "Revisar"}
                </span>
              </button>
            ))}
          </div>

          {step === 1 && (
            <div className="rounded-3xl bg-[#f7f2e8] p-5">
              <h4 className="text-xl font-bold">Antes de empezar</h4>
              <p className="mt-3 leading-7">
                Vas a solicitar: <strong>{selectedItem.title}</strong>.
              </p>
              <ul className="mt-3 space-y-2 leading-7">
                <li>La pantalla usara el participante asociado a tu usuario.</li>
                <li>Podras revisar la solicitud antes de enviarla.</li>
                <li>La asociacion revisara la solicitud despues del envio.</li>
              </ul>
              <button
                type="button"
                onClick={() => onStepChange(2)}
                className="mt-5 rounded-2xl bg-[#23675b] px-5 py-3 font-bold text-white"
              >
                Completar datos
              </button>
            </div>
          )}

          {step === 2 && (
            <div className="rounded-3xl bg-[#f7f2e8] p-5">
              {participantIds.length === 0 && (
                <p className="rounded-2xl border border-[#bf6b5b] bg-[#fff4ef] p-4 text-[#703729]">
                  No hay ningun participante asociado a este usuario. Contacta con la asociacion para revisar el acceso.
                </p>
              )}

              {participantIds.length === 1 && (
                <div className="rounded-2xl border border-[#d8d1c2] bg-white p-4">
                  <p className="text-lg font-bold">Participante seleccionado</p>
                  <p className="mt-2 text-[#52615c]">
                    Se usara automaticamente el participante #{participantIds[0]}.
                  </p>
                </div>
              )}

              {participantIds.length > 1 && (
                <label className="block">
                  <span className="mb-2 block text-lg font-bold">Selecciona participante</span>
                  <span className="mb-3 block text-sm text-[#52615c]">
                    Elige quien va a usar la actividad o servicio.
                  </span>
                  <select
                    value={participantId}
                    onChange={(event) => onParticipantIdChange(event.target.value)}
                    className="w-full rounded-2xl border border-[#b9ad98] bg-white px-4 py-3 text-[#23312f]"
                    required
                  >
                    <option value="">Selecciona una opcion</option>
                    {participantIds.map((id) => (
                      <option key={id} value={id}>
                        Participante #{id}
                      </option>
                    ))}
                  </select>
                </label>
              )}
              <button
                type="button"
                onClick={() => onStepChange(3)}
                disabled={participantIds.length === 0}
                className="mt-5 rounded-2xl bg-[#23675b] px-5 py-3 font-bold text-white"
              >
                Revisar solicitud
              </button>
            </div>
          )}

          {step === 3 && (
            <div className="rounded-3xl bg-[#f7f2e8] p-5">
              <h4 className="text-xl font-bold">Resumen antes de enviar</h4>
              <dl className="mt-4 space-y-3">
                <div>
                  <dt className="font-semibold">Solicitud</dt>
                  <dd>{selectedItem.title}</dd>
                </div>
                <div>
                  <dt className="font-semibold">Tipo</dt>
                  <dd>{selectedItem.kind === "actividad" ? "Actividad" : "Servicio"}</dd>
                </div>
                <div>
                  <dt className="font-semibold">Participante</dt>
                  <dd>{participantId ? `Participante #${participantId}` : "Falta seleccionar participante"}</dd>
                </div>
              </dl>
              <div className="mt-5 flex flex-col gap-3 sm:flex-row">
                <button
                  type="submit"
                  disabled={submitting}
                  className="rounded-2xl bg-[#23675b] px-5 py-3 font-bold text-white disabled:bg-[#9bb7af]"
                >
                  {submitting ? "Enviando solicitud..." : "Enviar solicitud"}
                </button>
                <button
                  type="button"
                  onClick={() => onStepChange(2)}
                  className="rounded-2xl border border-[#23675b] bg-white px-5 py-3 font-bold text-[#23675b]"
                >
                  Corregir datos
                </button>
              </div>
            </div>
          )}

          {formError && (
            <p className="rounded-2xl border border-[#bf6b5b] bg-[#fff4ef] p-4 text-[#703729]">
              {formError}
            </p>
          )}

          <button
            type="button"
            onClick={onCancel}
            className="text-sm font-semibold text-[#52615c] underline"
          >
            Cancelar solicitud y volver al listado
          </button>
        </form>
      )}
    </section>
  );
}

function RequestsPanel({
  confirmation,
  requests,
}: {
  confirmation: string;
  requests: LocalRequest[];
}) {
  return (
    <section className="rounded-3xl border border-[#d8d1c2] bg-[#fffdf7] p-6 shadow-sm">
      <h3 className="text-2xl font-bold">Mis solicitudes</h3>
      <p className="mt-2 leading-7 text-[#52615c]">
        Aqui se muestran las solicitudes enviadas desde este navegador durante el uso del area privada.
      </p>

      {confirmation && (
        <p className="mt-5 rounded-2xl border border-[#7fa58e] bg-[#edf7ef] p-4 text-[#244c35]">
          {confirmation}
        </p>
      )}

      {requests.length === 0 ? (
        <p className="mt-5 rounded-2xl bg-[#f7f2e8] p-4">
          Todavia no hay solicitudes enviadas.
        </p>
      ) : (
        <div className="mt-5 space-y-3">
          {requests.map((request) => (
            <article key={request.id} className="rounded-2xl border border-[#d8d1c2] bg-white p-4">
              <div className="flex flex-col gap-2 md:flex-row md:items-center md:justify-between">
                <div>
                  <p className="text-sm font-semibold text-[#23675b]">{request.id}</p>
                  <h4 className="font-bold">{request.title}</h4>
                  <p className="text-sm text-[#52615c]">Enviada el {request.createdAt}</p>
                </div>
                <div className="rounded-2xl bg-[#edf7ef] px-4 py-2 text-sm font-bold text-[#244c35]">
                  {request.status}
                </div>
              </div>
              <p className="mt-3 text-sm text-[#52615c]">
                {statusDescription[request.status]}
              </p>
            </article>
          ))}
        </div>
      )}
    </section>
  );
}

function HelpPanel() {
  return (
    <section className="rounded-3xl border border-[#d8d1c2] bg-[#fffdf7] p-6 shadow-sm">
      <h3 className="text-2xl font-bold">Ayuda</h3>
      <div className="mt-5 grid gap-4 md:grid-cols-2">
        <article className="rounded-2xl bg-[#f7f2e8] p-5">
          <h4 className="font-bold">Si no sabes que elegir</h4>
          <p className="mt-2 leading-7 text-[#52615c]">
            Revisa la descripcion de cada actividad o servicio. Si dudas, contacta con la asociacion antes de enviar.
          </p>
        </article>
        <article className="rounded-2xl bg-[#f7f2e8] p-5">
          <h4 className="font-bold">Si la solicitud no se envia</h4>
          <p className="mt-2 leading-7 text-[#52615c]">
            Comprueba el ID de participante. Si sigue fallando, guarda el nombre de la actividad o servicio y pide ayuda.
          </p>
        </article>
      </div>

      <div className="mt-5 rounded-2xl border border-[#d8d1c2] bg-white p-5">
        <h4 className="font-bold">Contacto de la asociacion</h4>
        <p className="mt-2 text-[#52615c]">
          Canal recomendado: escribir a administracion o llamar en horario de atencion.
        </p>
        <p className="mt-2 text-sm text-[#52615c]">
          Plazo orientativo de respuesta: 2 dias laborables.
        </p>
      </div>
    </section>
  );
}
