import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { toast } from 'react-toastify';
import API_BASE_URL from '../config/api';

const DrainList = () => {
  const [drains, setDrains] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const { isAdmin, getToken, user, getUserId } = useAuth();

  useEffect(() => {
    fetchDrains();
  }, []);

  const fetchDrains = async () => {
    try {
      const response = await fetch(`${API_BASE_URL}/api/drains`);
      if (!response.ok) {
        throw new Error('Failed to fetch drains');
      }
      const data = await response.json();
      setDrains(data);
      setLoading(false);
    } catch (err) {
      setError(err.message);
      setLoading(false);
    }
  };

  const handleDelete = async (drainId, drainName) => {
    if (!window.confirm(`Are you sure you want to delete "${drainName}"?`)) {
      return;
    }

    try {
      const token = getToken();
      const response = await fetch(`${API_BASE_URL}/api/drains/${drainId}`, {
        method: 'DELETE',
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });

      if (!response.ok) {
        throw new Error('Failed to delete drain');
      }

      toast.success('Drain deleted successfully');
      fetchDrains(); // Refresh the list
    } catch (error) {
      toast.error(error.message);
    }
  };

  if (loading) return <div>Loading drains...</div>;
  if (error) return <div>Error: {error}</div>;

  return (
    <div className="drain-list">
      <div className="drain-list-header">
        <h2>Available Drains</h2>
        {isAdmin() && (
          <Link to="/drains/new" className="create-button">
            + Create New Drain
          </Link>
        )}
      </div>
      <div className="drain-grid">
        {drains.map(drain => (
          <div key={drain.id} className="drain-card">
            <img 
              src={drain.imageUrl} 
              alt={drain.name} 
              className="drain-image"
            />
            <h3>{drain.name}</h3>
            <p className={`drain-status ${drain.adoptedByUserId && user && drain.adoptedByUserId === user.userId ? 'adopted-by-you' : ''}`}>
              Status: {
                drain.adoptedByUserId 
                  ? (user && drain.adoptedByUserId === user.userId 
                      ? '✅ Adopted by You' 
                      : '🔒 Adopted')
                  : '🆓 Available'
              }
            </p>
            <div className="card-actions">
              <Link to={`/drains/${drain.id}`} className="view-button">
                View Details
              </Link>
              {isAdmin() && (
                <>
                  <Link to={`/drains/${drain.id}/edit`} className="edit-button">
                    Edit
                  </Link>
                  <button 
                    onClick={() => handleDelete(drain.id, drain.name)}
                    className="delete-button"
                  >
                    Delete
                  </button>
                </>
              )}
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default DrainList;