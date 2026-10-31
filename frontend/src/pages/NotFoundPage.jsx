import { Link } from 'react-router-dom';

export default function NotFoundPage() {
  return (
    <div className="error-page">
      <div className="error-code">404</div>
      <h2>Page Not Found</h2>
      <p>The page you're looking for doesn't exist or has been moved.</p>
      <Link to="/" className="btn btn-primary">Go Home</Link>
    </div>
  );
}

