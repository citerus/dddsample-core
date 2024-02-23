import { useEffect, useState } from "react";
import { Unlocode } from "../types/Types";

let loadedCodes: Unlocode[] = [];
export default function useUnlocodes() {
  const [unlocodes, setUnlocodes] = useState<Unlocode[]>(loadedCodes);
  useEffect(() => {
    async function load() {
      const response = await fetch(`/dddsample/api/unlocodes`);
      const unlocodes = await response.json();
      loadedCodes = unlocodes;
      setUnlocodes(unlocodes);
    }
    if (loadedCodes.length === 0) {
      load();
    }
  }, []);
  return unlocodes;
}
