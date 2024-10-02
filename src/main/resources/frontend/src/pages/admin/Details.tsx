import { ChangeEvent, useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";
import classes from "./Details.module.css";
import { CargoTracking, CargoLeg, ApiError } from "../../types/Types";
import { changeCargoDestination, getCargoById } from "../../api/Api";
import useUnlocodes from "../../hooks/useUnlocodes";
import ErrorDialog from "../../components/ErrorDialog";

function formatDate(date: Date) {
  return date.toLocaleDateString("sv-SE", {
    year: "numeric",
    month: "numeric",
    day: "numeric",
    hour: "2-digit",
    minute: "2-digit",
  });
}

function Leg(props: { leg: CargoLeg }) {
  const leg = props.leg;
  return (
    <>
      <div className={classes.voyageNumber}>{leg.voyageNumber}</div>
      <div className={classes.loadId}>{leg.from}</div>
      <div className={classes.loadDate}>{formatDate(leg.loadTime)}</div>
      <div className={classes.unloadId}>{leg.to}</div>
      <div className={classes.unloadDate}>{formatDate(leg.unloadTime)}</div>
    </>
  );
}

export default function AdminDetails() {
  const params = useParams();
  const trackingId = params.trackingId;

  const [cargo, setCargo] = useState<CargoTracking>();
  const [error, setError] = useState<ApiError>();
  const unlocodes = useUnlocodes();

  async function loadCargo(trackingId: string) {
    const cargo = await getCargoById(trackingId);
    setCargo(cargo);
  }
  useEffect(() => {
    if (trackingId !== undefined) {
      loadCargo(trackingId);
    }
  }, [trackingId]);

  async function changeDestinationHandler(e: ChangeEvent<HTMLSelectElement>) {
    const newDestination = e.target.value;
    if (trackingId) {
      try {
        await changeCargoDestination(trackingId, newDestination);
        loadCargo(trackingId);
      } catch (e: unknown) {
        setError(e as ApiError);
      }
    }
  }

  return (
    <>
      {error !== undefined && (
        <ErrorDialog error={error} onClose={() => setError(undefined)} />
      )}
      {cargo !== undefined && (
        <>
          <h2>Details for cargo {cargo.trackingId}</h2>
          <div className={classes.cargoDetails}>
            <div className={classes.label}>Origin:</div>
            <div>
              {cargo.origin.name} / {cargo.origin.unLocode}
            </div>
            <div className={classes.label}>Destination:</div>
            <div>
              <select
                name="destination"
                onChange={changeDestinationHandler}
                value={cargo.destination.unLocode}
              >
                {unlocodes.map((value, index) => (
                  <option
                    key={`${value.unLocode}-${index}`}
                    value={value.unLocode}
                  >
                    {value.name} / {value.unLocode}
                  </option>
                ))}
              </select>
            </div>
            <div className={classes.label}>Arrival deadline:</div>
            {cargo.eta && <div>{formatDate(cargo.eta)}</div>}
            {!cargo.eta && <div>Unknown</div>}
          </div>
          {cargo.isMisrouted && (
            <div>
              Cargo is misrouted -{" "}
              <Link to={`/dddsample/admin/cargo/${trackingId}/route`}>
                reroute this cargo
              </Link>
            </div>
          )}
          <h2>Itinerary</h2>
          {cargo.itinerary.length !== 0 && (
            <div className={classes.itineraryTable}>
              <div className={classes.tableHeader}>Voyage number</div>
              <div className={`${classes.tableHeader} ${classes.twoColumns}`}>
                Load
              </div>
              <div className={`${classes.tableHeader} ${classes.twoColumns}`}>
                Unload
              </div>
              {cargo.itinerary.map((leg: CargoLeg, index) => (
                <Leg key={`${leg.voyageNumber}-${index}`} leg={leg} />
              ))}
            </div>
          )}
          {cargo.itinerary.length === 0 && (
            <div>
              <strong>Not routed</strong>
              <span style={{ marginInline: "4px" }}>-</span>
              <Link to={`/dddsample/admin/cargo/${trackingId}/route`}>
                Route this cargo
              </Link>
            </div>
          )}
        </>
      )}
    </>
  );
}
