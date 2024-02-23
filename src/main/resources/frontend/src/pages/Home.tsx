import { FormEvent, useRef } from "react";
import { Link, Outlet, useNavigate } from "react-router-dom";
import classes from "./Home.module.css";

export default function Home() {
  const navigate = useNavigate();
  const trackingIdInputRef = useRef<HTMLInputElement>(null);

  function loadCargoClickHandler(e: FormEvent<HTMLFormElement>) {
    e.preventDefault();
    if (trackingIdInputRef.current) {
      const trackingId = trackingIdInputRef.current.value;
      if (trackingId === "") {
        navigate(`/dddsample/`);
      } else {
        navigate(`/dddsample/cargo/${trackingId}`);
      }
    }
  }

  return (
    <>
      <h1>Track cargo</h1>
      <div className={classes.trackingIdInput}>
        <label htmlFor="trackId">Enter your tracking id </label>
        <form onSubmit={loadCargoClickHandler}>
          <input id="trackId" type="text" ref={trackingIdInputRef} />
          <button className={classes.buttonLink} type="submit">
            Track!
          </button>
        </form>
        <div>Hint: try tracking &quot;ABC123&quot; or &quot;JKL567&quot;.</div>
      </div>
      <Outlet />
      <Link to={`/dddsample/admin/cargo`}>Administrate cargo</Link>
    </>
  );
}
