export default function Spinner({ size = 40, text = 'Loading…' }) {
  return (
    <div className="spinner-wrapper">
      <div
        className="spinner"
        style={{ width: size, height: size }}
        role="status"
        aria-label="Loading"
      />
      {text && <p className="spinner-text">{text}</p>}
    </div>
  );
}

