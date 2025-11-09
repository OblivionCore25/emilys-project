import { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { toast } from 'react-toastify';
import ImageUpload from './ImageUpload';
import API_BASE_URL from '../config/api';
import './CommentSection.css';

const CommentSection = ({ drainId, isAdopted, isAdopter }) => {
  const [comments, setComments] = useState([]);
  const [commentText, setCommentText] = useState('');
  const [commentImage, setCommentImage] = useState('');
  const [loading, setLoading] = useState(false);
  const { getToken, getUserId, isAdmin } = useAuth();

  useEffect(() => {
    fetchComments();
  }, [drainId]);

  const fetchComments = async () => {
    try {
      const response = await fetch(`${API_BASE_URL}/api/drains/${drainId}/comments`);
      if (response.ok) {
        const data = await response.json();
        setComments(data);
      }
    } catch (error) {
      console.error('Error fetching comments:', error);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!commentText.trim()) {
      toast.error('Please enter a comment');
      return;
    }

    setLoading(true);
    const token = getToken();
    const userId = getUserId();

    try {
      const response = await fetch(
        `${API_BASE_URL}/api/drains/${drainId}/comments?userId=${userId}`,
        {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
          },
          body: JSON.stringify({
            text: commentText,
            imageUrl: commentImage || null
          })
        }
      );

      if (response.ok) {
        toast.success('Comment added successfully!');
        setCommentText('');
        setCommentImage('');
        fetchComments(); // Refresh comments
      } else {
        const error = await response.text();
        toast.error(error || 'Failed to add comment');
      }
    } catch (error) {
      console.error('Error adding comment:', error);
      toast.error('Failed to add comment');
    } finally {
      setLoading(false);
    }
  };

  const handleDeleteComment = async (commentId) => {
    if (!window.confirm('Are you sure you want to delete this comment?')) {
      return;
    }

    const token = getToken();

    try {
      const response = await fetch(
        `${API_BASE_URL}/api/drains/${drainId}/comments/${commentId}`,
        {
          method: 'DELETE',
          headers: {
            'Authorization': `Bearer ${token}`
          }
        }
      );

      if (response.ok) {
        toast.success('Comment deleted successfully!');
        fetchComments(); // Refresh comments
      } else {
        toast.error('Failed to delete comment');
      }
    } catch (error) {
      console.error('Error deleting comment:', error);
      toast.error('Failed to delete comment');
    }
  };

  const handleImageUpload = (url) => {
    setCommentImage(url);
  };

  if (!isAdopted) {
    return null;
  }

  return (
    <div className="comment-section">
      <h3>Updates & Comments</h3>
      
      {/* Add Comment Form (only for adopter) */}
      {isAdopter && (
        <form onSubmit={handleSubmit} className="comment-form">
          <textarea
            value={commentText}
            onChange={(e) => setCommentText(e.target.value)}
            placeholder="Share an update about this drain..."
            rows="4"
            required
          />
          
          <ImageUpload 
            onImageUpload={handleImageUpload}
            currentImageUrl={commentImage}
            label="Add a photo (optional)"
          />

          <button type="submit" disabled={loading} className="submit-comment-btn">
            {loading ? 'Posting...' : 'Post Update'}
          </button>
        </form>
      )}

      {/* Comments List */}
      <div className="comments-list">
        {comments.length === 0 ? (
          <p className="no-comments">No updates yet. Be the first to share!</p>
        ) : (
          comments.map((comment) => (
            <div key={comment.id} className="comment-card">
              <div className="comment-header">
                <div className="comment-author">
                  <strong>{comment.userName}</strong>
                  <span className="comment-date">
                    {new Date(comment.createdAt).toLocaleDateString('en-US', {
                      year: 'numeric',
                      month: 'long',
                      day: 'numeric',
                      hour: '2-digit',
                      minute: '2-digit'
                    })}
                  </span>
                </div>
                {isAdmin() && (
                  <button
                    onClick={() => handleDeleteComment(comment.id)}
                    className="delete-comment-btn"
                    title="Delete comment"
                  >
                    🗑️ Delete
                  </button>
                )}
              </div>
              
              <p className="comment-text">{comment.text}</p>
              
              {comment.imageUrl && (
                <div className="comment-image">
                  <img src={comment.imageUrl} alt="Update" />
                </div>
              )}
            </div>
          ))
        )}
      </div>
    </div>
  );
};

export default CommentSection;
