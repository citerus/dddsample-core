import classes from "./Error.module.css";
export default function Error() {
  return (
    <div className={classes.errorPage}>
      <h1>Error</h1>
      <h2>
        Either something went wrong, or the page you were trying to go to does
        not exist.
      </h2>
    </div>
  );
}
