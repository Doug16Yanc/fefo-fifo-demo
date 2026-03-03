INSERT INTO medicaments (name, description, medicament_category, unit_of_measure, cold_chain, volume) VALUES
      ('Amoxicillin 500mg', 'Broad-spectrum antibiotic', 'ANTIBIOTIC', 'CAPSULE', false, 0.50),
      ('Ibuprofen 400mg', 'Anti-inflammatory and analgesic', 'ANALGESIC', 'TABLET', false, 0.40),
      ('Insulin Glargine 100UI', 'Long-acting insulin', 'HORMONE', 'ML', true, 10.00),
      ('Omeprazole 20mg', 'Proton pump inhibitor', 'GASTROINTESTINAL', 'CAPSULE', false, 0.30),
      ('Metformin 850mg', 'Oral antidiabetic', 'ANTIDIABETIC', 'TABLET', false, 0.85);

INSERT INTO batches (batch_number, manufacturing_date, expiration_date, initial_quantity, current_quantity, batch_status, medication_id) VALUES
     (1001, '2024-01-01', '2026-06-01', 500, 450, 'ACTIVE', 1),
     (1002, '2024-03-15', '2026-04-15', 300, 270, 'ACTIVE', 1),
     (1003, '2024-06-01', '2025-12-01', 200, 0,   'EXPIRED', 2),
     (1004, '2024-08-10', '2027-08-10', 1000, 950, 'ACTIVE', 2),
     (1005, '2024-02-20', '2026-03-20', 150, 130, 'ACTIVE', 3),
     (1006, '2024-11-01', '2027-11-01', 400, 400, 'ACTIVE', 4),
     (1007, '2024-05-05', '2026-05-05', 600, 500, 'ACTIVE', 5);

INSERT INTO stock_entries (entry_date, quantity, supplier, batch_id) VALUES
     ('2024-01-15', 500, 'PharmaCorp', 1),
     ('2024-03-20', 300, 'PharmaCorp', 2),
     ('2024-06-10', 200, 'MediSupply', 3),
     ('2024-08-15', 1000, 'MediSupply', 4),
     ('2024-02-25', 150, 'ColdChain Logistics', 5),
     ('2024-11-05', 400, 'GlobalPharma', 6),
     ('2024-05-10', 600, 'GlobalPharma', 7);

INSERT INTO stock_exits (exit_date, quantity, exit_reason, batch_id) VALUES
     ('2024-09-01', 50,  'DISPENSED', 1),
     ('2024-10-15', 30,  'DISPENSED', 2),
     ('2024-07-01', 200, 'EXPIRED',   3),
     ('2024-12-01', 50,  'DISPENSED', 4),
     ('2025-01-10', 20,  'DISPENSED', 5),
     ('2025-02-01', 100, 'DISPENSED', 7);

INSERT INTO expiration_alerts (alert_date, days_until_expiration, expiration_alert_status, batch_id) VALUES
     ('2026-03-01', 92,  'PENDING', 1),
     ('2026-02-15', 59,  'PENDING', 2),
     ('2026-02-18', 30,  'NOTIFIED', 5),
     ('2026-04-05', 426, 'PENDING', 6);