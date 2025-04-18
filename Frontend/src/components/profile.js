import { useBlockchain } from "../contexts/BlockchainContext";
import { useNavigate } from "react-router-dom";
import { useEffect ,useState } from "react";
import axios from "axios";

function MyProfile({ user }) {

    const {account,disconnectWallet} = useBlockchain();
    const navigate = useNavigate();
    const editInfo = () =>{
        navigate("/register");
    }
    const logout =() =>{
        axios.post("http://localhost:8080/api/user/logout",{withCredential : true})
        .then((res) => {
            disconnectWallet();
            navigate('/');
        })
        .catch(error => alert('Log out error'));
    }

    if (!user) {
        return <div className="text-blue-500">Loading profile...</div>;
    }

    return (
        <div className="p-3">
            <h2 className="text-3xl font-extrabold text-blue-500 text-center mb-4">{user.name}'s Profile</h2>
        
        <div className="space-y-3 text-sky-400">
            <p><strong className="text-blue-500">Name:</strong> {user.name}</p>
            <p><strong className="text-blue-500">Email:</strong> {user.email}</p>
            <p><strong className="text-blue-500">Phone:</strong> {user.phone}</p>
            {/*<p><strong className="text-blue-600">Rating:</strong> ⭐ {user.rating}</p>*/}
            <p><strong className="text-blue-500">Skills:</strong> {user.skills?.join(", ") || "No skills listed"}</p>
        </div>
    
        <div className="mt-5">
            <h3 className="text-xl font-semibold text-blue-600 mb-2">Finished Projects</h3>
            {user.finishedProjects && user.finishedProjects.length > 0 ? (
                <ul className="space-y-4">
                    {user.finishedProjects.map((project) => (
                        <div key={project.id} className="p-6  rounded-lg  border-2 border-gray-700 shadow-white">
                        <div className="flex justify-between items-center text-sky-400">
                            <div>
                                <h3 className="text-lg font-bold">{project.name}</h3>
                                <p className="text-cyan-500">{project.detail}</p>
                                <p className="text-sm text-blue-400 mt-1">Category: {project.category}</p>
                            </div>
                        </div>
                    </div>
                    ))}
                </ul>
            ) : (
                <p className="text-sky-500 italic">No projects finished</p>
            )}
        </div>
        <div className="grid grid-cols-2 gap-4">
                <button 
            onClick={editInfo} 
            className="m-3 w-full ring-2 ring-blue-500 shadow-[0_0_5px_#06B6D4,0_0_10px_#06B6D4,0_0_20px_#06B6D4] text-white py-2 rounded-lg hover:bg-gray-700 transition"
            >
            Edit Info
            </button>
            <button 
            onClick={logout} 
            className="m-3 w-full ring-2 ring-red-500 shadow-[0_0_5px_#D46606,0_0_10px_#D46606,0_0_20px_#D46606] text-white py-2 rounded-lg hover:bg-gray-700 transition"
            >
            Logout
            </button>
        </div>
        </div>
    );
}

export default MyProfile;