DROP PROCEDURE IF EXISTS AddColumn;
DROP PROCEDURE IF EXISTS DropColumn;
DROP PROCEDURE IF EXISTS CreateIndex;
DROP PROCEDURE IF EXISTS DropIndex;
DROP PROCEDURE IF EXISTS AddForeignKey;
DROP PROCEDURE IF EXISTS ChangeColumn;
DROP PROCEDURE IF EXISTS RenameColumn;
DROP PROCEDURE IF EXISTS DropForeignKey;

DELIMITER $$

CREATE PROCEDURE AddColumn(
	IN tableName VARCHAR(256),
	IN fieldName VARCHAR(256),
	IN fieldDef VARCHAR(256))
begin
	IF NOT EXISTS (
		SELECT 1 FROM information_schema.COLUMNS
		WHERE column_name = fieldName
		and table_name  = tableName
		and table_schema  = Database()
		)
	THEN
		set @ddl = CONCAT('ALTER TABLE ',Database(),'.',tableName,
			' ADD COLUMN ',fieldName,' ',fieldDef);

		prepare stmt from @ddl;
		execute stmt;
	END IF;
end $$

CREATE PROCEDURE DropForeignKey(IN tableName VARCHAR(64), IN constraintName VARCHAR(64))
    BEGIN
        IF EXISTS(
            SELECT * FROM information_schema.table_constraints
            WHERE
                table_schema    = DATABASE()     AND
                table_name      = tableName      AND
                constraint_name = constraintName AND
                constraint_type = 'FOREIGN KEY')
        THEN
            SET @query = CONCAT('ALTER TABLE ', tableName, ' DROP FOREIGN KEY ', constraintName, ';');
            PREPARE stmt FROM @query;
            EXECUTE stmt;
            DEALLOCATE PREPARE stmt;
        END IF;
    END$$

CREATE PROCEDURE AddForeignKey(
	IN constraintName VARCHAR(256),
	IN tableName VARCHAR(256),
	IN fieldName VARCHAR(256),
	IN foreignTableName VARCHAR(256),
	IN foreignFieldName VARCHAR(256))
begin
	IF NOT EXISTS (
		SELECT 1 FROM information_schema.TABLE_CONSTRAINTS
		WHERE constraint_name  = constraintName
		)
	THEN
		set @ddl = CONCAT('ALTER TABLE ',Database(),'.',tableName,
			' ADD CONSTRAINT ',constraintName,' FOREIGN KEY (',fieldName,') REFERENCES ',foreignTableName, ' (' ,foreignFieldName, ')');
		prepare stmt from @ddl;
		execute stmt;
	END IF;
end $$

CREATE PROCEDURE DropColumn(
	IN tableName VARCHAR(256),
	IN fieldName VARCHAR(256))
begin
	IF EXISTS (
		SELECT 1 FROM information_schema.COLUMNS
		WHERE column_name  = fieldName
		and table_name  = tableName
		and table_schema  = Database()
		)
	THEN
		set @ddl=CONCAT('ALTER TABLE ',Database(),'.',tableName,
			' DROP COLUMN ',fieldName);

		prepare stmt from @ddl;
		execute stmt;
	END IF;
end $$

CREATE PROCEDURE CreateIndex(
	IN tableName VARCHAR(256),
	IN indexName VARCHAR(256),
	IN columnList VARCHAR(256),
  IN isUnique BIT)
begin
	IF NOT EXISTS (
		SELECT 1
        FROM INFORMATION_SCHEMA.STATISTICS
        WHERE TABLE_SCHEMA  = Database()
        AND table_name  = tableName
        AND index_name  = indexName
		)
	THEN
		set @ddl=CONCAT('CREATE ',
		                 IF(isUnique = 1, 'UNIQUE ', ' '), 'INDEX ',
		                 indexName, ' ON ', Database(), '.', tableName, ' (', columnList ,')');

		prepare stmt from @ddl;
		execute stmt;
	END IF;
end $$

CREATE PROCEDURE DropIndex(
	IN tableName VARCHAR(256),
	IN indexName VARCHAR(256))
begin
	IF EXISTS (
		SELECT 1
        FROM INFORMATION_SCHEMA.STATISTICS
        WHERE TABLE_SCHEMA  = Database()
        AND table_name  = tableName
        AND index_name  = indexName
		)
	THEN
		set @ddl=CONCAT('DROP INDEX ',
		                 indexName, ' ON ', Database(), '.', tableName);

		prepare stmt from @ddl;
		execute stmt;
	END IF;
end $$

CREATE PROCEDURE ChangeColumn(
	IN tableName VARCHAR(256),
	IN fieldName VARCHAR(256),
	IN fieldDef VARCHAR(256))
begin
	IF EXISTS (
		SELECT 1 FROM information_schema.COLUMNS
		WHERE column_name = fieldName
		and table_name  = tableName
		and table_schema  = Database()
		)
	THEN
		set @ddl = CONCAT('ALTER TABLE ',Database(),'.',tableName,
			' CHANGE COLUMN ',fieldName,' ',fieldName,' ',fieldDef);

		prepare stmt from @ddl;
		execute stmt;
	END IF;
end $$

CREATE PROCEDURE RenameColumn(
	IN tableName VARCHAR(256),
	IN oldFieldName VARCHAR(256),
	IN newFieldName VARCHAR(256),
	IN fieldDef VARCHAR(256))
begin
	IF EXISTS (
		SELECT 1 FROM information_schema.COLUMNS
		WHERE column_name = oldFieldName
		and table_name  = tableName
		and table_schema  = Database()
		)
	THEN
		set @ddl = CONCAT('ALTER TABLE ',Database(),'.',tableName,
			' CHANGE COLUMN `',oldFieldName,'` `',newFieldName,'` ',fieldDef);

		prepare stmt from @ddl;
		execute stmt;
	END IF;
end $$

DELIMITER ;