CREATE TABLE basket(
  date      VARCHAR(30),
	uuid    	VARCHAR(100),
  market    VARCHAR(20),
  state     VARCHAR(10),
  boll      DOUBLE,
  mfi       DOUBLE,
	price     DOUBLE,
  volume    DOUBLE
)
ENGINE=InnoDB;

CREATE TABLE history(
  date      VARCHAR(30),
  market    VARCHAR(20),
  volume    DOUBLE,
  bid_boll  DOUBLE,
  bid_mfi   DOUBLE,
  bid_price DOUBLE,
  ask_boll  DOUBLE,
  ask_mfi   DOUBLE,
  ask_price DOUBLE,
  pnl       DOUBLE
)
ENGINE=InnoDB;