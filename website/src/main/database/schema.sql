create table Content (
  id int primary key,
  language varchar(50),
  title varchar(100),
  template_id int
);

create table Field (
  id int primary key,
  content_id int,
  type_id int
);

create table BooleanField (
  id int primary key,
  value boolean
);

create table DateField (
  id int primary key,
  value timestamp
);

create table LongTextField (
  id int primary key,
  value clob
);

create table NumberField (
  id int primary key,
  value decimal
);

create table ParentField (
  id int primary key,
  value int
);

create table TextField (
  id int primary key,
  value varchar(1024)
);

create table Template (
  id int primary key,
  name varchar(50)
);

create table Template_type (
  template_id INT,
  types_id INT
);

create table Type (
  id int primary key,
  name varchar(50),
  value varchar(255),
  dtype varchar(50),
  required boolean
);

create table DateType (
  id int primary key,
  after timestamp,
  before timestamp
);

create table Option (
  id int primary key,
  name varchar(50),
  value varchar(50),
  type_id int
);

-- Basic types, IDs between 1 - 499
insert into Type (id, name, value, dtype, required) values (1, 'Description', 'br.com.depasser.content.field.LongTextField', 'Type', true);

insert into Type (id, name, value, dtype, required) values (2, 'Abstract', 'br.com.depasser.content.field.LongTextField', 'Type', true);

insert into Type (id, name, value, dtype, required) values (3, 'Created', 'br.com.depasser.content.field.DateField', 'DateType', true);
insert into DateType (id, after, before) values (3, null, null);

insert into Type (id, name, value, dtype, required) values (4, 'Created By', 'br.com.depasser.content.field.TextField', 'Type', true);

insert into Type (id, name, value, dtype, required) values (5, 'Updated', 'br.com.depasser.content.field.DateField', 'DateType', true);
insert into DateType (id, after, before) values (5, null, null);

insert into Type (id, name, value, dtype, required) values (6, 'Updated By', 'br.com.depasser.content.field.TextField', 'Type', true);

insert into Type (id, name, value, dtype, required) values (7, 'Parent', 'br.com.depasser.content.field.ParentField', 'Type', true);

insert into Type (id, name, value, dtype, required) values (8, 'Content', 'br.com.depasser.content.field.LongTextField', 'Type', true);

insert into Type (id, name, value, dtype, required) values (9, 'Active', 'br.com.depasser.content.field.BooleanField', 'Type', true);

-- Special types, IDs between 500 - 599
insert into Type (id, name, value, dtype, required) values (500, 'Start', 'br.com.depasser.content.field.DateField', 'DateType', true);
insert into DateType (id, after, before) values (500, null, null);

insert into Type (id, name, value, dtype, required) values (501, 'End', 'br.com.depasser.content.field.DateField', 'DateType', true);
insert into DateType (id, after, before) values (501, null, null);

insert into Type (id, name, value, dtype, required) values (502, 'Course Categories', 'br.com.depasser.content.field.MultiValueField', 'OptionType', true);
insert into Option (id, name, value, type_id) values (1, 'Java', 'Java', 502);
insert into Option (id, name, value, type_id) values (2, 'Web', 'Web', 502);

insert into Type (id, name, value, dtype, required) values (503, 'Tags', 'br.com.depasser.content.field.MultiValueField', 'OptionType', false);

insert into Type (id, name, value, dtype, required) values (504, 'Blog Categories', 'br.com.depasser.content.field.MultiValueField', 'OptionType', true);
insert into Option (id, name, value, type_id) values (3, 'Exemplos', 'Exemplos', 504);
insert into Option (id, name, value, type_id) values (4, 'Eventos', 'Eventos', 504);
insert into Option (id, name, value, type_id) values (5, 'Tutoriais', 'Tutoriais', 504);
insert into Option (id, name, value, type_id) values (6, 'Componentes', 'Componentes', 504);
insert into Option (id, name, value, type_id) values (7, 'Notícias', 'Notícias', 504);

insert into Type (id, name, value, dtype, required) values (505, 'Schedule', 'br.com.depasser.content.field.TextField', 'Type', true);

-- Templates
insert into Template (id, name) values (1, 'Course');
insert into Template_type (template_id, types_id) values (1, 1); -- Description
insert into Template_type (template_id, types_id) values (1, 3); -- Created
insert into Template_type (template_id, types_id) values (1, 4); -- Created By
insert into Template_type (template_id, types_id) values (1, 5); -- Updated
insert into Template_type (template_id, types_id) values (1, 6); -- Updated By
insert into Template_type (template_id, types_id) values (1, 8); -- Content
insert into Template_type (template_id, types_id) values (1, 502); -- Categories

insert into Template (id, name) values (2, 'Class');
insert into Template_type (template_id, types_id) values (2, 7); -- Parent Course
insert into Template_type (template_id, types_id) values (2, 500); -- Start
insert into Template_type (template_id, types_id) values (2, 501); -- End
insert into Template_type (template_id, types_id) values (2, 505); -- Schedule

insert into Template (id, name) values (3, 'Blog Post');
insert into Template_type (template_id, types_id) values (3, 8); -- Content
insert into Template_type (template_id, types_id) values (3, 2); -- Abstract
insert into Template_type (template_id, types_id) values (3, 3); -- Created
insert into Template_type (template_id, types_id) values (3, 4); -- Created By
insert into Template_type (template_id, types_id) values (3, 5); -- Updated
insert into Template_type (template_id, types_id) values (3, 6); -- Updated By
insert into Template_type (template_id, types_id) values (3, 503); -- Tags
insert into Template_type (template_id, types_id) values (3, 504); -- Categories

insert into Template(id, name) values (4, 'Blog Comment');
insert into Template_type (template_id, types_id) values (4, 9); -- Active
insert into Template_type (template_id, types_id) values (4, 7); -- Blog
insert into Template_type (template_id, types_id) values (4, 8); -- Content
insert into Template_type (template_id, types_id) values (4, 3); -- Created
insert into Template_type (template_id, types_id) values (4, 4); -- Created By

-- Create Course
insert into Content (id, language, title, template_id) values (1, 'en_US', 'Programming in Java', 1);

-- Description
insert into Field (id, content_id, type_id) values (1, 1, 1);
insert into LongTextField (id, value) values (1, 'Course Description.');

-- Created
insert into Field (id, content_id, type_id) values (2, 1, 3);
insert into DateField (id, value) values (2, '2020-01-01 10:10:10');