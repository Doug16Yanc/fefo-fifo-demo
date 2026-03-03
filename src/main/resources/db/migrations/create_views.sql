CREATE OR REPLACE VIEW vw_batch_fefo AS
SELECT
    b.id as batch_id,
    b.batch_number,
    b.expiration_date,
    b.current_quantity,
    b.batch_status,
    m.id as medicament_id,
    m.name as medicament_name,
    m.volume,
    m.medicament_category
FROM batches b
         JOIN medicaments m ON m.id = b.medication_id
WHERE b.batch_status = 'ACTIVE'
  AND b.current_quantity > 0
  AND (
    m.medicament_category IN (
      'CONTROLLED_SUBSTANCE',
      'VACCINE',
      'BLOOD_DERIVATIVE',
      'BIOTECHNOLOGICAL',
      'INSULIN',
      'HOMEOPATHIC'
        ) OR m.cold_chain = true
    )
ORDER BY b.expiration_date ASC;

CREATE OR REPLACE VIEW vw_batch_fifo AS
SELECT
    b.id as batch_id,
    b.batch_number,
    b.expiration_date,
    b.current_quantity,
    b.batch_status,
    m.id as medicament_id,
    m.name as medicament_name,
    m.volume,
    m.medicament_category,
    MIN(se.entry_date) as entry_date
FROM batches b
         JOIN medicaments m ON m.id = b.medication_id
         LEFT JOIN stock_entries se ON se.batch_id = b.id
WHERE b.batch_status = 'ACTIVE'
  AND b.current_quantity > 0
  AND m.medicament_category NOT IN (
    'CONTROLLED_SUBSTANCE',
    'VACCINE',
    'BLOOD_DERIVATIVE',
    'BIOTECHNOLOGICAL',
    'INSULIN',
    'HOMEOPATHIC'
    )
  AND m.cold_chain = false
GROUP BY
    b.id,
    b.batch_number,
    b.expiration_date,
    b.current_quantity,
    b.batch_status,
    m.id,
    m.name,
    m.volume,
    m.medicament_category
ORDER BY entry_date ASC;


DELETE FROM stock_entries se
WHERE se.id NOT IN (
    SELECT MAX(se2.id)
    FROM stock_entries se2
    GROUP BY se2.batch_id
);
