import "./Header.css";

const Header = () => {
  return (
    <span onClick={() => window.scroll(0, 0)} className="header">
      Netflix Recommendation App
    </span>
  );
};

export default Header;