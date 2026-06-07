const API_URL = process.env.NEXT_PUBLIC_API_URL ?? "http://localhost:8080/api/v1";

interface ApiFetchOptions extends RequestInit {
  auth?: boolean;
}

export class ApiError extends Error {
  status: number;

  constructor(status: number, message: string) {
    super(message);
    this.name = "ApiError";
    this.status = status;
  }
}

function buildErrorMessage(rawBody: string, status: number): string {
  if (!rawBody) {
    return `Error HTTP ${status}`;
  }

  try {
    const parsed = JSON.parse(rawBody) as {
      message?: string;
      error?: Record<string, string>;
    };

    const fieldErrors = parsed.error
      ? Object.entries(parsed.error)
          .map(([field, message]) => `${field}: ${message}`)
          .join(" | ")
      : "";

    if (parsed.message && fieldErrors) {
      return `${parsed.message}. ${fieldErrors}`;
    }

    if (parsed.message) {
      return parsed.message;
    }

    if (fieldErrors) {
      return fieldErrors;
    }
  } catch {
    return `Error HTTP ${status}: ${rawBody}`;
  }

  return `Error HTTP ${status}`;
}

export async function apiFetch<T>(
  endpoint: string,
  options: ApiFetchOptions = {}
): Promise<T> {
  const { auth = true, ...fetchOptions } = options;

  const token =
    typeof window !== "undefined" ? localStorage.getItem("token") : null;

  const headers = new Headers(fetchOptions.headers);

  if (!headers.has("Content-Type")) {
    headers.set("Content-Type", "application/json");
  }

  if (auth && token) {
    headers.set("Authorization", `Bearer ${token}`);
  }

  const response = await fetch(`${API_URL}${endpoint}`, {
    ...fetchOptions,
    headers,
  });

  if (!response.ok) {
    const message = buildErrorMessage(await response.text(), response.status);
    throw new ApiError(response.status, message);
  }

  if (response.status === 204) {
    return null as T;
  }

  const rawBody = await response.text();

  if (!rawBody.trim()) {
    return null as T;
  }

  const contentType = response.headers.get("Content-Type") ?? "";

  if (contentType.includes("application/json")) {
    return JSON.parse(rawBody) as T;
  }

  return rawBody as T;
}
