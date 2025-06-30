# Solution for MySQL Data Truncation Error

## Problem Description
You're encountering a MySQL data truncation error when trying to upload image files as justifications for absences:

```
com.mysql.cj.jdbc.exceptions.MysqlDataTruncation: Data truncation: Incorrect string value: '\x89PNG\x0D\x0A...' for column `gestion_universitaire`.`abscences`.`justification` at row 1
```

## Root Cause
The `justification` column in the `abscences` table is currently defined as a `TEXT` or `VARCHAR` type, but the application is trying to store binary image data (PNG files) into it. MySQL is attempting to convert the binary data to a string, which fails because PNG files contain non-printable characters.

## Solution

### Step 1: Fix Database Schema
Run the following SQL script to change the column type to `LONGBLOB`:

```sql
-- Fix the justification column type to properly store binary image data
USE gestion_universitaire;

-- Modify the justification column to LONGBLOB
ALTER TABLE abscences MODIFY COLUMN justification LONGBLOB;

-- Verify the change
DESCRIBE abscences;
```

### Step 2: Code Fixes Applied
I've already fixed the following issues in the `AbscenceService.java` file:

1. **Fixed hardcoded table names**: Updated `updateJustificatif`, `validerJustificatif`, and `getAbsencesByClasse` methods to use the proper constants (`DATABASE_SCHEMA` and `ABSCENCES_TABLE`) instead of hardcoded table names.

2. **Consistent table naming**: All methods now use the same table reference pattern.

### Step 3: Verification
After applying the database changes, the application should work correctly. The `updateJustificatif` method will be able to store binary image data without truncation errors.

## Technical Details

### Why LONGBLOB?
- `LONGBLOB` can store up to 4GB of binary data
- Perfect for storing image files, PDFs, and other binary documents
- Maintains data integrity without character encoding issues

### Code Changes Made
1. **Line 333 in AbscenceService.java**: Fixed SQL query to use proper table constants
2. **validerJustificatif method**: Fixed SQL query to use proper table constants  
3. **getAbsencesByClasse method**: Fixed SQL query to use proper table constants

### Testing
After applying these changes:
1. Run the SQL script to modify the database
2. Test uploading an image justification in the student dashboard
3. Verify the image can be viewed by teachers and administrators

## Alternative Solutions (if needed)
If you prefer not to change the database schema, you could:
1. Store images as Base64 strings (not recommended for large files)
2. Store file paths and keep images in the file system
3. Use a separate table for binary data

However, the `LONGBLOB` approach is the most efficient and secure solution for this use case. 