-- Add missing details column to audit_logs table
ALTER TABLE audit_logs ADD COLUMN details TEXT;