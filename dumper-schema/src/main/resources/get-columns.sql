SELECT table_name,
    column_name,
    ordinal_position,
    data_type,
    udt_name
FROM information_schema.columns
WHERE table_schema = 'public' AND
        table_name IN (
        SELECT table_name
        FROM information_schema.tables
        WHERE table_schema = 'public' AND table_type = 'BASE TABLE'
    )
ORDER BY table_schema, table_name, ordinal_position;
