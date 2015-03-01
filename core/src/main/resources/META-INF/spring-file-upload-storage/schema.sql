create table sfus_file (
	id varchar(255) not null,
	name varchar(255) not null,
	original_filename varchar(255),
	content_type varchar(255),
	size int not null,
	data blob,
	context varchar(255),
	created_at bigint not null,
	expires_at bigint not null
);
