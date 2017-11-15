-- role table
CREATE TABLE IF NOT EXISTS `role` (
  `role_id` INT NOT NULL AUTO_INCREMENT,
  `role_name` VARCHAR(45) NOT NULL,
  `role_desc` VARCHAR(45) NOT NULL,
  `is_predefined_role` BIT NOT NULL DEFAULT 0,
  `is_deleted` BIT NOT NULL DEFAULT 0,
  PRIMARY KEY (`role_id`),
  UNIQUE KEY role_roleName_unique (`role_name`))
ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- user table
CREATE TABLE IF NOT EXISTS user (
  user_id INT NOT NULL AUTO_INCREMENT,
  user_name VARCHAR(45) NOT NULL,
  email VARCHAR(45) NOT NULL,
  password VARCHAR(80) NULL,
  temp_password VARCHAR(80) NULL,
  created_dttm TIMESTAMP NULL,
  last_login_dttm TIMESTAMP NULL,
  is_active TINYINT(1) NOT NULL,
  role_id INT NOT NULL,
  PRIMARY KEY (user_id),
  UNIQUE KEY `user_userName_unique` (`user_name` ASC),
  UNIQUE KEY `user_email_unique` (`email` ASC),
  CONSTRAINT `fk_user_role`
    FOREIGN KEY (`role_id`)
    REFERENCES role (role_id)
    ON DELETE CASCADE
    ON UPDATE NO ACTION)
ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

INSERT IGNORE INTO role (role_name, role_desc, is_predefined_role) VALUES('Super user', 'Super user', 1);
INSERT IGNORE INTO role (role_name, role_desc, is_predefined_role) VALUES('Organizer', 'Organizer', 1);
INSERT IGNORE INTO role (role_name, role_desc, is_predefined_role) VALUES('Volunteer', 'Volunteer', 1);

