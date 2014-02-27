delimiter $$

create procedure children(tableName varchar(15), nodeId int)
begin
    set @s := concat('select id from ', tableName, ' where parent=', nodeId);
    prepare query from @s;
	execute query;
	deallocate prepare query;
end$$

delimiter ;

