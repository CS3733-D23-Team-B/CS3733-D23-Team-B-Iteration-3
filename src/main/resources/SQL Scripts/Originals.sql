DROP TABLE IF EXISTS movebackup;
DROP TABLE IF EXISTS signbackup;
DROP TABLE IF EXISTS locationnamebackup;
DROP TABLE IF EXISTS nodebackup;

CREATE TABLE nodebackup(
                           nodeID INT, xcoord INT, ycoord INT, floor VARCHAR(255), building varchar(255), primary key (nodeID));

CREATE TABLE locationnamebackup(
                                   longName VARCHAR(255), shortName VARCHAR(255), nodetype VARCHAR(255), primary key (longName));

CREATE TABLE movebackup (
                            nodeID INT, longName VARCHAR(255), date VARCHAR(20), primary key (nodeID, longName, date),
                            constraint fk_nodeID foreign key(nodeID) references nodebackup(nodeID),
                            constraint fk_longName foreign key(longName) references locationnamebackup(longName));

INSERT INTO nodebackup SELECT * FROM nodes;
INSERT INTO locationnamebackup SELECT * FROM locationNames;
INSERT INTO movebackup SELECT * FROM moves;

CREATE TABLE signbackup (
                            signagegroup   varchar(255)                                        not null,
                            locationname   text         default ''::text                       not null,
                            direction      varchar(255) default 'stop here'::character varying not null,
                            startdate      date         default CURRENT_DATE                   not null,
                            enddate        date,
                            singleblock    boolean      default true,
                            "signLocation" varchar(255) default 'Info Node 19 Floor 2'::character varying
                                constraint "fk_longName"
                                    references locationnames
                                    on update cascade on delete set default ,
                            constraint signbackup_pkey
                                primary key (signagegroup, locationname, startdate)
);

INSERT INTO signbackup SELECT * FROM signs;