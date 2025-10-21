## 🔍 **Appointment Form Debugging Guide**

### **STEP-BY-STEP TESTING**

#### 1. **Login Process**
```
URL: http://localhost:8083/login
Username: jim.b@gmail.com
Password: admin123
```

#### 2. **Access Appointment Form**  
```
URL: http://localhost:8083/dentalsurgeryapp/rolebase/patient/appointments/new
```

#### 3. **What You Should See**
- ✅ Form with dentist dropdown populated
- ✅ Date field (minimum today's date)
- ✅ Time selection dropdown
- ✅ Reason textarea
- ✅ Urgency dropdown (default: Medium)

#### 4. **Common Error Scenarios**

**A. If you see "Access Denied" or redirect to login:**
- Issue: Not logged in or wrong role
- Solution: Login with patient credentials above

**B. If dentist dropdown is empty:**
- Issue: No dentists in database or service error
- Check: Application logs for errors

**C. If form validation fails:**
- Issue: Required fields not filled
- Required fields: Dentist, Date, Time, Reason

**D. If JavaScript errors in browser console:**
- Issue: Client-side validation problems
- Check: Browser developer tools console

### **SUCCESSFUL FORM SUBMISSION SHOULD:**
1. Create appointment in database
2. Redirect to: `/dentalsurgeryapp/rolebase/patient/appointments`
3. Show success message: "Appointment booked successfully!"
4. Display new appointment in list

### **VALIDATION TESTS TO TRY:**

**Test 1: Empty Form Submission**
- Submit without filling any fields
- Should see client-side validation errors

**Test 2: Past Date Selection**
- Try selecting yesterday's date
- Should be prevented by date input min attribute

**Test 3: Missing Required Fields**
- Fill some fields but not others
- Should see specific field validation errors

**Test 4: Valid Submission**
- Fill all required fields with valid data
- Should successfully create appointment

### **DEBUG INFORMATION TO CHECK:**

1. **Browser Network Tab**: Check for 403/500 errors
2. **Application Logs**: Look for Java exceptions  
3. **Database**: Verify dentists exist in dentists table
4. **Session**: Ensure user is authenticated with PATIENT role

If you're still encountering issues, please share:
1. The specific error message you see
2. Browser console errors (F12 → Console)
3. Any server-side errors from logs