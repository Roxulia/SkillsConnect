import { useBlockchain } from "../contexts/BlockchainContext";
import { useNavigate,useLocation } from "react-router-dom";
import { BrowserProvider, Contract, parseEther } from "ethers";
import { useEffect ,useState } from "react";
import axios from "axios";

function UserProfile()
{
    const location = useLocation();
    const data = location.state;
    const navigate = useNavigate();
    const {account} = useBlockchain();
    const [user,setUser] = useState(null);
    const [viewer,setViewer] = useState(null);

    useEffect(()=>{
        if(!account || !data)
        {
            navigate("/");
        }

        axios.get("http://localhost:8080/api/user/me", { withCredentials: true })
                .then(response => {
                    console.log(response.data);
                setViewer(response.data);
                })
                .catch(error => console.error("Error fetching user data:", error));

        axios.get(`http://localhost:8080/api/user/${data.uid}`, {
            withCredentials: true,
          })
          .then((res) => {
            //console.log(res.data);    
            setUser(res.data);
          } )
          .catch(error => alert(`Error fetching project: ` + error.response.data));
    },[account,navigate,data]);

    if(!user){
        return (<div>loading...</div>);
    }

    return (<div className="max-w-2xl mx-auto p-6 bg-white shadow-lg rounded-2xl border border-gray-200">
        <h2 className="text-3xl font-extrabold text-blue-700 text-center mb-4">{user.name}'s Profile</h2>
        
        <div className="space-y-3 text-gray-800">
            <p><strong className="text-blue-600">Name:</strong> {user.name}</p>
            <p><strong className="text-blue-600">Email:</strong> {user.email}</p>
            <p><strong className="text-blue-600">Phone:</strong> {user.phone}</p>
            <p><strong className="text-blue-600">Skills:</strong> {user.skills?.join(", ") || "No skills listed"}</p>
        </div>
    
        <div className="mt-5">
            <h3 className="text-xl font-semibold text-blue-600 mb-2">Finished Projects</h3>
            {user.finishedProjects && user.finishedProjects.length > 0 ? (
                <ul className="space-y-4">
                    {user.finishedProjects.map((project) => (
                        <li key={project.id} className="p-4 bg-gray-100 rounded-lg border border-gray-300">
                            <h4 className="text-lg font-bold text-gray-900">{project.name}</h4>
                            <span className="text-sm text-blue-500">{project.category}</span>
                            <p className="text-gray-600 mt-1">{project.detail}</p>
                        </li>
                    ))}
                </ul>
            ) : (
                <p className="text-gray-500 italic">No projects finished</p>
            )}
        </div>
    
        <div className="mt-6">
            <button 
                onClick={() => navigate("/userfeed", { state: viewer })} 
                className="w-full bg-gradient-to-r from-blue-500 to-blue-700 text-white font-semibold py-3 rounded-xl shadow-md hover:shadow-lg transition duration-300"
            >
                Back to User Feed
            </button>
        </div>
    </div>
    );
}

export default UserProfile;