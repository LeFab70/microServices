-- V2 a inséré les catégories de seed avec des ids explicites, sans passer par nextval(category_seq),
-- donc la séquence n'a jamais avancé et reste bloquée à 1 alors que les ids 1..951 sont déjà pris.
SELECT setval('category_seq', (SELECT COALESCE(MAX(id), 0) FROM category) + 50, false);
