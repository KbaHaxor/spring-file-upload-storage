create table sfus_file (
	id varchar(255) not null,
	name varchar(255) not null,
	original_filename varchar(255),
	content_type varchar(255),
	size int not null,
	data blob,
	username varchar(255),
	created_at timestamp not null,
	expires_at timestamp not null
);
