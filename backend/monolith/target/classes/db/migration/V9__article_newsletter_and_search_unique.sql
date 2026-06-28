ALTER TABLE articles
    ADD COLUMN newsletter_sent_at DATETIME NULL AFTER published_at;

DELETE sd1 FROM search_documents sd1
    INNER JOIN search_documents sd2
        ON sd1.target_type = sd2.target_type
       AND sd1.target_id = sd2.target_id
       AND sd1.id > sd2.id;

-- uk_search_documents_target already created in V8; dedupe above is sufficient
