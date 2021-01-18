CREATE FUNCTION increase_version() RETURNS trigger
    LANGUAGE plpgsql
AS
$$
BEGIN
    NEW.version = NEW.version + 1;
    RETURN NEW;
END;
$$;


CREATE FUNCTION update_last_modified_date() RETURNS trigger
    LANGUAGE plpgsql
AS
$$
BEGIN
    NEW.last_modified_date := NOW();
    RETURN NEW;
END;
$$;
