# Cloudinary Image Upload Setup Instructions

This application uses Cloudinary for secure, scalable cloud-based image storage and delivery.

## Quick Setup (5 minutes)

### Step 1: Create a Free Cloudinary Account

1. Go to [https://cloudinary.com/users/register/free](https://cloudinary.com/users/register/free)
2. Sign up with your email or Google account
3. Verify your email address

**Free Tier Includes:**
- 25 GB storage
- 25 GB bandwidth per month
- Image transformations and optimization
- Perfect for development and small production apps

### Step 2: Get Your Cloud Name

1. After logging in, go to your **Dashboard**
2. Find your **Cloud Name** in the "Account Details" section (top-left)
3. Copy this value (e.g., `dxyz12345`)

### Step 3: Create an Unsigned Upload Preset

**Important:** We use "unsigned" uploads so users can upload images directly from the browser without exposing API secrets.

1. Click the **Settings** icon (⚙️) in the top-right
2. Navigate to: **Upload** tab
3. Scroll down to **Upload presets** section
4. Click **Add upload preset**
5. Configure the preset:
   - **Preset name**: `drains_unsigned` (or any name you prefer)
   - **Signing Mode**: Select **Unsigned** ⚠️ (Very Important!)
   - **Folder**: `drains` (optional, helps organize images)
   - **Format**: Leave as Auto
   - **Access mode**: Public (images need to be publicly accessible)
6. Click **Save**

### Step 4: Update Your .env File

1. Open `drain-adoption-frontend/.env`
2. Replace the placeholder values:

```env
REACT_APP_CLOUDINARY_CLOUD_NAME=your_cloud_name_here
REACT_APP_CLOUDINARY_UPLOAD_PRESET=drains_unsigned
```

**Example:**
```env
REACT_APP_CLOUDINARY_CLOUD_NAME=dxyz12345
REACT_APP_CLOUDINARY_UPLOAD_PRESET=drains_unsigned
```

### Step 5: Restart Your Frontend Server

```bash
cd drain-adoption-frontend
npm start
```

**Note:** React requires a restart to pick up new environment variables.

---

## Testing the Upload

1. Navigate to **Create New Drain** (admin user required)
2. Click the image upload area
3. Select an image from your computer or phone
4. You should see:
   - Image preview
   - "Uploading to cloud storage..." message
   - Success notification when complete
5. The image is now stored on Cloudinary's CDN!

---

## Features Included

✅ **Direct Browser Upload**: Images upload from user's device to Cloudinary (no backend file handling)  
✅ **File Validation**: Type checking (images only) and size limits (10MB max)  
✅ **Image Preview**: Instant preview before and after upload  
✅ **Progress Indicator**: Visual feedback during upload  
✅ **Error Handling**: Clear error messages for upload failures  
✅ **Mobile Support**: Works with phone cameras and photo galleries  
✅ **Change/Remove**: Easy to swap or remove images  
✅ **CDN Delivery**: Fast image loading worldwide  

---

## Security Notes

- **Unsigned presets** are safe for public apps because:
  - They only allow uploads (not deletions or modifications)
  - You can set folder restrictions
  - You can limit file types and sizes in Cloudinary settings
  - Each upload is tracked in your Cloudinary dashboard

- **For production apps**, consider:
  - Adding rate limiting in Cloudinary settings
  - Setting up moderation rules
  - Implementing backend validation

---

## Troubleshooting

### "Cloudinary configuration is missing"
- Ensure `.env` file has both `REACT_APP_CLOUDINARY_CLOUD_NAME` and `REACT_APP_CLOUDINARY_UPLOAD_PRESET`
- Restart your frontend server after updating `.env`

### "Upload failed"
- Verify your upload preset is set to **Unsigned** mode
- Check that the cloud name matches your dashboard
- Ensure the preset name is typed correctly (case-sensitive)

### "Image size must be less than 10MB"
- The frontend limits uploads to 10MB
- You can adjust this in `ImageUpload.jsx` (line 25)

### Images not displaying
- Ensure the upload preset has **Access mode** set to **Public**
- Check browser console for CORS errors

---

## Next Steps (Optional Enhancements)

1. **Image Transformations**: Automatically resize/optimize images
2. **Direct Camera Access**: Enable camera capture on mobile
3. **Drag & Drop**: Add drag-and-drop upload area
4. **Multiple Images**: Allow gallery of images per drain
5. **AI Tagging**: Auto-tag images with Cloudinary AI

Need help? Check the [Cloudinary Documentation](https://cloudinary.com/documentation)
