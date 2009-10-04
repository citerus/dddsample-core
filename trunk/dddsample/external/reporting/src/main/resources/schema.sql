create table cargo (
  cargo_tracking_id varchar not null primary key,
  received_in varchar,
  destination varchar,
  arrival_deadline timestamp,
  eta timestamp,
  current_status varchar,
  current_voyage_number varchar,
  current_location varchar,
  last_updated_on timestamp
)

create table handling (
  cargo_tracking_id varchar not null,
  type varchar,
  location varchar,
  voyage_number varchar,
  completed_on timestamp,
  foreign key (cargo_tracking_id) references cargo(cargo_tracking_id)
)

create table voyage (
  voyage_number varchar not null primary key,
  next_stop varchar,
  eta_next_stop timestamp,
  current_status varchar not null,
  delayed_by_min int not null,
  last_updated_on timestamp
)