/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     2025/1/14 星期二 15:44:15                       */
/*==============================================================*/


drop table if exists File;

drop table if exists InviteSpecialisRrecord;

drop table if exists ProjectDeclare;

drop table if exists ProjectDeclareField;

drop table if exists ProjectDeclareFieldValueAssociation;

drop table if exists ProjectOptimizeRrecord;

drop table if exists SystemSetting;

drop table if exists Template;

drop table if exists TemplateFields;

drop table if exists User;

drop table if exists form_fields;

drop table if exists forms;

/*==============================================================*/
/* Table: File                                                  */
/*==============================================================*/
create table File
(
   file_id              int not null auto_increment,
   file_size            float not null,
   file_type            varchar(20) not null,
   file_function        varchar(20) not null,
   upload_status        int not null,
   file                 longblob not null,
   file_create_time     datetime not null,
   last_accessed_times  datetime,
   primary key (file_id)
);

/*==============================================================*/
/* Table: InviteSpecialisRrecord                                */
/*==============================================================*/
create table InviteSpecialisRrecord
(
   inviteSpecialisRrecord_id int not null,
   invite_user_id       int not null,
   invite_create_at     datetime not null,
   invite_deadline      datetime not null,
   invite_isAgree       boolean,
   Specialis_user_id    int,
   invite_refuseReason  varchar(255),
   primary key (inviteSpecialisRrecord_id)
);

/*==============================================================*/
/* Table: ProjectDeclare                                        */
/*==============================================================*/
create table ProjectDeclare
(
   projectDeclare_id    int not null auto_increment,
   template_id          int not null,
   projectDeclare_user  int not null,
   projectDeclare_create_at datetime not null,
   projectDeclare_draftEnable boolean,
   primary key (projectDeclare_id)
);

/*==============================================================*/
/* Table: ProjectDeclareField                                   */
/*==============================================================*/
create table ProjectDeclareField
(
   projectDeclareField_id int not null auto_increment,
   templateFields_id    int not null,
   projectDeclareField_value text,
   primary key (projectDeclareField_id)
);

/*==============================================================*/
/* Table: ProjectDeclareFieldValueAssociation                   */
/*==============================================================*/
create table ProjectDeclareFieldValueAssociation
(
   projectDeclareFieldValueAssociation_id int not null auto_increment,
   projectDeclare_id    int not null,
   projectDeclareField_id int not null,
   optimize_enable      boolean not null,
   optimize_frequency   int,
   projectOptimizeRrecord_id int,
   primary key (projectDeclareFieldValueAssociation_id)
);

alter table ProjectDeclareFieldValueAssociation comment '如果查询原始版本，只需查询优化次数为0的记录
如果想要查询第一优化版本，只需要查询优化次数为1的版本。';

/*==============================================================*/
/* Table: ProjectOptimizeRrecord                                */
/*==============================================================*/
create table ProjectOptimizeRrecord
(
   projectOptimizeRrecord_id int not null auto_increment,
   projectDeclare_id    int not null,
   specialist_user_id   int not null,
   projectOptimize_create_at datetime not null,
   primary key (projectOptimizeRrecord_id)
);

/*==============================================================*/
/* Table: SystemSetting                                         */
/*==============================================================*/
create table SystemSetting
(
   systemSetting_id     int not null auto_increment,
   systemSetting_item   varchar(255) not null,
   systemSetting_value  varchar(255) not null,
   systemSetting_lastUpdate datetime not null,
   systemSetting_lastUpdate_user_id int,
   primary key (systemSetting_id)
);

/*==============================================================*/
/* Table: Template                                              */
/*==============================================================*/
create table Template
(
   template_id          int not null auto_increment,
   template_name        varchar(50) not null,
   template_description varchar(255),
   template_enable      boolean not null,
   template_type        int not null,
   templateFile_id      int,
   template_create_user int not null,
   template_create_at   datetime not null,
   templateArchive_enable boolean not null,
   templateArchive_create_at datetime,
   primary key (template_id)
);

