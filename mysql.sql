create table if not exists user
(
    user_id     int auto_increment
        primary key,
    created_at  datetime(6)  not null,
    modified_at datetime(6)  not null,
    nickname    varchar(100) not null,
    passwd      varchar(100) not null,
    username    varchar(24)  not null
);

create table if not exists note
(
    note_id           int auto_increment
        primary key,
    created_at        datetime(6) not null,
    latest_version_id int         null,
    owner_user_id     int         not null,
    constraint FKlukm6691qldoe402oc011pd3q
        foreign key (owner_user_id) references user (user_id)
);

create table if not exists note_invite
(
    invite_id  varchar(36) not null
        primary key,
    note_id    int         not null,
    readonly   bit         not null,
    created_at datetime(6) not null,
    expires_at datetime(6) not null,
    constraint FK7d5ppti2wgxcowlwt2m45ajoy
        foreign key (note_id) references note (note_id)
);

create table if not exists note_permission
(
    note_id  int not null,
    user_id  int not null,
    readonly bit not null,
    primary key (note_id, user_id),
    constraint FK59i27s2xcum7sd9si0ks7538k
        foreign key (note_id) references note (note_id),
    constraint FKgm9dcnl7ep4k4p6wxt5opd4rp
        foreign key (user_id) references user (user_id)
);

create table if not exists note_version
(
    note_version_id int auto_increment
        primary key,
    content         longtext     not null,
    created_at      datetime(6)  not null,
    title           varchar(100) not null,
    version         int          not null,
    creator_user_id int          not null,
    note_id         int          not null,
    constraint FK4p85rnjie8livxiifdpyd2wlj
        foreign key (note_id) references note (note_id),
    constraint FKgaoufay892gm6n69rshonwtb0
        foreign key (creator_user_id) references user (user_id)
);

alter table note
    add constraint FKx9u7nv3tcdryjeq2qatcll8o
        foreign key (latest_version_id) references note_version (note_version_id);

create table if not exists refresh_token
(
    token   varchar(255) not null
        primary key,
    user_id int          not null,
    constraint FKfgk1klcib7i15utalmcqo7krt
        foreign key (user_id) references user (user_id)
);

create table if not exists tag
(
    tag_id   int auto_increment
        primary key,
    tag_name varchar(100) not null,
    user_id  int          not null,
    constraint FKld85w5kr7ky5w4wda3nrdo0p8
        foreign key (user_id) references user (user_id)
);

create table if not exists note_tag
(
    note_id int not null,
    tag_id  int not null,
    primary key (note_id, tag_id),
    constraint FKcdpo2kwep1elf4i7fi4niltwn
        foreign key (tag_id) references tag (tag_id),
    constraint FKdchpnvg6njslx0ye4voybfwji
        foreign key (note_id) references note (note_id)
);
