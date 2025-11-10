import React from 'react';
import { useNavigate } from 'react-router-dom';
import './Home.css';

const Home = () => {
  const navigate = useNavigate();

  return (
    <div className="home-container">
      {/* Hero Section */}
      <section className="hero-section">
        <div className="hero-overlay">
          <div className="hero-content">
            <h1 className="hero-title">Adopt a Storm Drain</h1>
            <p className="hero-subtitle">
              Join your community in keeping our waterways clean, one storm drain at a time
            </p>
            <div className="hero-buttons">
              <button 
                className="btn btn-primary" 
                onClick={() => navigate('/register')}
              >
                Get Started
              </button>
              <button 
                className="btn btn-secondary" 
                onClick={() => navigate('/login')}
              >
                Sign In
              </button>
            </div>
          </div>
        </div>
      </section>

      {/* About Section */}
      <section className="about-section">
        <div className="container">
          <div className="about-content">
            <div className="about-image">
              <img 
                src="/images/drain-art.jpg" 
                alt="Colorful koi fish painted on storm drain with 'Only Rain Down the Drain' message"
                className="featured-image"
              />
              <div className="image-caption">
                Art reminds us: Storm drains lead directly to our waterways
              </div>
            </div>
            <div className="about-text">
              <h2 className="section-title">What is Drain Adoption?</h2>
              <p className="section-description">
                The Drain Adoption Program empowers community members to take ownership of storm drains 
                in their neighborhoods. By adopting a drain, you commit to keeping it clear of debris, 
                preventing flooding, and protecting our local waterways from pollution.
              </p>
              <div className="key-message">
                <strong>Remember:</strong> Only rain should go down the drain. Everything else flows 
                directly into our rivers, lakes, and oceans—affecting fish, wildlife, and our drinking water.
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* How It Works Section */}
      <section className="how-it-works-section">
        <div className="container">
          <h2 className="section-title">How It Works</h2>
          <div className="steps-grid">
            <div className="step-card">
              <div className="step-icon">📍</div>
              <h3 className="step-title">1. Find a Drain</h3>
              <p className="step-description">
                Browse available storm drains on our interactive map and choose one near you
              </p>
            </div>
            
            <div className="step-card">
              <div className="step-icon">✅</div>
              <h3 className="step-title">2. Adopt It</h3>
              <p className="step-description">
                Register and claim your drain. You'll become its official caretaker
              </p>
            </div>
            
            <div className="step-card">
              <div className="step-icon">🧹</div>
              <h3 className="step-title">3. Keep It Clean</h3>
              <p className="step-description">
                Regularly check and clear debris. Report issues and track your impact
              </p>
            </div>
            
            <div className="step-card">
              <div className="step-icon">🌊</div>
              <h3 className="step-title">4. Make a Difference</h3>
              <p className="step-description">
                Help prevent flooding and protect our waterways from pollution
              </p>
            </div>
          </div>
        </div>
      </section>

      {/* Benefits Section */}
      <section className="benefits-section">
        <div className="container">
          <h2 className="section-title">Why Adopt a Drain?</h2>
          <div className="benefits-grid">
            <div className="benefit-card">
              <div className="benefit-icon">💧</div>
              <h3 className="benefit-title">Protect Water Quality</h3>
              <p className="benefit-description">
                Prevent trash and pollutants from entering our rivers, lakes, and oceans
              </p>
            </div>
            
            <div className="benefit-card">
              <div className="benefit-icon">🏘️</div>
              <h3 className="benefit-title">Reduce Flooding</h3>
              <p className="benefit-description">
                Clear drains help prevent street flooding during heavy rains
              </p>
            </div>
            
            <div className="benefit-card">
              <div className="benefit-icon">🤝</div>
              <h3 className="benefit-title">Build Community</h3>
              <p className="benefit-description">
                Connect with neighbors and work together to improve your area
              </p>
            </div>
            
            <div className="benefit-card">
              <div className="benefit-icon">🌱</div>
              <h3 className="benefit-title">Environmental Impact</h3>
              <p className="benefit-description">
                Make a real difference in protecting local ecosystems and wildlife
              </p>
            </div>
          </div>
        </div>
      </section>

      {/* Impact Stats Section */}
      <section className="stats-section">
        <div className="container">
          <h2 className="section-title">Our Community Impact</h2>
          <div className="stats-grid">
            <div className="stat-card">
              <div className="stat-number">500+</div>
              <div className="stat-label">Drains Adopted</div>
            </div>
            <div className="stat-card">
              <div className="stat-number">1,200+</div>
              <div className="stat-label">Active Volunteers</div>
            </div>
            <div className="stat-card">
              <div className="stat-number">3,000+</div>
              <div className="stat-label">Cleanups Completed</div>
            </div>
            <div className="stat-card">
              <div className="stat-number">50+</div>
              <div className="stat-label">Floods Prevented</div>
            </div>
          </div>
        </div>
      </section>

      {/* CTA Section */}
      <section className="cta-section">
        <div className="container">
          <h2 className="cta-title">Ready to Make a Difference?</h2>
          <p className="cta-description">
            Join hundreds of volunteers in keeping our community clean and safe
          </p>
          <button 
            className="btn btn-cta" 
            onClick={() => navigate('/register')}
          >
            Start Your Adoption Journey
          </button>
        </div>
      </section>

      {/* Footer */}
      <footer className="home-footer">
        <div className="container">
          <p>&copy; 2025 Drain Adoption Program. Making a difference, one drain at a time.</p>
        </div>
      </footer>
    </div>
  );
};

export default Home;
