drop procedure if exists leavesAL;
drop procedure if exists leavesALRec;

delimiter $$

/**
 * Leaves operation for adjacency list model.
 * This method calls recursive leavesALRec(). This method should be used instead of leavesALRec(). 
 */
create procedure leavesAL(tableName varchar(15), nodeId int)
begin
    -- Create temp tables
    create temporary table if not exists LeavesTable(
        nodeId int primary key
    );
    create temporary table if not exists ChildTable(
        parentId int not null,
        childId int not null,
        primary key (parentId, childId)
    );
    
    -- Set recursion depth 
    set max_sp_recursion_depth = 100;
    
    -- Call recursive function.
    call leavesALRec(tableName, nodeId);
    
    -- Get result
    select * from LeavesTable;
    
    -- Clean up
    Drop temporary table if exists LeavesTable;
    Drop temporary table if exists ChildTable;
    
end $$

/**
 * Recursive version of leaveAL. This should NOT be called directly.
 *
 * Reference:
 * 1. http://bugs.mysql.com/bug.php?id=28227
 */
create procedure leavesALRec(tableName varchar(15), nodeId int)
begin
    --
    -- Declarations used in recursive calls (else statement below).
    --
    declare oldp int;               -- Holds previous value of @p
    declare child int;              -- Support iteration through children
	declare done int default false; -- Iteration variable.
    declare cur cursor for select childId from ChildTable where parentId=@p;
    declare continue handler for not found set done = true;

    -- Get child count
    set @s := concat('select count(*) into @count from ', tableName, ' where parent=', nodeId);
    prepare query from @s;
	execute query;
	deallocate prepare query;
	
	-- If child count is 0, then this is a leaf; so add to result table.
	if @count = 0 then
	    set @s := concat('insert ignore into LeavesTable values(', nodeId, ')');
	    prepare query from @s;
	    execute query;
	    deallocate prepare query;
	-- Else, this is not a leaf; perform recursive call.
	else
	    --
	    -- Insert children into temporary table, which we will iterate through below.
	    --
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
	        call leavesALRec(tableName, child);
	    end while;
	    
	    -- Delete children
	    delete from ChildTable where parentId=@p;
	    
	    -- Restore @p
	    set @p := oldp;
	end if;
end$$

delimiter ;

