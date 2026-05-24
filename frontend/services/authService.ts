import { apiFetch } from "./api";
import type { LoginRequest, LoginResponse } from "@/types/auth";

export async function login(data: LoginRequest): Promise<LoginResponse> {
  return apiFetch<LoginResponse>("/auth/login", {
    method: "POST",
    body: JSON.stringify(data),
  });
}

export function saveSession(data: LoginResponse): void {
  localStorage.setItem("token", data.token);
  localStorage.setItem("user", JSON.stringify(data));
}

export function logout(): void {
  localStorage.removeItem("token");
  localStorage.removeItem("user");
}

export function getToken(): string | null {
  return localStorage.getItem("token");
}

export function getUser(): LoginResponse | null {
  const user = localStorage.getItem("user");
  return user ? (JSON.parse(user) as LoginResponse) : null;
}