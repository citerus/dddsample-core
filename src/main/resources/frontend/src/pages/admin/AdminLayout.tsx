import { Outlet } from "react-router-dom";
import Nav from "./components/Nav";
import classes from "./AdminLayout.module.css";

export default function AdminLayout() {
  return (
    <>
      <h1>Cargo Booking and Routing</h1>
      <Nav />
      <Outlet />
    </>
  );
}
