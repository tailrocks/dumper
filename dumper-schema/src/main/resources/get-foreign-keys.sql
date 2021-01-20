SELECT
    source.table_name,
    source.column_name,
    target.table_name  AS foreign_table_name,
    target.column_name AS foreign_column_name
FROM information_schema.referential_constraints AS rc
         JOIN information_schema.key_column_usage source
         ON source.constraint_name = rc.constraint_name
         JOIN information_schema.key_column_usage target
         ON target.ordinal_position = source.position_in_unique_constraint
             AND target.constraint_name = rc.unique_constraint_name
GROUP BY rc.constraint_name,
         source.table_name,
         source.column_name,
         target.table_name,
         target.column_name,
         source.ordinal_position
ORDER BY rc.constraint_name,
         source.ordinal_position,
         source.table_name;