CREATE TABLE IF NOT EXISTS `demnitar` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `nume` varchar(100) NOT NULL,
  `prenume` varchar(100) NOT NULL,
  `an_nastere` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `demnitar_unique` (`nume`,`prenume`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `declaratie_avere` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `demnitar_id` int(11) NOT NULL,
  `data_declaratiei` date NOT NULL,
  `data_depunerii` date DEFAULT NULL,
  `functie` varchar(200) NOT NULL,
  `functie2` varchar(200) DEFAULT NULL,
  `institutie` varchar(400) NOT NULL,
  `institutie2` varchar(400) DEFAULT NULL,
  `grup_politic` varchar(200) DEFAULT NULL,
  `link_declaratie` VARCHAR(200) DEFAULT NULL,
  `circumscriptia` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `declaratieAvere_unique` (`demnitar_id`,`data_declaratiei`),
  CONSTRAINT `declaratieAvere_demnitar_fk` FOREIGN KEY (`demnitar_id`) REFERENCES `demnitar` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `declaratie_interese` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `demnitar_id` int(11) NOT NULL,
  `data_declaratiei` date NOT NULL,
  `data_depunerii` date DEFAULT NULL,
  `functie` varchar(200) NOT NULL,
  `functie2` varchar(200) DEFAULT NULL,
  `institutie` varchar(400) NOT NULL,
  `institutie2` varchar(400) DEFAULT NULL,
  `grup_politic` varchar(200) DEFAULT NULL,
  `link_declaratie` VARCHAR(200) DEFAULT NULL,
  `circumscriptia` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `declaratieInterese_unique` (`demnitar_id`,`data_declaratiei`),
  CONSTRAINT `declaratieAvere_demnitar_fk` FOREIGN KEY (`demnitar_id`) REFERENCES `demnitar` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `declaratie_interese_asociat_sc` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `declaratie_interese_id` int(11) NOT NULL,
  `unitatea` text NOT NULL,
  `calitatea` varchar(100) DEFAULT NULL,
  `parti_sociale_actiuni` varchar(100) DEFAULT NULL,
  `valoarea` decimal(12,2) DEFAULT NULL,
  `moneda` varchar(100) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `declaratieIntereseAsociatSc_declaratieInterese_fk_idx` (`declaratie_interese_id`),
  CONSTRAINT `declaratieIntereseAsociatSc_declaratieInterese_fk` FOREIGN KEY (`declaratie_interese_id`) REFERENCES `declaratie_interese` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



CREATE TABLE IF NOT EXISTS `declaratie_avere_alte_active` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `declaratie_avere_id` int(11) NOT NULL,
  `descriere` text NOT NULL,
  PRIMARY KEY (`id`),
  KEY `declaratieAvereAlteActive_declaratieAvere_fk_idx` (`declaratie_avere_id`),
  CONSTRAINT `declaratieAvereAlteActive_declaratieAvere_fk` FOREIGN KEY (`declaratie_avere_id`) REFERENCES `declaratie_avere` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `declaratie_avere_bun_imobil` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `declaratie_avere_id` int(11) NOT NULL,
  `is_teren` bit(1) NOT NULL,
  `adresa_imobil` varchar(500) NOT NULL,
  `teren_categorie` int(11) DEFAULT NULL COMMENT '1 - Agricol, 2- Forestier, 3 - Intravilan, 4 - Luciu de apa, 5 - Alte categorii de terenuri extravilane, daca se afla in circuitul civil, 6 - altă categorie decât cele de mai sus',
  `cladire_categorie` int(11) DEFAULT NULL COMMENT '1 - apartament, 2 - casă de locuit, 3 - casă de vacanţă, 4 - spaţii comerciale/de producţie, 5 - altă categorie decât cele de mai sus',
  `an_dobandire` varchar(100) NOT NULL,
  `suprafata` decimal(12,2) NOT NULL,
  `explicatie_suprafata` TEXT DEFAULT NULL,
  `unitate_masura` varchar(10) DEFAULT NULL,
  `cota_parte` varchar(100) DEFAULT NULL,
  `mod_dobandire` text DEFAULT NULL,
  `titular` TEXT DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `declaratieAvereBunuriImobile_declaratieAvere_fk_idx` (`declaratie_avere_id`),
  CONSTRAINT `declaratieAvereBunuriImobile_declaratieAvere_fk` FOREIGN KEY (`declaratie_avere_id`) REFERENCES `declaratie_avere` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `declaratie_avere_bun_mobil` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `declaratie_avere_id` int(11) NOT NULL,
  `tip` varchar(100) NOT NULL,
  `marca` varchar(100) NOT NULL,
  `cantitate` int(11) NOT NULL,
  `an_fabricare` varchar(100) NOT NULL,
  `mod_dobandire` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `declaratieAvereBunMobil_declaratieAvere_fk_idx` (`declaratie_avere_id`),
  CONSTRAINT `declaratieAvereBunMobil_declaratieAvere_fk` FOREIGN KEY (`declaratie_avere_id`) REFERENCES `declaratie_avere` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `declaratie_avere_bijuterie` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `declaratie_avere_id` int(11) NOT NULL,
  `descriere` text NOT NULL,
  `an_dobandire` VARCHAR(100) NULL,
  `valoare_estimata` decimal(12,2) NOT NULL,
  `explicatie_bijuterie` TEXT DEFAULT NULL,
  `moneda` varchar(45) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `declaratieAvereBijuterie_declaratieAvere_fk_idx` (`declaratie_avere_id`),
  CONSTRAINT `declaratieAvereBijuterie_declaratieAvere_fk` FOREIGN KEY (`declaratie_avere_id`) REFERENCES `declaratie_avere` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `declaratie_avere_plasament` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `declaratie_avere_id` int(11) NOT NULL,
  `titular` TEXT NOT NULL,
  `emitent_titlu` text NOT NULL,
  `tipul_plasamentului` int(11) NULL COMMENT '1 - hârtii de valoare deţinute (titluri de stat, certificate, obligaţiuni), 2 - acţiuni sau părţi sociale în societăţi comerciale, 3 - împrumuturi acordate în nume personal, 4 - altele',
  `numar_titluri_sau_cota_parte` varchar(500) NOT NULL,
  `valoare` decimal(12,2) NOT NULL,
  `moneda` varchar(100) NOT NULL,
  `explicatie_plasament` TEXT DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `declaratieAverePlasament_declaratieAvere_fk_idx` (`declaratie_avere_id`),
  CONSTRAINT `declaratieAverePlasament_declaratieAvere_fk` FOREIGN KEY (`declaratie_avere_id`) REFERENCES `declaratie_avere` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `declaratie_avere_datorie` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `declaratie_avere_id` int(11) NOT NULL,
  `creditor` varchar(200) NOT NULL,
  `an_contractare` varchar(200) NOT NULL COMMENT 'e varchar pt ca nu se poate parsa automat',
  `scadenta` varchar(100) NOT NULL COMMENT 'e varchar pt ca nu se poate parsa automat',
  `valoare` decimal(12,2) NOT NULL,
  `explicatie_datorie` TEXT DEFAULT NULL,
  `moneda` varchar(100) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `declaratieAvereDatorie_declaratieAvere_fk_idx` (`declaratie_avere_id`),
  CONSTRAINT `declaratieAvereDatorie_declaratieAvere_fk` FOREIGN KEY (`declaratie_avere_id`) REFERENCES `declaratie_avere` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `declaratie_avere_cont` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `declaratie_avere_id` int(11) NOT NULL,
  `titular` TEXT NOT NULL,
  `institutie_bancara` text NOT NULL,
  `tip_cont` int(11) NULL COMMENT '1 - cont curent sau echivalente (inclusiv card), 2 - depozit bancar sau echivalente, 3 - fonduri de investiţii sau echivalente, inclusiv fonduri private de pensii sau alte sisteme cu acumulare, 4 - alt tip decât cele de mai sus',
  `moneda` varchar(100) NOT NULL,
  `an_deschidere_cont` varchar(100) NOT NULL,
  `sold_cont` decimal(12,2) NOT NULL,
  `explicatie_sold` TEXT DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `declaratieAvereCont_declaratieAvere_fk_idx` (`declaratie_avere_id`),
  CONSTRAINT `declaratieAvereCont_declaratieAvere_fk` FOREIGN KEY (`declaratie_avere_id`) REFERENCES `declaratie_avere` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `declaratie_avere_bun_instrainat` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `declaratie_avere_id` int(11) NOT NULL,
  `tip` varchar(500) NOT NULL,
  `data_instrainarii` varchar(45) NOT NULL COMMENT 'éste varchar pt ca nu poate fi parsat din excel',
  `persoana_beneficiara` varchar(100) NOT NULL,
  `forma_instrainarii` varchar(100) NOT NULL,
  `valoarea` decimal(12,2) NOT NULL,
  `explicatie_suma` TEXT DEFAULT NULL,
  `moneda` varchar(100) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `declaratieAvereBunInstrainat_declaratieAvere_fk_idx` (`declaratie_avere_id`),
  CONSTRAINT `declaratieAvereBunInstrainat_declaratieAvere_fk` FOREIGN KEY (`declaratie_avere_id`) REFERENCES `declaratie_avere` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `declaratie_avere_cadou` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `declaratie_avere_id` INT NOT NULL,
  `titular` TEXT NOT NULL,
  `sursa_venit` TEXT NOT NULL,
  `serviciul_prestat` VARCHAR(500) NULL,
  `venit` DECIMAL(12,2) NOT NULL,
  `explicatie_cadou` TEXT DEFAULT NULL,
  `moneda` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `declaratieAvereCadou_declaratieAvere_fk_idx` (`declaratie_avere_id` ASC),
  CONSTRAINT `declaratieAvereCadou_declaratieAvere_fk`
    FOREIGN KEY (`declaratie_avere_id`)
    REFERENCES `declaratie_avere` (`id`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION);

CREATE TABLE IF NOT EXISTS `declaratie_avere_venit` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `declaratie_avere_id` INT NOT NULL,
  `tip` INT NOT NULL COMMENT '1 - Salar, 2 - Activitati Independente, 3 - Cedarea Folosintei, 4 - Investitii, 5 - Pensii, 6 - Agricole, 7- Noroc,  8 - Alte Venituri',
  `titular` TEXT NOT NULL,
  `sursa_venit` TEXT NULL,
  `serviciul_prestat` VARCHAR(500) NOT NULL,
  venit_anual DECIMAL(12, 2) NOT NULL,
  `explicatie_venit` TEXT DEFAULT NULL,
  `moneda` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `declaratieAvereVenit_declaratieAvere_fk_idx` (`declaratie_avere_id` ASC),
  CONSTRAINT `declaratieAvereVenit_declaratieAvere_fk`
    FOREIGN KEY (`declaratie_avere_id`)
    REFERENCES `declaratie_avere` (`id`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION);

-- permission table
CREATE TABLE IF NOT EXISTS `permission` (
  permission_id int(11) NOT NULL AUTO_INCREMENT,
  permission_name varchar(100) NOT NULL,
  permission_code varchar(45) NOT NULL,
  permission_desc varchar(45) DEFAULT NULL,
  is_deleted bit(1) NOT NULL DEFAULT b'0',
  PRIMARY KEY (permission_id),
  UNIQUE KEY `permission_permissionName_idx` (`permission_name`),
  UNIQUE KEY `permission_permissionCode_idx` (`permission_code`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;


-- rolePermission table
CREATE TABLE IF NOT EXISTS role_permission (
  role_perm_id INT NOT NULL AUTO_INCREMENT,
  role_id INT NOT NULL,
  permission_id INT NOT NULL,
  PRIMARY KEY (role_perm_id),
  UNIQUE KEY rolePermission_unique (role_id, permission_id),
  INDEX `fk_rolePermission_role_idx` (`role_id` ASC),
  INDEX `fk_rolePermission_permission_idx` (`permission_id` ASC),
  CONSTRAINT `fk_rolePermission_role`
    FOREIGN KEY (`role_id`)
    REFERENCES role (role_id)
    ON DELETE CASCADE
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_rolePermission_permission_idx`
    FOREIGN KEY (permission_id)
    REFERENCES permission (permission_id)
    ON DELETE CASCADE
    ON UPDATE NO ACTION)
ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS permission_rest_method (
  permission_rest_method_id int(11) NOT NULL AUTO_INCREMENT,
  permission_id int(11) NOT NULL,
  rest_request_path varchar(200) DEFAULT NULL,
  rest_request_method varchar(10) DEFAULT NULL,
  PRIMARY KEY (`permission_rest_method_id`),
  UNIQUE KEY `PERMISSION_REST_METHOD_UNIQUE` (`rest_request_path`,`rest_request_method`,`permission_id`),
  KEY `fk_RN_PERMISSION_REST_METHOD_PERIMISSION` (`permission_id`),
  CONSTRAINT `fk_RN_PERMISSION_REST_METHOD_PERIMISSION` FOREIGN KEY (`permission_id`) REFERENCES `permission` (`permission_id`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=68 DEFAULT CHARSET=utf8;


-- add permissions for manage demnitari

INSERT IGNORE INTO permission (permission_name, permission_code, permission_desc, is_deleted)
VALUES ('Change temporary password', 'CTP', 'Change temporary password', 0);

INSERT IGNORE INTO permission_rest_method (permission_id, rest_request_path, rest_request_method)
VALUES ((SELECT permission_id FROM permission WHERE permission_code='CTP'), '/iam/iam/changeTemporaryPassword', 'POST');

INSERT IGNORE INTO role_permission (role_id, permission_id)
VALUES (1, (SELECT permission_id FROM permission WHERE permission_code='CTP'));

INSERT IGNORE INTO role_permission (role_id, permission_id)
VALUES (2, (SELECT permission_id FROM permission WHERE permission_code='CTP'));

INSERT IGNORE INTO role_permission (role_id, permission_id)
VALUES (3, (SELECT permission_id FROM permission WHERE permission_code='CTP'));

INSERT IGNORE INTO permission (permission_name, permission_code, permission_desc, is_deleted)
VALUES ('Manage demnitari', 'MDEN', 'Manage demnitari', 0);

INSERT IGNORE INTO permission_rest_method
(permission_id, rest_request_path, rest_request_method)
VALUES ((SELECT permission_id FROM permission WHERE permission_code='MDEN'), '/demnitarservice/demnitar', 'GET');

INSERT IGNORE INTO permission_rest_method
(permission_id, rest_request_path, rest_request_method)
VALUES ((SELECT permission_id FROM permission WHERE permission_code='MDEN'), '/demnitarservice/demnitar', 'POST');

INSERT IGNORE INTO permission_rest_method
(permission_id, rest_request_path, rest_request_method)
VALUES ((SELECT permission_id FROM permission WHERE permission_code='MDEN'), '/demnitarservice/demnitar', 'PUT');

INSERT IGNORE INTO permission_rest_method
(permission_id, rest_request_path, rest_request_method)
VALUES ((SELECT permission_id FROM permission WHERE permission_code='MDEN'), '/demnitarservice/demnitar/find', 'POST');

INSERT IGNORE INTO permission_rest_method
(permission_id, rest_request_path, rest_request_method)
VALUES ((SELECT permission_id FROM permission WHERE permission_code='MDEN'), '/demnitarservice/demnitar/declaratieavere', 'GET');

INSERT IGNORE INTO permission_rest_method
(permission_id, rest_request_path, rest_request_method)
VALUES ((SELECT permission_id FROM permission WHERE permission_code='MDEN'), '/demnitarservice/demnitar/declaratieavere', 'POST');

INSERT IGNORE INTO permission_rest_method
(permission_id, rest_request_path, rest_request_method)
VALUES ((SELECT permission_id FROM permission WHERE permission_code='MDEN'), '/demnitarservice/demnitar/declaratieavere', 'PUT');

INSERT IGNORE INTO permission_rest_method
(permission_id, rest_request_path, rest_request_method)
VALUES ((SELECT permission_id FROM permission WHERE permission_code='MDEN'), '/demnitarservice/demnitar/declaratieavere/find', 'POST');

INSERT IGNORE INTO permission_rest_method
(permission_id, rest_request_path, rest_request_method)
VALUES ((SELECT permission_id FROM permission WHERE permission_code='MDEN'), '/demnitarservice/demnitar/import', 'POST');

INSERT IGNORE INTO role_permission (role_id, permission_id)
VALUES (1, (SELECT permission_id FROM permission WHERE permission_code='MDEN'));

INSERT IGNORE INTO role_permission (role_id, permission_id)
VALUES (2, (SELECT permission_id FROM permission WHERE permission_code='MDEN'));

call CreateIndex('demnitar', 'demnitar_prenume_idx', 'prenume', 0);

call AddColumn('demnitar', 'grup_politic', "varchar(200) NULL");
call AddColumn('declaratie_avere', 'link_declaratie', "varchar(200) NULL");
call AddColumn('declaratie_avere_bun_imobil', 'explicatie_suprafata', "TEXT NULL");

call ChangeColumn('declaratie_avere_venit', 'titular', "TEXT NOT NULL");

CREATE TABLE IF NOT EXISTS `institutie` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `nume` varchar(200) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `institutie_unique` (nume)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `functie` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `nume` varchar(200) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `functie_unique` (nume)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

call AddColumn('declaratie_avere', 'institutie_id', 'int null');
call AddColumn('declaratie_avere', 'institutie2_id', 'int null');
call AddColumn('declaratie_avere', 'functie_id', 'int null');
call AddColumn('declaratie_avere', 'functie2_id', 'int null');
call AddForeignKey('fk_declaratie_avere_institutie', 'declaratie_avere', 'institutie_id', 'institutie', 'id');
call AddForeignKey('fk_declaratie_avere_institutie2', 'declaratie_avere', 'institutie2_id', 'institutie', 'id');
call AddForeignKey('fk_declaratie_avere_functie', 'declaratie_avere', 'functie_id', 'functie', 'id');
call AddForeignKey('fk_declaratie_avere_functie2', 'declaratie_avere', 'functie2_id', 'functie', 'id');
call CreateIndex('declaratie_avere', 'fk_declaratie_avere_institutie_idx', 'institutie_id', 0);
call CreateIndex('declaratie_avere', 'fk_declaratie_avere_institutie2_idx', 'institutie2_id', 0);
call CreateIndex('declaratie_avere', 'fk_declaratie_avere_functie_idx', 'functie_id', 0);
call CreateIndex('declaratie_avere', 'fk_declaratie_avere_functie2_idx', 'functie2_id', 0);

call AddColumn('demnitar', 'institutie_id', 'int null');
call AddColumn('demnitar', 'institutie2_id', 'int null');
call AddColumn('demnitar', 'functie_id', 'int null');
call AddColumn('demnitar', 'functie2_id', 'int null');
call AddForeignKey('fk_demnitar_institutie', 'demnitar', 'institutie_id', 'institutie', 'id');
call AddForeignKey('fk_demnitar_institutie2', 'demnitar', 'institutie2_id', 'institutie', 'id');
call AddForeignKey('fk_demnitar_functie', 'demnitar', 'functie_id', 'functie', 'id');
call AddForeignKey('fk_demnitar_functie2', 'demnitar', 'functie2_id', 'functie', 'id'); 
call CreateIndex('demnitar', 'fk_demnitar_institutie_idx', 'institutie_id', 0);
call CreateIndex('demnitar', 'fk_demnitar_institutie2_idx', 'institutie2_id', 0);
call CreateIndex('demnitar', 'fk_demnitar_functie_idx', 'functie_id', 0);
call CreateIndex('demnitar', 'fk_demnitar_functie2_idx', 'functie2_id', 0);

call DropColumn('declaratie_avere', 'institutie');
call DropColumn('declaratie_avere', 'institutie2');
call DropColumn('declaratie_avere', 'functie');
call DropColumn('declaratie_avere', 'functie2');

call DropColumn('demnitar', 'institutie');
call DropColumn('demnitar', 'institutie2');
call DropColumn('demnitar', 'functie');
call DropColumn('demnitar', 'functie2');

call AddColumn('declaratie_avere', 'voluntar_id', "INT NULL");
call AddForeignKey('fk_declaratie_avere_voluntar', 'declaratie_avere', 'voluntar_id', 'user', 'user_id');
call CreateIndex('declaratie_avere', 'fk_declaratie_avere_voluntar_idx', 'voluntar_id', 0);

call AddColumn('declaratie_avere', 'is_done', "BIT NOT NULL DEFAULT 0");
call CreateIndex('declaratie_avere', 'declaratie_avere_data_idx', 'data_declaratiei', 0);


INSERT IGNORE INTO permission (permission_name, permission_code, permission_desc, is_deleted)
VALUES ('Find declaratii avere', 'FDEC', 'Find declaratii avere', 0);

INSERT IGNORE INTO permission (permission_name, permission_code, permission_desc, is_deleted)
VALUES ('Update declaratii avere', 'UDEC', 'Update declaratii avere', 0);


INSERT IGNORE INTO permission_rest_method
(permission_id, rest_request_path, rest_request_method)
VALUES ((SELECT permission_id FROM permission WHERE permission_code='FDEC'), '/demnitarservice/demnitar/declaratieavere', 'GET');

INSERT IGNORE INTO permission_rest_method
(permission_id, rest_request_path, rest_request_method)
VALUES ((SELECT permission_id FROM permission WHERE permission_code='FDEC'), '/demnitarservice/demnitar/declaratieavere/find', 'POST');

INSERT IGNORE INTO permission_rest_method
(permission_id, rest_request_path, rest_request_method)
VALUES ((SELECT permission_id FROM permission WHERE permission_code='UDEC'), '/demnitarservice/demnitar/declaratieavere', 'PUT');

INSERT IGNORE INTO role_permission (role_id, permission_id)
VALUES (3, (SELECT permission_id FROM permission WHERE permission_code='FDEC'));

INSERT IGNORE INTO role_permission (role_id, permission_id)
VALUES (3, (SELECT permission_id FROM permission WHERE permission_code='UDEC'));