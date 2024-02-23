import { Link } from "react-router-dom";
import classes from "./Logo.module.css";
export default function Logo() {
  return (
    <header className={classes.logo}>
      <Link to="/dddsample/">
        <div className={classes.bigtext}>Domain Driven Delivery</div>
        <div className={classes.smalltext}>We&apos;re ubiquitous!</div>
      </Link>
    </header>
  );
}
