CREATE VIEW vw_batch_fefo AS
SELECT
    b.id,
    b.batch_number,
    b.expiration_date,
    b.current_quantity,
    b.batch_status,
    m.id as medicament_id
    m.name as medicament_name,
    m.medicament_category
FROM batches b
         JOIN medicaments m ON m.id = b.medication_id
WHERE b.batch_status = 'ACTIVE'
  AND b.current_quantity > 0
ORDER BY b.expiration_date ASC;

CREATE VIEW vw_batch_fifo AS
SELECT
    b.id,
    b.batch_number,
    b.expiration_date,
    b.current_quantity,
    b.batch_status,
    m.id as medicament_id
    m.name as medicament_name,
    m.medicament_category,
    se.entry_date
FROM batches b
         JOIN medicaments m ON m.id = b.medication_id
         JOIN stock_entries se ON se.batch_id = b.id
WHERE b.batch_status = 'ACTIVE'
  AND b.current_quantity > 0
ORDER BY se.entry_date ASC;