ALTER TABLE search_documents
  ADD FULLTEXT INDEX ft_search_documents_title_content (title, content) WITH PARSER ngram;
