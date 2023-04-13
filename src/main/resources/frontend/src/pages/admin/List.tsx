import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { getAllCargo } from "../../api/Api";
import { CargoTracking } from "../../types/Types";
import classes from "./List.module.css";

function ListItem({ cargo }: { cargo: CargoTracking }) {
  let routed = cargo.handlingEvents.length !== 0 ? "Yes" : "No";
  if (cargo.isMisdirected) {
    routed = "Misrouted";
  }
  return (
    <>
      <Link to={`/dddsample/admin/cargo/${cargo.trackingId}`}>
        {cargo.trackingId}
      </Link>
      <div>{cargo.origin.name}</div>
      <div>{cargo.destination.name}</div>
      <div>{routed}</div>
    </>
  );
}

export default function List() {
  const [cargoList, setCargoList] = useState<CargoTracking[]>();
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    async function load() {
      const cargoList = await getAllCargo();
      setCargoList(cargoList);
      setLoading(false);
    }
    setLoading(true);
    load();
  }, []);
  return (
    <>
      <h2>Admin list</h2>
      {!loading && (
        <div className={classes.cargoTable}>
          <div>Tracking ID</div>
          <div>Origin</div>
          <div>Destination</div>
          <div>Routed</div>
          <hr />
          {cargoList?.map((cargo) => (
            <ListItem key={cargo.trackingId} cargo={cargo} />
          ))}
        </div>
      )}
      <Link
        className={classes.registerLink}
        to={`/dddsample/admin/registration`}
      >
        Register new cargo
      </Link>
    </>
  );
}
