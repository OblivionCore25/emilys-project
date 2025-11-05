# Google Maps Location Picker Setup

This application now includes an interactive Google Maps location picker that allows users to:
- 🗺️ Click on the map to select a location
- 🔍 Search for addresses or places
- 📍 Use their current location automatically
- 🏠 View reverse-geocoded addresses

## Setup Instructions (5 minutes)

### Step 1: Get a Google Maps API Key

1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Create a new project or select an existing one
3. Enable the following APIs:
   - **Maps JavaScript API**
   - **Places API**
   - **Geocoding API**

#### Detailed Steps:

**A. Enable APIs:**
1. Click on "Navigation Menu" (☰) → "APIs & Services" → "Library"
2. Search for "Maps JavaScript API" and click "Enable"
3. Search for "Places API" and click "Enable"
4. Search for "Geocoding API" and click "Enable"

**B. Create API Key:**
1. Go to "APIs & Services" → "Credentials"
2. Click "+ CREATE CREDENTIALS" → "API Key"
3. Copy your API key (e.g., `AIzaSyC...`)

**C. Restrict Your API Key (Recommended for Production):**
1. Click on your API key to edit it
2. Under "Application restrictions":
   - For development: Select "None"
   - For production: Select "HTTP referrers" and add your domain
3. Under "API restrictions":
   - Select "Restrict key"
   - Choose: Maps JavaScript API, Places API, Geocoding API
4. Click "Save"

### Step 2: Update Your .env File

Open `drain-adoption-frontend/.env` and ensure you have:

```env
REACT_APP_GOOGLE_MAPS_API_KEY=YOUR_API_KEY_HERE
```

**Example:**
```env
REACT_APP_GOOGLE_MAPS_API_KEY=AIzaSyCOQhpeKWT_xQAFlGKKd0PSvwQwNKekN08
```

### Step 3: Restart Your Frontend

```bash
cd drain-adoption-frontend
npm start
```

**Important:** React requires a restart to pick up new environment variables.

---

## Features

### 🗺️ Interactive Map
- Click anywhere on the map to set a drain location
- Drag the map to explore different areas
- Zoom in/out for precision

### 🔍 Address Search
- Type any address or place name
- Get autocomplete suggestions as you type
- Instantly jump to the location

### 📍 Current Location
- Click "Use My Current Location" button
- Browser will request location permission
- Automatically centers map on your position

### 🏠 Reverse Geocoding
- Selected coordinates are converted to human-readable addresses
- Displays formatted address below the map
- Shows exact latitude and longitude

---

## Free Tier Limits

Google Maps API offers generous free usage:

- **$200 free credit per month**
- That's approximately:
  - 28,000 map loads per month
  - 40,000 geocoding requests per month
  - 28,000 autocomplete requests per month

Perfect for development and small-to-medium production apps!

---

## Troubleshooting

### "Google Maps API key is missing"
- Check that `.env` has `REACT_APP_GOOGLE_MAPS_API_KEY=your_key`
- Restart your development server
- Ensure the key starts with `REACT_APP_`

### Map doesn't load / "This page can't load Google Maps correctly"
- Verify your API key is valid
- Ensure Maps JavaScript API is enabled in Google Cloud Console
- Check browser console for specific error messages
- Verify API key restrictions (should allow your localhost during development)

### Search box doesn't work
- Ensure Places API is enabled
- Check API key restrictions
- Look for errors in browser console

### "Geocoding error" or address not showing
- Ensure Geocoding API is enabled
- Some locations may not have full address data

### "Unable to get your location"
- Browser needs location permission
- HTTPS is required in production (not localhost)
- Some browsers block location on insecure connections

---

## Testing the Location Picker

1. Navigate to **Create New Drain** or **Edit Drain** (admin only)
2. Scroll to the "Drain Location" section
3. Try all three methods:
   - **Click** directly on the map
   - **Search** for "Central Park, New York"
   - **Use** the "Use My Current Location" button
4. Verify the coordinates and address appear below the map
5. Submit the form to save the drain with the location

---

## Comparison: Old vs New

### Before (Manual Entry) ❌
- Users had to know exact coordinates
- Error-prone typing
- No visual feedback
- No address validation
- Poor user experience

### After (Interactive Map) ✅
- Visual map interface
- Click to select
- Address search
- Auto-detect location
- Real-time feedback
- Human-readable addresses
- Much better UX!

---

## Production Considerations

1. **API Key Security**:
   - Restrict key to your domain
   - Enable only required APIs
   - Monitor usage in Google Cloud Console

2. **Performance**:
   - Maps are loaded once per form (efficient)
   - Autocomplete queries are debounced
   - Reverse geocoding is cached

3. **Fallback**:
   - Component gracefully handles missing API key
   - Shows error message with instructions
   - Form still submits if location is set

4. **Mobile**:
   - Fully responsive design
   - Touch-friendly controls
   - Works with device location services

---

## Next Steps (Optional Enhancements)

1. **Drawing Tools**: Allow users to draw drain coverage areas
2. **Satellite View**: Toggle between map and satellite imagery
3. **Street View**: Preview location at street level
4. **Nearby Search**: Find nearby drains automatically
5. **Route Planning**: Calculate routes between drains
6. **Cluster Markers**: Group nearby drains on overview map

Need help? Check the [Google Maps JavaScript API Documentation](https://developers.google.com/maps/documentation/javascript)
