import { useEffect, useState } from "react";
import { useBlockchain } from "../contexts/BlockchainContext";
import { useNavigate } from "react-router-dom";
import axios from "axios";

function BiddedProjects({user})
{
    const [projects,setProjects] = useState([]);
    const {account} = useBlockchain();
    const navigate = useNavigate();

    useEffect(()=>{
        if(!account || !user)
        {
            navigate("/");
        }

        const fetchProjects = async () => {
            try {
                const response = await axios.get("http://localhost:8080/api/user/bidded-projects", { withCredentials: true });
                setProjects(response.data);
            } catch (error) {
                console.error("Failed to fetch projects", error);
            }
        };
        fetchProjects();
    },[account,navigate,user]);
    
    return (<div className="p-3 m-3">
        <h2 className="text-xl font-bold text-sky-400 mb-4">Bidded Projects</h2>
        <ul className="space-y-3">
            {projects.map((project) => (
                <div key={project.id} className="p-6  rounded-lg  border-2 border-gray-700 shadow-white">
                <div className="flex justify-between items-center text-sky-400">
                    <div>
                        <h3 className="text-lg font-bold">{project.name}</h3>
                        <p className="text-cyan-500">{project.detail}</p>
                        <p className="text-sm text-blue-400 mt-1">Category: {project.category}</p>
                    </div>
                    <button
                        className=" mx-3 ring-2 ring-blue-600   insert-ring-2 insert-ring-blue-400 hover:bg-gray-700   text-white font-bold py-2 px-6 rounded-lg text-lg shadow-[0_0_5px_#06B6D4,0_0_10px_#06B6D4,0_0_20px_#06B6D4]"
                        onClick={() => {
                            if(project.status == "HIRING"){
                                navigate("/projectdetail", { state: project });
                            }
                            else
                            {
                                navigate("/ongoingproject", { state: project });
                            }
                            
                        }

                        }
                    >
                        Detail
                    </button>
                </div>
            </div>
            ))}
        </ul>
    </div>);
}

export default BiddedProjects;