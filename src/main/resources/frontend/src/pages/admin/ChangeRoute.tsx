import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { getCargoById, getPossibleRoutes, setItinerary } from "../../api/Api";
import { CargoLeg, CargoTracking, RouteCandidate } from "../../types/Types";
import classes from "./ChangeRoute.module.css";

//"yyyy-MM-dd hh:mm"
function formatDate(date: Date) {
  return date.toLocaleDateString("sv-SE", {
    year: "numeric",
    month: "numeric",
    day: "numeric",
    hour: "2-digit",
    minute: "2-digit",
  });
}

function RouteLeg({ leg }: { leg: CargoLeg }) {
  return (
    <>
      <div className={classes.voyageNumber}>{leg.voyageNumber}</div>
      <div className={classes.from}>{leg.from}</div>
      <div className={classes.loadTime}>{formatDate(leg.loadTime)}</div>
      <div className={classes.to}>{leg.to}</div>
      <div className={classes.unloadTime}>{formatDate(leg.unloadTime)}</div>
    </>
  );
}

function RouteCandidateItem({
  trackingId,
  legs,
  index,
}: {
  trackingId: string;
  legs: CargoLeg[];
  index: number;
}) {
  const navigate = useNavigate();
  async function assignItineraryHandler() {
    await setItinerary(
      trackingId,
      legs.map((leg) => ({
        ...leg,
        unloadTime: leg.unloadTime.toISOString(),
        loadTime: leg.loadTime.toISOString(),
      }))
    );
    navigate(`/dddsample/admin/cargo/${trackingId}`);
  }

  return (
    <div key={index} className={classes.routeCandidateItem}>
      <h3>
        Route candidate <span>{index}</span>
      </h3>
      <div className={classes.routeGrid}>
        <div className={classes.voyageHeader}>Voyage</div>
        <div className={classes.fromHeader}>From</div>
        <div className={classes.toHeader}>To</div>
        {legs.map((leg: CargoLeg) => (
          <RouteLeg
            key={`${leg.voyageNumber}-${leg.from}-${leg.to}-${index}`}
            leg={leg}
          />
        ))}
      </div>
      <button onClick={assignItineraryHandler}>
        Assign cargo to this route
      </button>
    </div>
  );
}

export default function ChangeRoute() {
  const params = useParams();
  const trackingId = params.trackingId;
  const [routeCandidates, setRouteCandidates] = useState<RouteCandidate[]>();
  const [cargo, setCargo] = useState<CargoTracking>();

  async function loadCargo(trackingId: string) {
    const cargo = await getCargoById(trackingId);
    setCargo(cargo);
  }
  useEffect(() => {
    if (trackingId !== undefined) {
      loadCargo(trackingId);
    }
  }, [trackingId]);

  useEffect(() => {
    async function load() {
      if (trackingId) {
        const routes = await getPossibleRoutes(trackingId);
        setRouteCandidates(routes);
      }
    }
    load();
  }, [trackingId]);

  return (
    <div id="container">
      {cargo !== undefined && (
        <div>
          <h2>Select route</h2>
          <div>
            Cargo <strong>{cargo.trackingId}</strong> is going from{" "}
            <strong>{cargo.origin.unLocode}</strong> to{" "}
            <strong>{cargo.destination.unLocode}</strong>
          </div>
        </div>
      )}
      {routeCandidates !== undefined && (
        <>
          {routeCandidates?.length === 0 && (
            <p>
              No routes found that satisfy the route specification. Try setting
              an arrival deadline futher into the future (a few weeks at least).
            </p>
          )}

          {routeCandidates?.length > 0 &&
            routeCandidates.map((route, index) => (
              <RouteCandidateItem
                key={index}
                trackingId={trackingId ?? ""}
                legs={route.legs}
                index={index}
              />
            ))}
        </>
      )}
    </div>
  );
}
