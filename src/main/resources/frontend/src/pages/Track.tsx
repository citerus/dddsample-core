import errorImg from "../assets/error.png";
import tickImg from "../assets/tick.png";
import crossImg from "../assets/cross.png";
import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { ApiError, CargoTracking } from "../types/Types";
import { getCargoById } from "../api/Api";
import ErrorDialog from "../components/ErrorDialog";

function formatDate(date: Date) {
  return date.toLocaleDateString("sv-SE", {
    year: "numeric",
    month: "numeric",
    day: "numeric",
    hour: "2-digit",
    minute: "2-digit",
  });
}

export default function Track() {
  const navigate = useNavigate();
  const [cargo, setCargo] = useState<CargoTracking>();
  const [error, setError] = useState<{ error: string; message: string }>();
  const params = useParams();
  const trackingId = params.trackingId;

  function onErrorCloseHandler() {
    navigate("/dddsample/");
  }

  useEffect(() => {
    async function load() {
      if (trackingId !== undefined) {
        try {
          const cargo = await getCargoById(trackingId);
          setCargo(cargo);
        } catch (e: unknown) {
          const error = e as ApiError;
          setError(error);
        }
      }
    }
    load();
  }, [trackingId]);
  return (
    <>
      {error !== undefined && (
        <ErrorDialog error={error} onClose={onErrorCloseHandler} />
      )}
      {cargo !== undefined && (
        <div id="result">
          <h2>Status for cargo {cargo.trackingId}</h2>
          <div>{cargo?.statusText}</div>

          <p>
            {`Estimated time of arrival in ${cargo.destination.name}: ${
              cargo.eta !== undefined ? formatDate(cargo.eta) : "Unknown"
            }`}
          </p>

          <p>{cargo.nextExpectedActivity}</p>

          {cargo.isMisdirected && (
            <p className="notify">
              <img src={errorImg} alt="error" />
              Cargo is misdirected
            </p>
          )}

          {cargo.handlingEvents.length > 0 && (
            <>
              <h3>Handling History</h3>
              <ul style={{ listStyleType: "none" }}>
                {cargo.handlingEvents.map((event, index) => (
                  <li key={index}>
                    <p>
                      {event.isExpected && <img src={tickImg} alt="expected" />}
                      {!event.isExpected && (
                        <img src={crossImg} alt="not expected" />
                      )}
                      &nbsp;
                      {event.description}
                    </p>
                  </li>
                ))}
              </ul>
            </>
          )}
        </div>
      )}
    </>
  );
}
