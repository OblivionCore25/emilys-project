import React from 'react';
import { BrowserRouter as Router, Routes, Route, Link, useNavigate, useParams } from 'react-router-dom';
import { ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import { AuthProvider, useAuth } from './context/AuthContext';
import DrainList from './components/DrainList';
import DrainDetail from './components/DrainDetail';
import DrainForm from './components/DrainForm';
import Login from './components/Login';
import Register from './components/Register';
import Notifications from './components/Notifications';
import API_BASE_URL from './config/api';
import './components/DrainStyles.css';

function Navigation() {
  const { user, logout, getToken, isAdmin } = useAuth();
  const navigate = useNavigate();
  const [unreadCount, setUnreadCount] = React.useState(0);

  React.useEffect(() => {
    const fetchUnreadCount = async () => {
      if (!isAdmin()) return;
      
      try {
        const token = getToken();
        const response = await fetch(`${API_BASE_URL}/api/notifications/unread-count`, {
          headers: {
            'Authorization': `Bearer ${token}`
          }
        });
        if (response.ok) {
          const data = await response.json();
          setUnreadCount(data.count);
        }
      } catch (error) {
        console.error('Error fetching unread count:', error);
      }
    };

    if (user && isAdmin()) {
      fetchUnreadCount();
      // Poll every 30 seconds for new notifications
      const interval = setInterval(fetchUnreadCount, 30000);
      return () => clearInterval(interval);
    }
  }, [user, isAdmin, getToken]);

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <header className="app-header">
      <div className="header-content">
        <Link to="/" className="header-title">
          <h1>Drain Adoption Program</h1>
        </Link>
        <nav className="header-nav">
          {user ? (
            <>
              {isAdmin() && (
                <Link to="/notifications" className="notification-bell">
                  🔔
                  {unreadCount > 0 && (
                    <span className="notification-badge">{unreadCount}</span>
                  )}
                </Link>
              )}
              <span className="user-info">
                {user.name} ({user.role})
              </span>
              <button onClick={handleLogout} className="logout-button">
                Logout
              </button>
            </>
          ) : (
            <>
              <Link to="/login" className="nav-link">Login</Link>
              <Link to="/register" className="nav-link">Register</Link>
            </>
          )}
        </nav>
      </div>
    </header>
  );
}

function DrainFormWrapper() {
  const { id } = useParams();
  const [existingDrain, setExistingDrain] = React.useState(null);
  const [loading, setLoading] = React.useState(true);

  React.useEffect(() => {
    const fetchDrain = async () => {
      try {
        const response = await fetch(`${API_BASE_URL}/api/drains/${id}`);
        if (!response.ok) throw new Error('Failed to fetch drain');
        const data = await response.json();
        setExistingDrain(data);
      } catch (error) {
        console.error('Error fetching drain:', error);
      } finally {
        setLoading(false);
      }
    };

    if (id) {
      fetchDrain();
    }
  }, [id]);

  if (loading) {
    return <div style={{ padding: '40px', textAlign: 'center' }}>Loading drain data...</div>;
  }

  return <DrainForm drainId={id} existingDrain={existingDrain} />;
}

function App() {
  return (
    <AuthProvider>
      <Router>
        <div className="App">
          <Navigation />
          <main>
            <Routes>
              <Route path="/" element={<DrainList />} />
              <Route path="/drains/new" element={<DrainForm />} />
              <Route path="/drains/:id/edit" element={<DrainFormWrapper />} />
              <Route path="/drains/:id" element={<DrainDetail />} />
              <Route path="/login" element={<Login />} />
              <Route path="/register" element={<Register />} />
              <Route path="/notifications" element={<Notifications />} />
            </Routes>
          </main>
          <ToastContainer position="top-right" autoClose={3000} />
        </div>
      </Router>
    </AuthProvider>
  );
}

export default App;