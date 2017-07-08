/*
Query for Suggestion Ranking 

Rank all students and then suggest the next assignment type.
*/

-- Rank based on most recently given part
SELECT
    MAX(`a`.`week`) AS `most_recent`, 
    `p`.`full_name`, `p`.`id`, `a`.`lesson`, `a`.`classroom`, `a`.`type`
FROM
    `ayfm`.`person` `p`
        LEFT JOIN
    `ayfm`.`assignment` `a` ON `p`.`id` = `a`.`assignee`
WHERE
    TRUE = `p`.`isactive`
GROUP BY `p`.`id`
ORDER BY `most_recent`;

-- Based on the assignable types (obtained below), suggest next type 
SELECT `type_id`
FROM `works_on`
WHERE `works_on`.`person_id` = ?;