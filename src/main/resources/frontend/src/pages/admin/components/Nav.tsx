import classes from "./Nav.module.css";
import { NavLink } from "react-router-dom";
export default function Nav() {
  return (
    <nav className={classes.navbar}>
      <NavLink
        to="/dddsample/admin/cargo"
        className={({ isActive }) => (isActive ? classes.active : "")}
        end
      >
        List all cargos
      </NavLink>
      <NavLink
        to="/dddsample/admin/registration"
        className={({ isActive }) => (isActive ? classes.active : "")}
        end
      >
        Book new cargo
      </NavLink>
    </nav>
  );
}
