# Security Setup Guide

This guide explains how to set up the application with your own credentials.

## ⚠️ Important Security Notes

**Never commit these files to git:**
- `application.properties` (backend)
- `.env` (frontend)
- Any file containing API keys, passwords, or secrets

These files are now in `.gitignore` and should remain private.

---

## Backend Setup (Spring Boot)

### 1. Configure Database Connection

Copy the example file:
```bash
cd drain-adoption/src/main/resources
cp application.properties.example application.properties
```

Edit `application.properties` and replace the placeholders:

```properties
# PostgreSQL Configuration
spring.datasource.url=jdbc:postgresql://your-host:5432/your-database?sslmode=require&user=your-username&password=your-password
spring.datasource.username=your-database-username
spring.datasource.password=your-database-password

# JWT Configuration
jwt.secret=your-super-secret-jwt-key-change-this-in-production
jwt.expiration=86400000
```

**To generate a secure JWT secret:**
```bash
openssl rand -base64 64
```

### 2. Database Options

**Option A: Prisma Postgres (Recommended for Development)**
1. Sign up at https://www.prisma.io/postgres
2. Create a new database
3. Copy the connection string from the dashboard
4. Paste it into `spring.datasource.url`

**Option B: Local PostgreSQL**
1. Install PostgreSQL
2. Create a database
3. Update the connection string with your local credentials

---

## Frontend Setup (React)

### 1. Configure Environment Variables

Copy the example file:
```bash
cd drain-adoption-frontend
cp .env.example .env
```

Edit `.env` and add your API keys:

```env
# Google Maps API Key
REACT_APP_GOOGLE_MAPS_API_KEY=your_google_maps_api_key_here

# Cloudinary Configuration
REACT_APP_CLOUDINARY_CLOUD_NAME=your_cloud_name_here
REACT_APP_CLOUDINARY_UPLOAD_PRESET=your_upload_preset_here
```

### 2. Get Google Maps API Key

1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Create a new project or select an existing one
3. Enable these APIs:
   - Maps JavaScript API
   - Places API
   - Geocoding API
4. Create an API key in "Credentials"
5. Copy the key to `.env`

**Free Tier:** $200/month credit (plenty for development)

See `GOOGLE_MAPS_SETUP.md` for detailed instructions.

### 3. Get Cloudinary Credentials

1. Sign up at [Cloudinary](https://cloudinary.com/users/register/free)
2. Go to Dashboard
3. Copy your **Cloud Name**
4. Create an **unsigned upload preset**:
   - Settings > Upload > Add upload preset
   - Set to "Unsigned" mode
   - Name it (e.g., `drains_unsigned`)
5. Add both to `.env`

**Free Tier:** 25 GB storage + 25 GB bandwidth/month

See `CLOUDINARY_SETUP.md` for detailed instructions.

---

## After Configuration

### Backend
```bash
cd drain-adoption
./mvnw clean install
./mvnw spring-boot:run
```

Backend will start on: http://localhost:8080

### Frontend
```bash
cd drain-adoption-frontend
npm install
npm start
```

Frontend will start on: http://localhost:3000

---

## Creating Admin Users

**IMPORTANT:** The public registration endpoint only creates ADOPTER users. Admin users must be created through secure methods.

### Method 1: Create First Admin (Initial Setup)

⚠️ **Use this immediately after deployment when no admin users exist**

**Using curl:**
```bash
curl -X POST http://localhost:8080/api/admin/create-first-admin \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Admin User",
    "email": "admin@example.com",
    "password": "secure_password_here"
  }'
```

**Using Postman:**
1. Method: `POST`
2. URL: `http://localhost:8080/api/admin/create-first-admin`
3. Headers: `Content-Type: application/json`
4. Body (raw JSON):
```json
{
  "name": "Admin User",
  "email": "admin@example.com",
  "password": "secure_password_here"
}
```

**Security Notes:**
- This endpoint only works when **no admin users exist** in the database
- Once an admin exists, this endpoint returns a 403 Forbidden error
- Use a strong password for the first admin
- After creating the first admin, use Method 2 or 3 for additional admins

### Method 2: Promote Existing User (Requires Admin Authentication)

Once you have an admin user, you can promote existing adopter users to admin:

**Steps:**
1. User registers normally (becomes ADOPTER)
2. Admin logs in and gets JWT token
3. Admin calls promotion endpoint

**Using curl:**
```bash
curl -X PUT http://localhost:8080/api/admin/promote/{userId} \
  -H "Authorization: Bearer YOUR_JWT_TOKEN_HERE"
```

**Using Postman:**
1. Method: `PUT`
2. URL: `http://localhost:8080/api/admin/promote/123` (replace 123 with user ID)
3. Headers: `Authorization: Bearer YOUR_JWT_TOKEN_HERE`

**To get the JWT token:**
- Login as admin through `/api/auth/login`
- Copy the `token` from the response
- Use it in the Authorization header

### Method 3: Direct Database Insert (Emergency/Development Only)

If you need to create an admin directly in the database:

**PostgreSQL:**
```sql
-- Generate password hash (use BCrypt online tool or backend encoder)
-- Example: BCrypt hash of "admin123" with cost 10

INSERT INTO users (name, email, password, role) 
VALUES (
  'Emergency Admin',
  'emergency@example.com',
  '$2a$10$your_bcrypt_hashed_password_here',
  'ADMIN'
);
```

**⚠️ Use this method only for:**
- Emergency recovery situations
- Development/testing environments
- When Method 1 and 2 are not available

**Generate BCrypt hash:**
- Online: https://bcrypt-generator.com/ (cost factor 10)
- Or use backend code to generate the hash

### Security Best Practices for Admin Creation

✅ **DO:**
- Create the first admin immediately after deployment
- Use strong, unique passwords for admin accounts
- Document who has admin access
- Limit the number of admin users
- Consider disabling the `/create-first-admin` endpoint after initial setup
- Use Method 2 (promotion) for routine admin creation

❌ **DON'T:**
- Leave the application without an admin user
- Share admin credentials
- Use weak or default passwords
- Grant admin access unnecessarily
- Use Method 3 in production environments

### Verifying Admin Access

After creating an admin user:

1. **Login:**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@example.com",
    "password": "your_password"
  }'
