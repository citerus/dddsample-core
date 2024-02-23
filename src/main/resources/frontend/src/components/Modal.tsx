import { Fragment, ReactNode } from "react";
import ReactDOM from "react-dom";
import classes from "./Modal.module.css";

function ModalOverlay(props: { children: ReactNode }) {
  return (
    <div className={classes.modal}>
      <div className={classes.content}>{props.children}</div>
    </div>
  );
}

function Backdrop(props: { onClose: () => void }) {
  return <div className={classes.backdrop} onClick={props.onClose}></div>;
}

export default function Modal(props: {
  onClose: () => void;
  children: ReactNode;
}) {
  const portalElement = document.querySelector("#overlays") as Element;
  return (
    <Fragment>
      {ReactDOM.createPortal(
        <Backdrop onClose={props.onClose} />,
        portalElement
      )}
      {ReactDOM.createPortal(
        <ModalOverlay>{props.children}</ModalOverlay>,
        portalElement
      )}
    </Fragment>
  );
}
