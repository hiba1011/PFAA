-- Fix the justification column type to properly store binary image data
-- This script changes the justification column from TEXT to LONGBLOB

USE gestion_universitaire;

-- First, backup existing data (optional but recommended)
-- CREATE TABLE abscences_backup AS SELECT * FROM abscences;

-- Modify the justification column to LONGBLOB
ALTER TABLE abscences MODIFY COLUMN justification LONGBLOB;

-- Verify the change
DESCRIBE abscences; 