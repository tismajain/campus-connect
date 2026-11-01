import type { NextConfig } from "next";
const api = process.env.NEXT_PUBLIC_API_BASE_URL || "http://localhost:8080";
const origin = new URL(api).origin;
if (process.env.REQUIRE_HTTPS === "true" && !origin.startsWith("https://")) throw new Error("Production API must use HTTPS");
const config: NextConfig = {
 poweredByHeader: false,
 async headers() {
  return [{ source: "/:path*", headers: [
   { key: "Content-Security-Policy", value: `default-src 'self'; script-src 'self' 'unsafe-inline'${process.env.NODE_ENV === 'development' ? " 'unsafe-eval'" : ''}; style-src 'self' 'unsafe-inline'; img-src 'self' data:; font-src 'self'; connect-src 'self' ${origin}; object-src 'none'; base-uri 'self'; form-action 'self'; frame-ancestors 'none'` },
   { key: "X-Content-Type-Options", value: "nosniff" },
   { key: "X-Frame-Options", value: "DENY" },
   { key: "Referrer-Policy", value: "no-referrer" },
   { key: "Permissions-Policy", value: "camera=(), microphone=(), geolocation=()" },
   ...(process.env.REQUIRE_HTTPS === "true" ? [{ key: "Strict-Transport-Security", value: "max-age=31536000; includeSubDomains" }] : [])
  ] }];
 }
};
export default config;
