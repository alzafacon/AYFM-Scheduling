--  suggestion query

/*
only two adjustments need to made for this query to fit any request
the assignment_id can be changed
and for querying householder chage assignee to householder

I decided not to filter for section because it would be 1) unfair, 2) more difficult to implement, and 3) negligibally slower
*/

-- https://dev.mysql.com/doc/refman/5.5/en/group-by-handling.html
/*
The above link explains that grouping will give indeterminate results for non group by columns
when those columns do not all have the same value
*/

-- most recent %type% given

select full_name, max(date_assgn), person_id, assignment_id
from 
		works_on
    left join
		assignment
	on person_id = assignee and assignment_id = assgn_type
    join
		person
	on person_id = id_person
where assignment_id = 1
and isactive = true -- todo will remove this from the schema (application will keep track of activity)
group by person_id
order by max(date_assgn);

/*
First, the where clause is used to filter the rows used to perform the join.
This means that groupby does not need to include assgn_type.

To find the specific information about the assignment given on the last date
    the assignment table would need to be queried for a match WHERE person_id and date_assgn
    
*/


