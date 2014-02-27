delimiter $$

create procedure rootAL(tableName varchar(15), nodeId int)
begin
    set @s := concat('select id, parent into @n, @p from ', tableName, ' where id=', nodeId);
    prepare query from @s;
	execute query;
	deallocate prepare query;
	
    while @p is not null do
        set @s := concat('select id, parent into @n, @p from ', tableName, ' where id=', @p);
        prepare query from @s;
	    execute query;
	    deallocate prepare query;
    end while;
	select @n;
end$$

delimiter ;

