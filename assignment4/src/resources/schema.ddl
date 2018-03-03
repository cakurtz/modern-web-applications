create database eavesdrop_projects;

use eavesdrop_projects;

create table projects(name varchar(255) NOT NULL, description varchar(255), project_id int NOT NULL AUTO_INCREMENT, PRIMARY KEY(project_id));