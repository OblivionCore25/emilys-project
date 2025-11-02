import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { toast, ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';

const DrainDetail = () => {
  const [drain, setDrain] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [isEditing, setIsEditing] = useState(false);
  const [editForm, setEditForm] = useState({ name: '', imageUrl: '' });
  const { id } = useParams();
  const navigate = useNavigate();
  
  // In a real app, this would come from authentication
  const currentUserId = 1;

  useEffect(() => {
    fetchDrainDetails();
  }, [id]);

  const fetchDrainDetails = async () => {
    try {
      const response = await fetch(`http://localhost:8080/api/drains/${id}`);
      if (!response.ok) {
        throw new Error('Failed to fetch drain details');
      }
      const data = await response.json();
      setDrain(data);
      setLoading(false);
    } catch (err) {
      setError(err.message);
      setLoading(false);
    }
  };

  const adoptDrain = async () => {
    try {
      const response = await fetch(`http://localhost:8080/api/drains/${id}/adopt?userId=${currentUserId}`, {
        method: 'POST',
      });
      
      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.message || 'Failed to adopt drain');
      }
      
      toast.success('Successfully adopted the drain!');
      fetchDrainDetails();
    } catch (err) {
      toast.error(err.message);
    }
  };

  const updateDrain = async (e) => {
    e.preventDefault();
    try {
      const response = await fetch(`http://localhost:8080/api/drains/${id}?userId=${currentUserId}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(editForm),
      });

      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.message || 'Failed to update drain');
      }

      toast.success('Successfully updated the drain!');
      setIsEditing(false);
      fetchDrainDetails();
    } catch (err) {
      toast.error(err.message);
    }
  };

  const startEditing = () => {
    setEditForm({
      name: drain.name,
      imageUrl: drain.imageUrl
    });
    setIsEditing(true);
  };

  if (loading) return <div>Loading drain details...</div>;
  if (error) return <div>Error: {error}</div>;
  if (!drain) return <div>No drain found</div>;

  return (
    <div className="drain-detail">
      <h2>{drain.name}</h2>
      <div className="drain-info">
        <img src={drain.imageUrl} alt={drain.name} className="drain-image" />
        <div className="map-section">
          <div className="map-container">
            <iframe
              width="100%"
              height="300"
              frameBorder="0"
              style={{ border: 0, borderRadius: '8px' }}
              src={`https://www.google.com/maps/embed/v1/place?key=${import.meta.env.VITE_GOOGLE_MAPS_API_KEY}&q=${drain.latitude},${drain.longitude}&zoom=17`}
              allowFullScreen
              loading="lazy"
              referrerPolicy="no-referrer-when-downgrade"
            />
          </div>
          <div className="map-links">
            <a 
              href={`https://www.google.com/maps/search/?api=1&query=${drain.latitude},${drain.longitude}`}
              target="_blank"
              rel="noopener noreferrer"
              className="google-maps-link"
            >
              <img 
                src="https://maps.gstatic.com/mapfiles/maps_lite/images/2x/ic_plus_code.png" 
                alt="Google Maps Icon" 
                className="maps-icon"
              />
              Open in Google Maps
            </a>
            <div className="coordinates">
              <span>📍 {drain.latitude.toFixed(6)}, {drain.longitude.toFixed(6)}</span>
            </div>
          </div>
        </div>
        <div className="drain-status">
          <p>Status: {drain.adoptedByUserId ? 'Adopted' : 'Available'}</p>
        </div>
        {!drain.adoptedByUserId && (
          <button 
            onClick={adoptDrain}
            className="adopt-button"
          >
            Adopt this Drain
          </button>
        )}
      </div>
    </div>
  );
};

export default DrainDetail;