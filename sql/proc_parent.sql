delimiter $$

create procedure parent(tableName varchar(15), nodeId int)
begin
    set @s := concat('select parent from ', tableName, ' where id =', nodeId);
    prepare query from @s;
	execute query;
	deallocate prepare query;
end$$

delimiter ;

