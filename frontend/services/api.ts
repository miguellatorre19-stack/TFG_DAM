const API_URL = process.env.NEXT_PUBLIC_API_URL ?? "http://localhost:8080/api/v1";

interface ApiFetchOptions extends RequestInit {
  auth?: boolean;
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
    const message = await response.text();
    throw new Error(`Error HTTP ${response.status}: ${message}`);
  }

  if (response.status === 204) {
    return null as T;
  }

  return response.json() as Promise<T>;
}