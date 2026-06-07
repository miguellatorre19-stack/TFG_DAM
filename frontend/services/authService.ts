import { apiFetch } from "./api";
import type { LoginRequest, LoginResponse, MeResponse } from "@/types/auth";

export const PRIVATE_AREA_ROLES = ["SOCIO", "PARTICIPANTE"] as const;
export const ADMIN_PANEL_ROLES = ["ADMIN", "ADMINISTRATIVA", "TRABAJADOR"] as const;

export async function login(data: LoginRequest): Promise<LoginResponse> {
  return apiFetch<LoginResponse>("/auth/login", {
    method: "POST",
    body: JSON.stringify(data),
    auth: false,
  });
}

export function saveSession(data: LoginResponse): void {
  if (typeof window === "undefined") {
    return;
  }

  localStorage.setItem("token", data.token);
  localStorage.setItem("user", JSON.stringify(data));
}

export function logout(): void {
  if (typeof window === "undefined") {
    return;
  }

  localStorage.removeItem("token");
  localStorage.removeItem("user");
}

export function getToken(): string | null {
  if (typeof window === "undefined") {
    return null;
  }

  return localStorage.getItem("token");
}

export function getUser(): LoginResponse | null {
  if (typeof window === "undefined") {
    return null;
  }

  const user = localStorage.getItem("user");
  return user ? (JSON.parse(user) as LoginResponse) : null;
}

export function hasAnyRole(
  user: Pick<LoginResponse, "roles"> | null | undefined,
  allowedRoles: readonly string[]
): boolean {
  return !!user && user.roles.some((role) => allowedRoles.includes(role));
}

export function canAccessPrivateArea(
  user: Pick<LoginResponse, "roles"> | null | undefined
): boolean {
  return hasAnyRole(user, PRIVATE_AREA_ROLES);
}

export function canAccessAdminPanel(
  user: Pick<LoginResponse, "roles"> | null | undefined
): boolean {
  return hasAnyRole(user, ADMIN_PANEL_ROLES);
}

export async function getMe(): Promise<MeResponse> {
  return apiFetch<MeResponse>("/me");
}
