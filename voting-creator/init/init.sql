create table creator(creator_id varchar(10) constraint creatorpk primary key,creator_name varchar(100) not null,creator_info varchar(500));
create table event(event_id varchar(10) constraint eventpk primary key, event_name varchar(100) not null,event_info varchar(500),creator_id varchar(10) not null, constraint eventfk1 foreign key(creator_id) references creator(creator_id));
create table candidate(candidate_id varchar(10), candidate_name varchar(100) not null, candidate_info varchar(500),primary key(candidate_id));
create table nomination(candidate_id varchar(10),event_id varchar(10),constraint nominationfk1 foreign key(candidate_id) references candidate(candidate_id),constraint nominationfk2 foreign key(event_id) references event(event_id));
create sequence creator_creator_id_seq owned by creator.creator_id;
alter table creator alter column creator_id set default nextval('creator_creator_id_seq');
create sequence event_event_id_seq owned by event.event_id;
alter table event alter column event_id set default nextval('event_event_id_seq');
alter table event add column "locked" boolean not null default false;
create sequence candidate_candidate_id_seq owned by candidate.candidate_id;
alter table candidate alter column candidate_id set default nextval('candidate_candidate_id_seq');

create table voter(voter_id varchar(10), voter_name varchar(100) not null, event_id varchar(10), voted boolean default false,primary key(voter_id), constraint voterfk1 foreign key(event_id) references event(event_id));
create sequence voter_voter_id_seq owned by voter.voter_id;
alter table voter alter column voter_id set default nextval('voter_voter_id_seq');
create table result(event_id varchar(10), candidate_id varchar(10), count integer default 0 check (count >= 0), constraint resultfk1 foreign key(event_id) references event(event_id), constraint resultfk2 foreign key(candidate_id) references candidate(candidate_id));
