import { BrowserRouter as Router, Route, Routes } from "react-router-dom";
import { BlockchainProvider } from "./contexts/BlockchainContext";
import Home from "./pages/Home";
import UserFeed from "./pages/UserFeed";
import Register from "./pages/Register";
import ProjectDetail from "./pages/ProjectDetail";
import OngoingProject from "./pages/OngoingProject";
import UserProfile from "./pages/UserProfile";
import GiveRatingPage from "./pages/GiveRating";


function App() {
  return (
    <BlockchainProvider>
    <Router>
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/userfeed" element={<UserFeed />} />
        <Route path="/register" element = {<Register/>} />
        <Route path="/projectdetail" element = {<ProjectDetail/>} />
        <Route path="/ongoingproject" element = {<OngoingProject/>}/>
        <Route path="/userprofile" element = {<UserProfile/>}/>
        <Route path="/giverating" element = {<GiveRatingPage/>}/>
      </Routes>
    </Router>
    </BlockchainProvider>
  );
}

export default App;
