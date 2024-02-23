import { ChangeEvent, FormEvent, useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { createNewCargo } from "../../api/Api";
import useUnlocodes from "../../hooks/useUnlocodes";
import { Unlocode } from "../../types/Types";
import classes from "./Registration.module.css";

function SelectLocation(props: {
  values: Unlocode[];
  label: string;
  id: string;
  onChange: (value: string) => void;
}) {
  function changeHandler(e: ChangeEvent<HTMLSelectElement>) {
    props.onChange(e.target.value);
  }

  return (
    <div>
      <label htmlFor={`${props.id}`}>{props.label}</label>
      <select name={props.id} onChange={changeHandler}>
        <option value=""></option>
        {props.values.map((value) => (
          <option key={value.unLocode} value={value.unLocode}>
            {value.name} / {value.unLocode}
          </option>
        ))}
      </select>
    </div>
  );
}

export default function Registration() {
  const navigate = useNavigate();

  const unlocodes = useUnlocodes();
  const [origin, setOrigin] = useState("");
  const [destination, setDestination] = useState("");
  const [arrivalDeadline, setArrivalDeadline] = useState(new Date());

  function originChangeHandler(value: string) {
    setOrigin(value);
  }
  function destinationChangeHandler(value: string) {
    setDestination(value);
  }
  function arrivalDeadlineChangeHandler(event: ChangeEvent<HTMLInputElement>) {
    setArrivalDeadline(new Date(event.target.value));
  }

  async function onSubmitHandler(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    const trackingId = await createNewCargo({
      origin,
      destination,
      arrivalDeadline: arrivalDeadline.toISOString(),
    });
    navigate(`/dddsample/admin/cargo/${trackingId}`);
  }

  return (
    <>
      <h2>Book new cargo</h2>
      <form className={classes.registrationForm} onSubmit={onSubmitHandler}>
        <SelectLocation
          values={unlocodes}
          id="originUnlocode"
          label="Origin"
          onChange={originChangeHandler}
        />
        <SelectLocation
          values={unlocodes}
          id="destinationUnlocode"
          label="Destination"
          onChange={destinationChangeHandler}
        />
        <div>
          <label htmlFor="arrivalDeadlineInput">Arrival deadline</label>
          <input
            type="date"
            name="arrivalDeadline"
            onChange={arrivalDeadlineChangeHandler}
          />
        </div>
        <button type="submit">Book</button>
      </form>
    </>
  );
}
