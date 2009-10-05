insert into cargo (
  cargo_tracking_id,
  received_in,
  destination,
  arrival_deadline,
  eta,
  current_status,
  current_voyage_number,
  current_location,
  last_updated_on
) values (
  'ABC',
  'Hongkong',
  'Stockholm',
  '2009-06-15 12:00:00.000',
  '2009-06-12 18:30:00.000',
  'Onboard voyage',
  'V0100',
  'Tokyo',
  '2009-06-08 14:23:12.123'
);

insert into handling (cargo_tracking_id, type,location,voyage_number,completed_on)
values ('ABC','Receive','Hongkong',  null,   '2009-05-30 07:32:00.000');

insert into handling (cargo_tracking_id, type,location,voyage_number,completed_on)
values ('ABC','Load',   'Hongkong',  'V0100','2009-05-31 13:37:00.000');

insert into handling (cargo_tracking_id, type,location,voyage_number,completed_on)
values ('ABC','Unload', 'Long Beach','V0100','2009-06-04 19:01:00.000');

insert into handling (cargo_tracking_id, type,location,voyage_number,completed_on)
values ('ABC','Load',   'Long Beach','V0200','2009-06-06 22:50:00.000');

insert into voyage (
  voyage_number,
  next_stop,
  eta_next_stop,
  current_status,
  delayed_by_min,
  last_updated_on
) values (
  'V0100',
  'Honolulu',
  '2009-06-10 04:25:00.000',
  'In port',
  '1400',
  '2009-06-06 14:01:22.331'
);

insert into voyage (
  voyage_number,
  next_stop,
  eta_next_stop,
  current_status,
  delayed_by_min,
  last_updated_on
) values (
  'V0200',
  'Seattle',
  '2009-06-07 12:45:00.000',
  'In transit',
  '0',
  '2009-06-06 14:01:19.901'
);
