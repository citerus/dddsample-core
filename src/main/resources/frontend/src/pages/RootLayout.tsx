import { Outlet } from "react-router-dom";
import classes from "./RootLayout.module.css";
import Footer from "./Footer";
import Logo from "./Logo";

export default function RootLayout() {
  return (
    <main className={classes.main}>
      <Logo />
      <hr />
      <div className={classes.pageContent}>
        <Outlet />
      </div>
      <hr />
      <Footer />
    </main>
  );
}
