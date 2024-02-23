import React from "react";
import ReactDOM from "react-dom/client";
import App from "./App";

const elm = document.querySelector("#root");
if (elm) {
  const root = ReactDOM.createRoot(elm);
  root.render(
    <React.StrictMode>
      <App />
    </React.StrictMode>
  );
}
