--
-- This is a RDBMS specific SQL script containing the initial DDL
-- For schema evolution with FlyWay see: http://flywaydb.org/getstarted/how.html
--
-- Versioning of the XML files must follow the naming conventions described in: http://flywaydb.org/documentation/migration
--
-- SQL Manager for PostgreSQL 5.7.1.47382
-- ---------------------------------------
-- Host      : localhost
-- Database  : ftsearch
-- Version   : PostgreSQL 9.2.13, compiled by Visual C++ build 1600, 64-bit



SET search_path = public, pg_catalog;
DROP TRIGGER trigger_ts_document_update ON public.client;
DROP TRIGGER trigger_ts_client_order ON public.client_order;
DROP TRIGGER trigger_ts_client_preference ON public.client_preference;
ALTER TABLE ONLY public.client_order DROP CONSTRAINT client_order_fk;
ALTER TABLE ONLY public.client_preference DROP CONSTRAINT client_preference_fk;
ALTER TABLE ONLY public.client_preference DROP CONSTRAINT client_preference_pkey;
ALTER TABLE ONLY public.client_order DROP CONSTRAINT client_order_pkey;
ALTER TABLE ONLY public.client DROP CONSTRAINT client_pkey;
DROP INDEX public.client_ts_idx;
DROP FUNCTION public.search (p_query text, p_mode text);
DROP FUNCTION public.ts_client_aux_data_update ();
DROP FUNCTION public.ts_document_update ();
DROP TABLE public.client_preference;
DROP TABLE public.client_order;
DROP TABLE public.client;
SET check_function_bodies = false;
--
-- Definition for function ts_document_update (OID = 46422) :
--
CREATE FUNCTION public.ts_document_update (
)
RETURNS trigger
AS '
--
-- This is the main TS VECTOR update function for the client fulltext search document (the `ts_document` column)
--
DECLARE
	-- declare some data holding variables
	v_order_descriptions TEXT;
	v_pref_locations TEXT;
	v_pref_products TEXT;
BEGIN

-- Collect the descriptions from all client orders into a single string (space-separated):
-- note that we do not care about the order in which the words will appear or whether there are duplicates
-- the goal is to collect all words accross all order descriptions which will be munged together to form part of the TSVECTOR document.
  SELECT string_agg(o.description, '' '')
  INTO v_order_descriptions
  FROM client_order o
  -- `new.id` is the client ID:
  WHERE o.client_id = new.id;

-- Here, we collect the locations and the products in separate collections
-- so we can add them to the TS document with different "weight" settings
  SELECT string_agg(p.location, '' ''),string_agg(p.products,'' '')
  INTO v_pref_locations, v_pref_products
  FROM client_preference p
  WHERE p.client_id = new.id;