```

2. **Check the response:**
```json
{
  "token": "eyJhbGc...",
  "userId": 1,
  "email": "admin@example.com",
  "name": "Admin User",
  "role": "ADMIN"  // ← Should be "ADMIN"
}
```

3. **Test admin privileges:**
```bash
# Try creating a drain (admin-only operation)
curl -X POST http://localhost:8080/api/drains \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{...drain data...}'
```

---

## Team Setup

When a new team member joins:

1. **DO NOT** share `application.properties` or `.env` files directly
2. Share the example files instead:
   - `application.properties.example`
   - `.env.example`
3. Help them set up their own credentials
4. Each developer should have their own:
   - Database instance (or shared dev database)
   - Google Maps API key
   - Cloudinary account

---

## Production Deployment

For production, use environment variables instead of files:

### Backend (Spring Boot)
```bash
export SPRING_DATASOURCE_URL="jdbc:postgresql://..."
export SPRING_DATASOURCE_USERNAME="..."
export SPRING_DATASOURCE_PASSWORD="..."
export JWT_SECRET="..."
export JWT_EXPIRATION="86400000"
```

### Frontend (React)
Set environment variables in your hosting platform:
- Vercel: Project Settings > Environment Variables
- Netlify: Site Settings > Build & Deploy > Environment
- AWS: Use AWS Secrets Manager

---

## Security Best Practices

✅ **DO:**
- Use strong, random JWT secrets (64+ characters)
- Rotate secrets regularly
- Use environment-specific credentials
- Enable database SSL connections
- Restrict API keys to specific domains (production)
- Use `.env.local` for personal overrides

❌ **DON'T:**
- Commit secrets to git
- Share credentials via Slack/email
- Use the same secrets in dev and production
- Use weak or predictable JWT secrets
- Leave API keys unrestricted

---

## Troubleshooting

### "application.properties not found"
Copy `application.properties.example` to `application.properties`

### "Google Maps API key is missing"
Add `REACT_APP_GOOGLE_MAPS_API_KEY` to `.env` file

### "Cloudinary configuration is missing"
Add Cloudinary settings to `.env` file

### Backend won't start
Check database connection string and credentials

---

## Need Help?

- Google Maps Setup: See `GOOGLE_MAPS_SETUP.md`
- Cloudinary Setup: See `CLOUDINARY_SETUP.md`
- Database issues: Check connection string format
- API key issues: Ensure APIs are enabled in Google Cloud Console