/*==============================================================*/
/* Table: TemplateFields                                        */
/*==============================================================*/
create table TemplateFields
(
   templateFields_id    int not null auto_increment,
   template_id          int,
   templateFields_name  varchar(50) not null,
   templateFields_type  int not null,
   templateFields_isRequired boolean not null,
   templateFields_options text,
   primary key (templateFields_id)
);

/*==============================================================*/
/* Table: User                                                  */
/*==============================================================*/
create table User
(
   user_id              int not null auto_increment,
   user_name            varchar(50) not null,
   user_phoneNumber     varchar(25) not null,
   user_hashPassword    varchar(255) not null,
   user_role            int not null,
   user_accountStatus   int not null,
   user_createDate      datetime not null,
   user_lastUpdated     datetime not null,
   primary key (user_id)
);

/*==============================================================*/
/* Table: form_fields                                           */
/*==============================================================*/
create table form_fields
(
   form_fields_id       int not null auto_increment,
   forms_id             int,
   form_id              int not null,
   form_fields_name     varchar(50) not null,
   form_fields_type     int not null,
   isRequired           boolean not null,
   form_fields_options  text,
   primary key (form_fields_id)
);

/*==============================================================*/
/* Table: forms                                                 */
/*==============================================================*/
create table forms
(
   forms_id             int not null auto_increment,
   form_name            varchar(50) not null,
   form_description     varchar(255),
   primary key (forms_id)
);

alter table InviteSpecialisRrecord add constraint FK_Reference_14 foreign key (invite_user_id)
      references User (user_id) on delete restrict on update restrict;

alter table InviteSpecialisRrecord add constraint FK_Reference_15 foreign key (Specialis_user_id)
      references User (user_id) on delete restrict on update restrict;

alter table ProjectDeclare add constraint FK_Reference_3 foreign key (template_id)
      references Template (template_id) on delete restrict on update restrict;

alter table ProjectDeclare add constraint FK_Reference_4 foreign key (projectDeclare_user)
      references User (user_id) on delete restrict on update restrict;

alter table ProjectDeclareField add constraint FK_Reference_6 foreign key (templateFields_id)
      references TemplateFields (templateFields_id) on delete restrict on update restrict;

alter table ProjectDeclareFieldValueAssociation add constraint FK_Reference_10 foreign key (projectDeclare_id)
      references ProjectDeclare (projectDeclare_id) on delete restrict on update restrict;

alter table ProjectDeclareFieldValueAssociation add constraint FK_Reference_11 foreign key (projectOptimizeRrecord_id)
      references ProjectOptimizeRrecord (projectOptimizeRrecord_id) on delete restrict on update restrict;

alter table ProjectDeclareFieldValueAssociation add constraint FK_Reference_9 foreign key (projectDeclareField_id)
      references ProjectDeclareField (projectDeclareField_id) on delete restrict on update restrict;

alter table ProjectOptimizeRrecord add constraint FK_Reference_12 foreign key (projectDeclare_id)
      references ProjectDeclare (projectDeclare_id) on delete restrict on update restrict;

alter table ProjectOptimizeRrecord add constraint FK_Reference_13 foreign key (specialist_user_id)
      references User (user_id) on delete restrict on update restrict;

alter table SystemSetting add constraint FK_Reference_16 foreign key (systemSetting_lastUpdate_user_id)
      references User (user_id) on delete restrict on update restrict;

alter table Template add constraint FK_Reference_7 foreign key (template_create_user)
      references User (user_id) on delete restrict on update restrict;

alter table Template add constraint FK_Reference_8 foreign key (templateFile_id)
      references File (file_id) on delete restrict on update restrict;

alter table TemplateFields add constraint FK_Reference_2 foreign key (template_id)
      references Template (template_id) on delete restrict on update restrict;

alter table form_fields add constraint FK_Reference_1 foreign key (forms_id)
      references forms (forms_id) on delete restrict on update restrict;