-- Now that we have `v_order_descriptions`, `v_pref_locations` and `v_pref_products`
-- we are ready to create the TS document and assign it as NEW value for the `ts_document` column
-- >> The tsvector is in reality just a specially structured sequence of characters (string), so we can use the
--    concatenation operator || to add the tsvectors together.
  NEW.ts_document :=
      -- 1) The function `setweight` can be used to label the entries of a tsvector with a given weight,
      -- where a weight is one of the letters A, B, C, or D
      -- 2) We use `to_tsvector` to convert the text into the tsvector datatype, using specific language dictionary
      setweight(to_tsvector(''pg_catalog.english'', new.name), ''A'') ||  -- The `name` gets highest priority,
      setweight(to_tsvector(''pg_catalog.english'', new.bio), ''B'') ||  --  then biography with weight=B,
      setweight(to_tsvector(''pg_catalog.english'', coalesce(v_order_descriptions,'''')), ''C'') ||  -- the order descriptions
      setweight(to_tsvector(''pg_catalog.english'', coalesce(v_pref_products,'''')), ''C'') ||  -- and product preferences will have weight of C,
      setweight(to_tsvector(''pg_catalog.english'', coalesce(v_pref_locations,'''')), ''D'')  -- and finally - locations with lowest weight D
      ;

-- Note the use of the `coalesce()` when creating the tsvectors above.
-- This is essential since there is no guarantee that the values will always be non-null.
-- Anything concatenated with <NULL> in PG results in <NULL>

-- Since this is a trigger function, we must return the record ref:
  RETURN NEW;


END;
'
LANGUAGE plpgsql;
--
-- Definition for function ts_client_aux_data_update (OID = 46426) :
--
CREATE FUNCTION public.ts_client_aux_data_update (
)
RETURNS trigger
AS '
BEGIN
	-- This is a generic funciton to be called whenever data is inserted or updated in any client-related tables.
    -- Here, we artificially update the `last_updated` field on the `client` table, which in turn triggers
    -- the on-update trigger in that table which will then cause the TSVECTORS to be rebuild taking into account the new data.

    -- :: IMPORTANT ::
    -- This tigger must be executed AFTER the event (inser/update) so that the data
    -- is already saved by the time the `client` trigger kicks-in.

    -- :: NOTE ::
    -- You will notice that the statement below expects `NEW.client_id` to be available during the execution.
    -- This will only be the case when this function is attached to a trigger on a table with a `client_id` column.
    -- If good design practices are followed, this column will be available on all tables connected to the main `client` table.

	UPDATE client SET last_updated=CURRENT_TIMESTAMP WHERE id=NEW.client_id;


    -- Since this is a trigger function, we must return the record ref:
    RETURN NEW;

END;
'
LANGUAGE plpgsql;
--
-- Definition for function search (OID = 46439) :
--
CREATE FUNCTION public.search (
  p_query text,
  p_mode text
)
RETURNS refcursor
AS '
DECLARE
  res_search refcursor;
  v_query tsquery;
BEGIN

  IF p_mode = ''PHRASE'' THEN
    -- search for exact phrase
    -- the query is same as for `ANY` and the results will be further narrowed by ILIKE match
    v_query := to_tsquery(regexp_replace(p_query,'' +'', ''|'', ''g''));
  ELSEIF p_mode = ''ANY'' THEN
    -- search for any of the terms (OR)
    v_query := to_tsquery(regexp_replace(p_query,'' +'', ''|'', ''g''));
  ELSE
    -- by default search for all of the terms (AND)
    v_query := plainto_tsquery(p_query);
  END IF;

  OPEN res_search FOR
  SELECT
    c.id,
    c.name,
  	ts_rank(c.ts_document, v_query) AS rank
  FROM client c
  WHERE c.ts_document @@ v_query
  ORDER BY rank DESC;


  -- return main search resultset:
  RETURN res_search;

END;
'
LANGUAGE plpgsql;
--
-- Structure for table client (OID = 46374) :
--
CREATE TABLE public.client (
    id serial NOT NULL,
    name text NOT NULL,
    bio text,
    ts_document tsvector,
    last_updated timestamp without time zone
)
WITH (oids = false);
--
-- Structure for table client_order (OID = 46394) :
--
CREATE TABLE public.client_order (
    id serial NOT NULL,
    client_id integer NOT NULL,
    description text NOT NULL,
    date timestamp(0) without time zone NOT NULL
)
WITH (oids = false);
--
-- Structure for table client_preference (OID = 46403) :
--
CREATE TABLE public.client_preference (
    client_id integer NOT NULL,
    location text NOT NULL,
    products text
)
WITH (oids = false);
--
-- Definition for index client_ts_idx (OID = 46432) :
--
CREATE INDEX client_ts_idx ON client USING gin (ts_document);
--
-- Definition for index client_pkey (OID = 46381) :
--
ALTER TABLE ONLY client
    ADD CONSTRAINT client_pkey
    PRIMARY KEY (id);
--
-- Definition for index client_order_pkey (OID = 46401) :
--
ALTER TABLE ONLY client_order
    ADD CONSTRAINT client_order_pkey
    PRIMARY KEY (id);
--
-- Definition for index client_preference_pkey (OID = 46409) :
--
ALTER TABLE ONLY client_preference
    ADD CONSTRAINT client_preference_pkey
    PRIMARY KEY (client_id);
--
-- Definition for index client_preference_fk (OID = 46411) :
--
ALTER TABLE ONLY client_preference
    ADD CONSTRAINT client_preference_fk
    FOREIGN KEY (client_id) REFERENCES client(id) ON DELETE CASCADE;
--
-- Definition for index client_order_fk (OID = 46416) :
--
ALTER TABLE ONLY client_order
    ADD CONSTRAINT client_order_fk
    FOREIGN KEY (client_id) REFERENCES client(id) ON DELETE CASCADE;
--
-- Definition for trigger trigger_ts_client_preference (OID = 46428) :
--
CREATE TRIGGER trigger_ts_client_preference
    AFTER INSERT OR UPDATE ON client_preference
    FOR EACH ROW
    EXECUTE PROCEDURE ts_client_aux_data_update ();
--
-- Definition for trigger trigger_ts_client_order (OID = 46429) :
--
CREATE TRIGGER trigger_ts_client_order
    AFTER INSERT OR UPDATE ON client_order
    FOR EACH ROW
    EXECUTE PROCEDURE ts_client_aux_data_update ();
--
-- Definition for trigger trigger_ts_document_update (OID = 46430) :
--
CREATE TRIGGER trigger_ts_document_update
    BEFORE INSERT OR UPDATE ON client
    FOR EACH ROW
    EXECUTE PROCEDURE ts_document_update ();
--
-- Comments
--
COMMENT ON SCHEMA public IS 'standard public schema';
COMMENT ON FUNCTION public.ts_document_update () IS 'The purpose of this function is to update the fulltext search `ts_document` field in the `client` table.
The events which execute this function are on-insert/update triggers for all tables containing data which must be indexed for fulltext search.
 ';
