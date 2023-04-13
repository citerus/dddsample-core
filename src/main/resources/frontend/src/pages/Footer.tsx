import classes from "./Footer.module.css";
export default function Footer() {
  return (
    <div className={classes.footer}>
      This application is written by
      <a href="http://www.citerus.se" target="_blank" rel="noreferrer">
        Citerus
      </a>
      and
      <a href="http://www.domainlanguage.com" target="_blank" rel="noreferrer">
        Domain Language
      </a>
    </div>
  );
}
