/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     2025/3/4 11:36:06                            */
/*==============================================================*/


drop table if exists communication;

drop table if exists communication_file;

drop table if exists membership;

drop table if exists `order`;

drop table if exists service;

drop table if exists setting;

drop table if exists user;

/*==============================================================*/
/* Table: communication                                         */
/*==============================================================*/
create table communication
(
   id                   int not null,
   server_id            int not null,
   send_user_id         int not null,
   receive_user_id      int,
   content              text,
   cumulative_number    int not null,
   time                 datetime not null,
   primary key (id)
);

/*==============================================================*/
/* Table: communication_file                                    */
/*==============================================================*/
create table communication_file
(
   id                   int not null,
   communication_id     int not null,
   file_url             text not null,
   primary key (id)
);

/*==============================================================*/
/* Table: membership                                            */
/*==============================================================*/
create table membership
(
   membership_id        int not null,
   user_id              int not null,
   buy_date             datetime not null,
   deadline             datetime not null,
   grade                int not null,
   order_id             int not null,
   primary key (membership_id)
);

/*==============================================================*/
/* Table: `order`                                               */
/*==============================================================*/
create table `order`
(
   id                   int not null,
   buyer_id             int not null,
   type                 int not null,
   payment_type         int,
   payment_amount       float,
   payee_id             int,
   draw_proportion      float,
   received_amount      float,
   status               int not null,
   create_at            datetime not null,
   end_at               datetime,
   delete_flag          int,
   delete_at            datetime,
   delete_by_id         int,
   primary key (id)
);

/*==============================================================*/
/* Table: service                                               */
/*==============================================================*/
create table service
(
   id                   int not null,
   order_id             int not null,
   apply_user_id        int not null,
   accept_expert_id     int,
   frequency            int not null,
   create_at            datetime not null,
   end_at               datetime,
   primary key (id)
);

/*==============================================================*/
/* Table: setting                                               */
/*==============================================================*/
create table setting
(
   id                   int not null,
   name                 text not null,
   content              text not null,
   parent_id            int,
   last_chaget_at       datetime not null,
   last_change_id       int not null,
   primary key (id)
);

/*==============================================================*/
/* Table: user                                                  */
/*==============================================================*/
create table user
(
   id                   int not null,
   name                 varchar(50) not null,
   phone                varchar(25) not null,
   description          text,
   password             varchar(255) not null,
   role                 int not null,
   status               int not null,
   balance              float not null,
   create_at            datetime not null,
   last_login_time      datetime not null,
   updater_id           int,
   update_at            datetime,
   delete_flag          int not null,
   delete_at            datetime,
   deleter_id           int,
   primary key (id)
);

alter table communication add constraint FK_receive_user_id foreign key (receive_user_id)
      references user (id) on delete restrict on update restrict;

alter table communication add constraint FK_send_user_id foreign key (send_user_id)
      references user (id) on delete restrict on update restrict;

alter table communication add constraint FK_server_id foreign key (server_id)
      references service (id) on delete restrict on update restrict;

alter table communication_file add constraint FK_communication_id foreign key (id)
      references communication (id) on delete restrict on update restrict;

alter table membership add constraint FK_member_order foreign key (order_id)
      references `order` (id) on delete restrict on update restrict;

alter table membership add constraint FK_member_user foreign key (user_id)
      references user (id) on delete restrict on update restrict;

alter table `order` add constraint FK_buyer_id foreign key (buyer_id)
      references user (id) on delete restrict on update restrict;

alter table `order` add constraint FK_delete_by_id foreign key (delete_by_id)
      references user (id) on delete restrict on update restrict;

alter table `order` add constraint FK_payee_id foreign key (id)
      references user (id) on delete restrict on update restrict;

alter table service add constraint FK_order_id foreign key (order_id)
      references `order` (id) on delete restrict on update restrict;

alter table setting add constraint FK_change_userid foreign key (id)
      references user (id) on delete restrict on update restrict;

