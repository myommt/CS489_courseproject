# Appointment Booking Form Testing Guide

## 🔍 Common Issues and Solutions

### Issue 1: Access Denied / Redirect to Login
**Problem**: Accessing `http://localhost:8083/dentalsurgeryapp/rolebase/patient/appointments/new` redirects to login
**Solution**: Login as a patient user first

### Issue 2: Form Validation Errors
**Problem**: Form submission fails with validation errors
**Solution**: The form has comprehensive validation. Check these fields:

1. **Dentist Selection**: Must select a dentist from dropdown
2. **Appointment Date**: Must be today or future date
3. **Appointment Time**: Must select a time slot
4. **Reason**: Must provide reason for visit

## 🧪 Testing Steps

### Step 1: Login Process
1. Go to: `http://localhost:8083/login`
2. Use patient credentials from data.sql:
   - Username: `patient1` (or check your data.sql file)
   - Password: `password123` (or check your data.sql file)

### Step 2: Navigate to Appointment Form
1. After successful login, go to: `http://localhost:8083/dentalsurgeryapp/rolebase/patient/appointments/new`
2. You should see the appointment booking form

### Step 3: Test Form Validation
Try submitting empty form - should see:
- "Please select a dentist" error
- "Please select an appointment date" error  
- "Please select an appointment time" error
- "Please provide a reason for your visit" error

### Step 4: Test Valid Submission
1. Select a dentist
2. Choose today or future date
3. Select a time slot
4. Enter reason for visit
5. Submit form - should create appointment successfully

## 🔧 Form Features

### Client-Side Validation
- Prevents submission of incomplete forms
- Validates date is not in past
- Shows immediate feedback to user

### Server-Side Validation  
- Validates all required fields
- Checks dentist exists
- Ensures appointment date/time are valid
- Provides specific error messages

### Error Handling
- Bootstrap styling for validation feedback
- Specific error messages for each field
- Form preservation on validation failure

## 🚨 Troubleshooting

If you're still seeing errors:

1. **Check browser console** for JavaScript errors
2. **Check application logs** for server-side errors
3. **Verify user authentication** - make sure you're logged in as PATIENT role
4. **Check database connection** - ensure dentists exist in database
5. **Test with valid data** - use future dates and required fields

## 📋 Expected Behavior

**Valid Form Submission Should:**
- Create new appointment in database
- Redirect to appointments list with success message
- Show appointment in patient's appointment history

**Invalid Form Submission Should:**
- Stay on form page
- Display specific error messages  
- Preserve user input for correction
- Highlight fields with errors