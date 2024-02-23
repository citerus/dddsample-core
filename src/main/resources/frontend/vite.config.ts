import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    proxy: {
      "/dddsample/api/": {
        target: "http://localhost:8080/",
      },
    },
  },
  build: {
    outDir: "../static",
    assetsDir: "assets",
    emptyOutDir: true,
  },
  base: "/dddsample",
});
