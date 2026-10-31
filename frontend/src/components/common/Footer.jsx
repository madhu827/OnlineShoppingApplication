export default function Footer() {
  return (
    <footer className="footer">
      <div className="footer-container">
        <p className="footer-brand">🛒 ShopApp</p>
        <p className="footer-copy">
          &copy; {new Date().getFullYear()} Online Shopping — Built with React &amp;
          Spring Boot
        </p>
        <div className="footer-stack">
          <span className="badge">React 18</span>
          <span className="badge">Spring Boot 3.5</span>
          <span className="badge">MongoDB</span>
          <span className="badge">Cassandra</span>
        </div>
      </div>
    </footer>
  );
}

