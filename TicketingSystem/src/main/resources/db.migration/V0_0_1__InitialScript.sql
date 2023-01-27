



INSERT INTO `department` (`description`, `name`) VALUES ('all', 'ALL');

DELIMITER $$
CREATE
TRIGGER demo
BEFORE INSERT ON `ticket`
FOR EACH ROW
BEGIN
IF (NEW.ticket_id IS NULL) THEN

  SELECT
    MAX(id) INTO @max_ticket_id
  FROM
    ticket;

  IF (@max_ticket_id IS NULL) THEN

    SET NEW.ticket_id = CONCAT('Ticket_', '1');
  ELSE

    SET NEW.ticket_id = CONCAT('Ticket_',@max_ticket_id+1);
  END IF;
END IF;
END$$
DELIMITER ;

DELIMITER $$
CREATE
TRIGGER test
BEFORE INSERT ON `user`
FOR EACH ROW
BEGIN
IF (NEW.user_id IS NULL) THEN

  SELECT
    MAX(id) INTO @max_user_id
  FROM
    user;

  IF (@max_user_id IS NULL) THEN

    SET NEW.user_id = CONCAT('User_', '1');
  ELSE

    SET NEW.user_id = CONCAT('User_',@max_user_id+1);
  END IF;
END IF;
END$$
DELIMITER ;




DELIMITER $$
CREATE
TRIGGER project
BEFORE INSERT ON `project`
FOR EACH ROW
BEGIN
IF (NEW.project_id IS NULL) THEN

  SELECT
    MAX(id) INTO @max_project_id
  FROM
    project;

  IF (@max_project_id IS NULL) THEN

    SET NEW.project_id = CONCAT('Project_', '1');
  ELSE

    SET NEW.project_id = CONCAT('Project_',@max_project_id+1);
  END IF;
END IF;
END$$
DELIMITER ;


SELECT id INTO @var FROM `role` WHERE role_name = 'SUPER_ADMIN';
SELECT @var;
DROP PROCEDURE IF EXISTS proc_cursor_to_loopAndInsert;
DELIMITER ;;
CREATE PROCEDURE proc_cursor_to_loopAndInsert()
BEGIN
  DECLARE perm_id INT;
  DECLARE permission_id_cursor CURSOR FOR SELECT id from roles_permissions;
    OPEN   permission_id_cursor;
  loop_through_rows: LOOP
    FETCH  NEXT FROM permission_id_cursor INTO perm_id;
   INSERT INTO roles_has_roles_permissions(roles_id,roles_permissions_id)
  values(@var,perm_id);
  END LOOP;
  CLOSE permission_id_cursor;
execute proc_cursor_to_loopAndInsert;
END;
;;


    INSERT INTO `role` (`role_description`, `role_name`) VALUES ('super_admin', 'SUPER_ADMIN');
    INSERT INTO `user` ( `email`, `is_deleted`, `name`, `password`, `phone_number`, `roles_id`) VALUES ('springemail234@gmail.com', 0,'Shubham','$2a$10$cO4j1lpui7Re19SboxF.N.cGwyk4CT4q8vVxoxVosDBuEofFKUbma','7717549945',
       (SELECT id from `role` where role_name ='SUPER_ADMIN' ));



  INSERT INTO `roles_permissions` (`created_at`,`page_name`, `page_permission`,`updated_at`) VALUES (CURRENT_TIMESTAMP(),'manage_users', 'view',CURRENT_TIMESTAMP());
  INSERT INTO `roles_permissions` (`created_at`,`page_name`, `page_permission`,`updated_at`) VALUES (CURRENT_TIMESTAMP(),'manage_users', 'view & edit',CURRENT_TIMESTAMP());




