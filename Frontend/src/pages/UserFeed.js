import { useBlockchain } from "../contexts/BlockchainContext";
import { useNavigate,useLocation } from "react-router-dom";
import { useEffect ,useState } from "react";
import axios from "axios";
import ViewHiringProjects from "../components/projects";
import CreateProject from "../components/createProject";
import MyProfile from "../components/profile";
import AcceptedProjects from "../components/acceptedProjects";
import BiddedProjects from "../components/biddedProjects";

function UserFeed() {
    const location = useLocation();
    const {account} = useBlockchain();
    const navigate = useNavigate();
    const data = location.state;
    const [user,setUser] = useState(null);
    const [activeTab,setActiveTab] = useState("view");

    useEffect(()=>{
        if(!account){
            navigate("/");
        }
        else
        {
            axios.get("http://localhost:8080/api/user/me", { withCredentials: true })
                .then(response => {
                setUser(response.data);
                })
                .catch(error => console.error("Error fetching user data:", error));
        }
    },[account,navigate])

    return (<div className="flex-col  bg-neutral-950 h-screen">
      <div className="flex justify-between border-b border-cyan-500">
        {["view", "accepted", "bidded", "create", "profile"].map((tab) => (
          <button
            key={tab}
            onClick={() => setActiveTab(tab)}
            className={`w-1/5 py-3 text-center text-lg font-semibold transition-all duration-300 ${
              activeTab === tab
                ? "border-b-4 border-cyan-500 text-neutral-200 shadow-[0_0_5px_#06B6D4,0_0_10px_#06B6D4]"
                : "text-neutral-200 hover:text-cyan-300"
            } hover:shadow-[0_0_5px_#06B6D4,0_0_10px_#06B6D4,0_0_20px_#06B6D4] bg-cyan-900/20  rounded-t-lg transition`}
          >
            {tab === "view" && "View Projects"}
            {tab === "accepted" && "Accepted Projects"}
            {tab === "bidded" && "Bidded Projects"}
            {tab === "create" && "Create Project"}
            {tab === "profile" && "Profile"}
          </button>
        ))}
      </div>

      {/* Tab Content */}
      <div className="p-4 bg-neutral-950">
        {activeTab === "view" && <ViewHiringProjects user={user} />}
        {activeTab === "accepted" && <AcceptedProjects user={user} />}
        {activeTab === "bidded" && <BiddedProjects user={user} />}
        {activeTab === "create" && <CreateProject user={user} />}
        {activeTab === "profile" && <MyProfile user={user} />}
      </div>
    </div>);
}

export default UserFeed;