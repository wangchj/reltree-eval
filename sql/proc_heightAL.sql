-- Reset creation of procedures
drop procedure if exists heightAL;
drop procedure if exists heightALRec;
drop temporary table if exists ChildTable;

delimiter $$

/**
 * Leaves operation for adjacency list model. This procedure should be call instead of heightALRec().
 * @param tableName the name of the table that contains the nodes.
 * @param nodeId    the id of starting node.
 */
create procedure heightAL(tableName varchar(15), nodeId int)
begin
    
    declare height int;
    
    set max_sp_recursion_depth = 100;
    
    -- Temporary table to be used in recursion
    create temporary table if not exists ChildTable(
        parentId int,
        childId int
    );
    
    -- Call recursive function to find height
    call heightALRec(tableName, nodeId, height);
    -- Get result
    select height;
    -- Clean up
    drop temporary table if exists ChildTable;
end $$

/**
 * Recursive version of heightAL(). This procedure is called by heightAL() and should NOT be called directly.
 * @param tableName the name of the table that contains the nodes.
 * @param nodeId    the id of starting node.
 * @param res       the variable that stores the output result.
 */
create procedure heightALRec(tableName varchar(15), nodeId int, out res int)
label: begin
    -- Delcarations
    declare height  int;               -- Height from recursive call (of child node)
    declare max     int default 0;     -- Max height of all children
    declare child   int;               -- Child ID for cursor iterations
    declare oldp    int;               -- Holds previous value of @p
    declare done    int default false; -- Cursor control varaible.
    declare cur cursor for select childId from ChildTable where parentId=@p;
    declare continue handler for not found set done = true;
    
    -- Get child count
    set @s := concat('select count(*) into @count from ', tableName, ' where parent=', nodeId);
    prepare query from @s;
	execute query;
	deallocate prepare query;
	
	-- If child count is 0, return height = 1.
	if @count = 0 then
	    set res := 1;
	    leave label;  -- Exit procedure (this iteration)
	end if;
	
	
	set @s := concat('insert into ChildTable select parent, id from ', tableName, ' where parent=', nodeId);
	prepare query from @s;
	execute query;
	deallocate prepare query;

	--
	-- Iterate through each child and recursive call for each.
	--
	
	-- Save @p, so it can be restored later.
	set oldp := @p;
	set @p   := nodeId;

    open cur;

    while not done do
        fetch cur into child; -- child is local variable decalred above.
	    call heightALRec(tableName, child, height);
	    if height > max then
	        set max := height;
	    end if;
	end while;
	
	-- Delete children from ChildTable
	delete from ChildTable where parentId=@p;
	-- Restore @p
	set @p := oldp;
	-- Return max height
	set res := max + 1;
end $$

delimiter ;

