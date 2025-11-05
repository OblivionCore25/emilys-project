import { useState, useRef, useEffect } from 'react';
import { toast } from 'react-toastify';
import './ImageUpload.css';

const ImageUpload = ({ imageUrl, onImageUpload, disabled = false }) => {
  const [uploading, setUploading] = useState(false);
  const [previewUrl, setPreviewUrl] = useState(imageUrl || '');
  const fileInputRef = useRef(null);

  const cloudName = process.env.REACT_APP_CLOUDINARY_CLOUD_NAME;
  const uploadPreset = process.env.REACT_APP_CLOUDINARY_UPLOAD_PRESET;

  // Update preview when imageUrl prop changes (for edit mode)
  useEffect(() => {
    if (imageUrl) {
      setPreviewUrl(imageUrl);
    }
  }, [imageUrl]);

  const handleFileSelect = async (e) => {
    const file = e.target.files[0];
    if (!file) return;

    // Validate file type
    if (!file.type.startsWith('image/')) {
      toast.error('Please select an image file');
      return;
    }

    // Validate file size (max 10MB)
    if (file.size > 10 * 1024 * 1024) {
      toast.error('Image size must be less than 10MB');
      return;
    }

    // Show preview immediately
    const reader = new FileReader();
    reader.onloadend = () => {
      setPreviewUrl(reader.result);
    };
    reader.readAsDataURL(file);

    // Upload to Cloudinary
    await uploadToCloudinary(file);
  };

  const uploadToCloudinary = async (file) => {
    if (!cloudName || !uploadPreset) {
      toast.error('Cloudinary configuration is missing. Please check your .env file.');
      return;
    }

    setUploading(true);

    const formData = new FormData();
    formData.append('file', file);
    formData.append('upload_preset', uploadPreset);
    formData.append('folder', 'drains'); // Organize images in a folder

    try {
      const response = await fetch(
        `https://api.cloudinary.com/v1_1/${cloudName}/image/upload`,
        {
          method: 'POST',
          body: formData,
        }
      );

      if (!response.ok) {
        throw new Error('Upload failed');
      }

      const data = await response.json();
      
      // Return the secure URL to parent component
      onImageUpload(data.secure_url);
      setPreviewUrl(data.secure_url);
      toast.success('Image uploaded successfully!');
    } catch (error) {
      toast.error('Failed to upload image. Please try again.');
      console.error('Upload error:', error);
      setPreviewUrl(imageUrl || ''); // Reset to original
    } finally {
      setUploading(false);
    }
  };

  const handleRemoveImage = () => {
    setPreviewUrl('');
    onImageUpload('');
    if (fileInputRef.current) {
      fileInputRef.current.value = '';
    }
  };

  const handleButtonClick = () => {
    fileInputRef.current?.click();
  };

  return (
    <div className="image-upload-container">
      <input
        ref={fileInputRef}
        type="file"
        accept="image/*"
        onChange={handleFileSelect}
        style={{ display: 'none' }}
        disabled={disabled || uploading}
      />

      {previewUrl ? (
        <div className="image-preview">
          <img src={previewUrl} alt="Preview" />
          <div className="image-overlay">
            <button
              type="button"
              onClick={handleButtonClick}
              className="change-button"
              disabled={disabled || uploading}
            >
              {uploading ? 'Uploading...' : 'Change Image'}
            </button>
            <button
              type="button"
              onClick={handleRemoveImage}
              className="remove-button"
              disabled={disabled || uploading}
            >
              Remove
            </button>
          </div>
        </div>
      ) : (
        <div className="upload-placeholder" onClick={handleButtonClick}>
          <div className="upload-icon">📷</div>
          <p>Click to upload image</p>
          <span className="upload-hint">
            {uploading ? 'Uploading...' : 'JPG, PNG, or GIF (max 10MB)'}
          </span>
        </div>
      )}

      {uploading && (
        <div className="upload-progress">
          <div className="progress-bar">
            <div className="progress-fill"></div>
          </div>
          <span>Uploading to cloud storage...</span>
        </div>
      )}
    </div>
  );
};

export default ImageUpload;
