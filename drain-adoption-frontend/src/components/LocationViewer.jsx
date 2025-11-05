import { useMemo, useState, useEffect } from 'react';
import { GoogleMap, useLoadScript, Marker } from '@react-google-maps/api';
import './LocationViewer.css';

const LocationViewer = ({ latitude, longitude, name }) => {
  const libraries = useMemo(() => ['places'], []);
  const [address, setAddress] = useState('');
  const [loadingAddress, setLoadingAddress] = useState(true);
  
  const { isLoaded, loadError } = useLoadScript({
    googleMapsApiKey: process.env.REACT_APP_GOOGLE_MAPS_API_KEY,
    libraries: libraries,
  });

  // Fetch address using reverse geocoding
  useEffect(() => {
    if (isLoaded && latitude && longitude) {
      const geocoder = new window.google.maps.Geocoder();
      geocoder.geocode(
        { location: { lat: latitude, lng: longitude } },
        (results, status) => {
          if (status === 'OK' && results[0]) {
            setAddress(results[0].formatted_address);
          } else {
            setAddress('Address not available');
          }
          setLoadingAddress(false);
        }
      );
    }
  }, [isLoaded, latitude, longitude]);

  const mapContainerStyle = {
    width: '100%',
    height: '100%',
    borderRadius: '8px'
  };

  const center = {
    lat: latitude,
    lng: longitude
  };

  const mapOptions = {
    disableDefaultUI: false,
    zoomControl: true,
    streetViewControl: true,
    mapTypeControl: true,
    fullscreenControl: true,
  };

  if (loadError) {
    return (
      <div className="location-viewer-error">
        <p>⚠️ Error loading map</p>
        <p className="error-hint">Unable to load Google Maps</p>
      </div>
    );
  }

  if (!isLoaded) {
    return (
      <div className="location-viewer-loading">
        <p>Loading map...</p>
      </div>
    );
  }

  return (
    <div className="location-viewer-wrapper">
      <div className="location-viewer">
        <GoogleMap
          mapContainerStyle={mapContainerStyle}
          center={center}
          zoom={16}
          options={mapOptions}
        >
          <Marker
            position={center}
            title={name}
            animation={window.google?.maps?.Animation?.DROP}
          />
        </GoogleMap>
      </div>
      <div className="location-details">
        <div className="location-coordinates">
          <strong>📍 Coordinates:</strong>
          <span>Lat: {latitude.toFixed(6)}, Lng: {longitude.toFixed(6)}</span>
        </div>
        <div className="location-address">
          <strong>🏠 Address:</strong>
          <span>
            {loadingAddress ? 'Loading address...' : address}
          </span>
        </div>
      </div>
    </div>
  );
};

export default LocationViewer;
